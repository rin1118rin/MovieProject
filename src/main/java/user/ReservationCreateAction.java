
package user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Schedule;
import dao.MovieDao;
import dao.ReservationSeatDao;
import dao.ScheduleDao;
import tool.Action;

public class ReservationCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest req,
                        HttpServletResponse res) throws Exception {

        MovieDao movieDao = new MovieDao();
        ScheduleDao scheduleDao = new ScheduleDao();

        req.setAttribute("movies", movieDao.getAll());

        // 今日から7日分の上映回
        List<Schedule> schedules = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            schedules.addAll(
                scheduleDao.getByDate(
                    java.sql.Date.valueOf(today.plusDays(i))
                )
            );
        }

        Schedule selected = null;
        String scheduleIdStr = req.getParameter("scheduleId");

        if (scheduleIdStr != null && !scheduleIdStr.isBlank()) {
            int scheduleId;

            try {
                scheduleId = Integer.parseInt(scheduleIdStr.trim());
            } catch (NumberFormatException e) {
                res.sendError(400, "上映回の指定が不正です。");
                return;
            }

            selected = scheduleDao.get(scheduleId);

            if (selected == null) {
                res.sendError(404, "上映回が見つかりません。");
                return;
            }

            boolean included = schedules.stream().anyMatch(
                s -> s.getScheduleId() == scheduleId
            );

            if (!included) {
                schedules.add(selected);
            }

            req.setAttribute("schedule", selected);
            req.setAttribute("selectedMovieId", selected.getMovieId());
            req.setAttribute(
                "selectedScheduleId", selected.getScheduleId()
            );
        }

        req.setAttribute("schedules", schedules);

        // 選択した上映回の予約済み座席
        Set<String> reservedSeatNos = new HashSet<>();

        if (selected != null) {
            ReservationSeatDao seatDao = new ReservationSeatDao();

            reservedSeatNos.addAll(
                seatDao.getReservedSeatNos(selected.getScheduleId())
            );
        }

        // 全スクリーン共通：A〜E列、各6席
        List<Map<String, Object>> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'E'; row++) {
            for (int number = 1; number <= 6; number++) {
                String seatNo = String.valueOf(row) + number;

                Map<String, Object> seat = new LinkedHashMap<>();
                seat.put("seatNo", seatNo);
                seat.put("reserved", reservedSeatNos.contains(seatNo));

                seats.add(seat);
            }
        }

        req.setAttribute("seats", seats);
        req.setAttribute("seatSelectionReady", selected != null);

        req.getRequestDispatcher("/user/reservation/create.jsp")
           .forward(req, res);
    }
}