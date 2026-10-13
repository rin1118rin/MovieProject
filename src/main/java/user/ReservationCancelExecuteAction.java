package user;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.ReservationDao;
import tool.Action;

public class ReservationCancelExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        // 取消後の完了画面
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(false);

            Object reservation = session == null
                ? null
                : session.getAttribute("canceledReservation");

            if (reservation == null) {
                response.sendRedirect(
                    request.getContextPath()
                    + "/user/reservationCancel.action"
                );
                return;
            }

            response.setHeader("Cache-Control", "no-store");
            request.setAttribute("reservation", reservation);

            request.getRequestDispatcher(
                "/user/reservation/cancel-complete.jsp"
            ).forward(request, response);
            return;
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET, POST");
            response.sendError(405);
            return;
        }

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        String token = request.getParameter("cancelToken");

        if (session == null || token == null
                || !token.equals(session.getAttribute("cancelToken"))) {
            response.sendError(
                403, "予約取消画面を開き直してください。"
            );
            return;
        }

        int reservationId;
        String email = request.getParameter("email");

        try {
            String id = request.getParameter("reservationId");

            if (id == null || !id.trim().matches("[0-9]{1,10}")) {
                throw new IllegalArgumentException();
            }

            // 000001も1として読み込む
            reservationId = Integer.parseInt(id.trim());

            if (reservationId <= 0 || email == null
                    || email.isBlank() || email.trim().length() > 255) {
                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException e) {
            showError(
                request, response,
                "予約番号とメールアドレスを正しく入力してください。"
            );
            return;
        }

        ReservationDao dao = new ReservationDao();

        String movieTitle = dao.getMovieTitle(
            reservationId, email.trim()
        );

        if (movieTitle == null) {
            showError(
                request, response,
                "予約番号・メールアドレスを確認してください。"
            );
            return;
        }

        boolean result = dao.cancel(
            reservationId, email.trim()
        );

        if (!result) {
            showError(
                request, response,
                "予約番号・メールアドレスが一致しないか、すでに取消済みです。"
            );
            return;
        }

        Map<String, Object> reservation = new LinkedHashMap<>();
        reservation.put("reservationId", reservationId);
        reservation.put("movieTitle",movieTitle);

        session.removeAttribute("cancelToken");
        session.setAttribute("canceledReservation", reservation);

        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader(
            "Location",
            response.encodeRedirectURL(
                request.getContextPath()
                + "/user/reservationCancelExecute.action"
            )
        );
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message) throws Exception {

        request.setAttribute("errorMessage", message);
        request.setAttribute(
            "cancelToken",
            request.getSession().getAttribute("cancelToken")
        );

        request.getRequestDispatcher("/user/reservation/cancel.jsp")
               .forward(request, response);
    }
}