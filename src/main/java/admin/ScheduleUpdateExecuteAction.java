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
        
        // JSPから送られてくるscreeningDateとstartTimeを受け取る
        String screeningDate = request.getParameter("screeningDate"); // 例: "2026-10-14"
        String startTime = request.getParameter("startTime");         // 例: "13:00"
        
        String endDatetimeStr = request.getParameter("endDatetime");
        String screeningFormat = request.getParameter("screeningFormat");

        // 日付と時刻を結合
        String startDatetimeStr = screeningDate + " " + startTime;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDatetime = sdf.parse(startDatetimeStr);
        
        if (endDatetimeStr != null && endDatetimeStr.contains("T")) {
            endDatetimeStr = endDatetimeStr.replace("T", " ");
        }
        Date endDatetime = (endDatetimeStr != null && !endDatetimeStr.isEmpty()) 
                           ? sdf.parse(endDatetimeStr) 
                           : new Date(startDatetime.getTime() + (2 * 60 * 60 * 1000)); // デフォルト2後など

        ScheduleDao scheduleDao = new ScheduleDao();

        // スケジュールとの時間帯重複チェック
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
            request.getRequestDispatcher("/admin/schedule/edit.jsp").forward(request, response);
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
            // 一覧へリダイレクトまたはフォワード
            response.sendRedirect(request.getContextPath() + "/admin/scheduleList.action");
        } else {
            MovieDao movieDao = new MovieDao();
            List<Movie> movieList = movieDao.getAll();
            request.setAttribute("schedule", schedule);
            request.setAttribute("movieList", movieList);
            request.setAttribute("errorMessage", "スケジュールの更新に失敗しました。");
            request.getRequestDispatcher("/admin/schedule/edit.jsp").forward(request, response);
        }
    }
}