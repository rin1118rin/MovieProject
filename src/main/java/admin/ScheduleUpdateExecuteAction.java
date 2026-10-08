package admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
import dao.ScheduleDao;
import tool.Action;

public class ScheduleUpdateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        request.setCharacterEncoding("UTF-8");

        int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
        int movieId = Integer.parseInt(request.getParameter("movieId"));
        int screenNo = Integer.parseInt(request.getParameter("screenNo"));
        String startDatetimeStr = request.getParameter("startDatetime");
        String endDatetimeStr = request.getParameter("endDatetime");
        String screeningFormat = request.getParameter("screeningFormat");

        SimpleDateFormat sdf = new SimpleDateFormat(startDatetimeStr.contains("T") ? "yyyy-MM-dd'T'HH:mm" : "yyyy-MM-dd HH:mm");
        Date startDatetime = sdf.parse(startDatetimeStr);
        Date endDatetime = sdf.parse(endDatetimeStr);

        ScheduleDao scheduleDao = new ScheduleDao();

        // 自分以外のスケジュールとの時間帯重複チェック
        if (scheduleDao.isOverlapped(screenNo, startDatetime, endDatetime, scheduleId)) {
            MovieDao movieDao = new MovieDao();
            List<Movie> movieList = movieDao.getAll();

            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setMovieId(movieId);
            schedule.setScreenNo(screenNo);
            schedule.setStartDatetime(startDatetime);
            schedule.setEndDatetime(endDatetime);
            schedule.setScreeningFormat(screeningFormat);

            request.setAttribute("schedule", schedule);
            request.setAttribute("movieList", movieList);
            request.setAttribute("errorMessage", "指定したスクリーンの時間帯にすでに別の上映スケジュールが存在します。");
            request.getRequestDispatcher("/admin/schedules/edit.jsp").forward(request, response);
            return;
        }

        Schedule schedule = new Schedule();
        schedule.setScheduleId(scheduleId);
        schedule.setMovieId(movieId);
        schedule.setScreenNo(screenNo);
        schedule.setStartDatetime(startDatetime);
        schedule.setEndDatetime(endDatetime);
        schedule.setScreeningFormat(screeningFormat);

        boolean isSuccess = scheduleDao.update(schedule);

        if (isSuccess) {
            request.setAttribute("message", "スケジュールを更新しました。");
            request.getRequestDispatcher("/admin/schedules/list.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "スケジュールの更新に失敗しました。");
            request.getRequestDispatcher("/admin/schedules/edit.jsp").forward(request, response);
        }
    }
}