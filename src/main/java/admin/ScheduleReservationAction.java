package admin;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
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

                // 総座席数・予約済み数のセット　←必要に応じて変更
                schedule.setTotalSeats(100);
                schedule.setReservedSeats(0); // 予約リストの件数等から算出する場合はここで設定

                List<Object> reservations = new ArrayList<>(); // データがない場合は空のリスト

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