package user;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
import dao.ReservationRegisterDao;
import dao.ScheduleDao;
import tool.Action;

public class ReservationCreateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        // 保存後の完了画面を表示
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(false);

            Object completed = session == null
                    ? null
                    : session.getAttribute("completedReservation");

            if (completed == null) {
                response.sendRedirect(
                    request.getContextPath()
                    + "/user/reservationCreate.action"
                );
                return;
            }

            response.setHeader("Cache-Control", "no-store");
            request.setAttribute("reservation", completed);

            request.getRequestDispatcher("/user/reservation/complete.jsp")
                   .forward(request, response);
            return;
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET, POST");
            response.sendError(405);
            return;
        }

        request.setCharacterEncoding("UTF-8");

        int scheduleId;
        int movieId;
        int[] quantities;
        List<String> seats;

        Map<String, Object> reservation = new LinkedHashMap<>();

        // 入力内容を確認
        try {
            scheduleId = Integer.parseInt(
                required(request, "scheduleId", 10)
            );

            movieId = Integer.parseInt(
                required(request, "movieId", 10)
            );

            quantities = new int[] {
                count(request, "adult"),
                count(request, "student"),
                count(request, "senior")
            };

            String[] seatValues = request.getParameterValues("seat");

            if (seatValues == null || seatValues.length == 0) {
                throw new IllegalArgumentException(
                    "座席を選択してください。"
                );
            }

            seats = new ArrayList<>(Arrays.asList(seatValues));

            if (seats.stream().distinct().count() != seats.size()) {
                throw new IllegalArgumentException(
                    "座席が重複しています。"
                );
            }

            for (String seat : seats) {
                if (seat == null || !seat.matches("[A-F](10|[1-9])")) {
                    throw new IllegalArgumentException(
                        "座席番号が不正です。"
                    );
                }
            }

            int ticketCount =
                quantities[0] + quantities[1] + quantities[2];

            if (ticketCount == 0 || ticketCount != seats.size()) {
                throw new IllegalArgumentException(
                    "チケット枚数と座席数を一致させてください。"
                );
            }

            String payment = required(request, "payment", 20);

            if (!payment.equals("counter") && !payment.equals("card")) {
                throw new IllegalArgumentException(
                    "支払い方法が不正です。"
                );
            }

            reservation.put(
                "customerName", required(request, "customerName", 100)
            );
            reservation.put(
                "customerKana", required(request, "customerKana", 100)
            );
            reservation.put(
                "email", required(request, "email", 255)
            );
            reservation.put(
                "phone", required(request, "phone", 20)
            );
            reservation.put("paymentMethod", payment);

        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
            return;
        }

        // 映画と上映回の組み合わせを確認
        Schedule schedule = new ScheduleDao().get(scheduleId);

        if (schedule == null || schedule.getMovieId() != movieId) {
            response.sendError(
                400, "映画と上映回の組み合わせが不正です。"
            );
            return;
        }

        if (schedule.getStartDatetime() == null
                || !schedule.getStartDatetime().after(new java.util.Date())) {
            response.sendError(
                400, "上映開始後の予約はできません。"
            );
            return;
        }

        Movie movie = new MovieDao().get(movieId);

        if (movie == null) {
            response.sendError(404, "映画が見つかりません。");
            return;
        }

        // 金額はサーバー側で計算する
        int totalPrice =
            quantities[0] * 1900
            + quantities[1] * 1500
            + quantities[2] * 1200;

        reservation.put("scheduleId", scheduleId);
        reservation.put("totalPrice", totalPrice);

        // 予約・座席・券種をDBに保存
        int reservationId;

        try {
            ReservationRegisterDao dao = new ReservationRegisterDao();

            reservationId = dao.register(
                reservation, seats, quantities
            );

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                response.sendError(
                    409,
                    "選択した座席は予約済みです。予約画面で選び直してください。"
                );
                return;
            }

            throw e;
        }

        // 完了画面で表示する情報
        reservation.put("reservationId", reservationId);
        reservation.put("movieTitle", movie.getTitle());
        reservation.put(
            "showtime",
            new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(schedule.getStartDatetime())
        );
        reservation.put("seats", String.join("・", seats));

        List<String> ticketInfo = new ArrayList<>();
        String[] types = { "一般", "学生", "シニア" };

        for (int i = 0; i < types.length; i++) {
            if (quantities[i] > 0) {
                ticketInfo.add(
                    types[i] + " × " + quantities[i] + "枚"
                );
            }
        }

        reservation.put(
            "ticketInfo", String.join(" / ", ticketInfo)
        );

        request.getSession().setAttribute(
            "completedReservation", reservation
        );

        // 完了画面の再読み込みで再登録されないようにする
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader(
            "Location",
            response.encodeRedirectURL(
                request.getContextPath()
                + "/user/reservationCreateExecute.action"
            )
        );
    }

    private String required(HttpServletRequest request,
                            String name, int maxLength) {

        String value = request.getParameter(name);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "必須項目が入力されていません：" + name
            );
        }

        value = value.trim();

        if (value.length() > maxLength) {
            throw new IllegalArgumentException(
                "入力が長すぎます：" + name
            );
        }

        return value;
    }

    private int count(HttpServletRequest request, String name) {
        int value = Integer.parseInt(
            required(request, name, 2)
        );

        if (value < 0 || value > 4) {
            throw new IllegalArgumentException(
                "チケット枚数が不正です。"
            );
        }

        return value;
    }
}