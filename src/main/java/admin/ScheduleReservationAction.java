package admin;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Reservation;
import bean.Schedule;
import dao.MovieDao;
import dao.ReservationDao;
import dao.ScheduleDao;
import tool.Action;

public class ScheduleReservationAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String scheduleIdStr = request.getParameter("scheduleId");

        if (scheduleIdStr != null && !scheduleIdStr.trim().isEmpty()) {
            int scheduleId = Integer.parseInt(scheduleIdStr.trim());

            ScheduleDao scheduleDao = new ScheduleDao();
            Schedule schedule = scheduleDao.get(scheduleId);

            if (schedule != null) {
                // 映画タイトルをMovieDaoから取得してセット
                MovieDao movieDao = new MovieDao();
                Movie movie = movieDao.get(schedule.getMovieId());
                if (movie != null) {
                    schedule.setMovieTitle(movie.getTitle());
                } else {
                    schedule.setMovieTitle("（不明な作品）");
                }

                // 総座席数（例として30席。必要に応じて変更してください）
                schedule.setTotalSeats(30);

                // データベースからこのスケジュールの予約一覧を取得
                ReservationDao reservationDao = new ReservationDao();
                List<Reservation> reservations = reservationDao.getReservationsByScheduleId(scheduleId);

                // 予約済み座席数を実際の予約リスト（座席数）から算出
                int reservedCount = 0;
                for (Reservation r : reservations) {
                    if (r.getSeats() != null) {
                        reservedCount += r.getSeats().size();
                    }
                }
                schedule.setReservedSeats(reservedCount);

                request.setAttribute("schedule", schedule);
                request.setAttribute("reservations", reservations);
                
                request.getRequestDispatcher("/admin/schedule/reservations.jsp").forward(request, response);
                return;
            }
        }

        request.setAttribute("errorMessage", "指定されたスケジュールが見つかりませんでした。");
        request.getRequestDispatcher("/admin/schedule/list.jsp").forward(request, response);
    }
}