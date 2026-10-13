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

public class ScheduleCreateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        request.setCharacterEncoding("UTF-8");

        String movieIdStr = request.getParameter("movieId");
        String screenNoStr = request.getParameter("screenNo");
        String screeningDate = request.getParameter("screeningDate"); // 例: "2026-10-08"
        String startTime = request.getParameter("startTime");         // 例: "10:00"
        String screeningFormat = request.getParameter("screeningFormat");

        int movieId = Integer.parseInt(movieIdStr);
        int screenNo = Integer.parseInt(screenNoStr);

        // 日付と時間を結合
        String startDatetimeStr = screeningDate + " " + startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDatetime = sdf.parse(startDatetimeStr);

        long twoHoursLater = startDatetime.getTime() + (2L * 60 * 60 * 1000);
        Date endDatetime = new Date(twoHoursLater);

        ScheduleDao scheduleDao = new ScheduleDao();

        // 時間帯の重複チェック
        if (scheduleDao.isOverlapped(screenNo, startDatetime, endDatetime)) {
            MovieDao movieDao = new MovieDao();
            List<Movie> movieList = movieDao.getAll();

            request.setAttribute("movies", movieList);
            request.setAttribute("errorMessage", "指定したスクリーンの時間帯にすでに別の上映スケジュールが存在します。");
            request.getRequestDispatcher("/admin/schedule/create.jsp").forward(request, response);
            return;
        }

        Schedule schedule = new Schedule();
        schedule.setMovieId(movieId);
        schedule.setScreenNo(screenNo);
        schedule.setStartDatetime(startDatetime);
        schedule.setEndDatetime(endDatetime);
        schedule.setScreeningFormat(screeningFormat);

        boolean isSuccess = scheduleDao.save(schedule);

        if (isSuccess) {
            // 登録成功時、登録した日付をパラメータにつけて一覧画面へリダイレクト
            response.sendRedirect(request.getContextPath() + "/admin/scheduleList.action?date=" + screeningDate);
        } else {
            MovieDao movieDao = new MovieDao();
            List<Movie> movieList = movieDao.getAll();
            request.setAttribute("movies", movieList);
            request.setAttribute("errorMessage", "スケジュールの登録に失敗しました。");
            request.getRequestDispatcher("/admin/schedule/create.jsp").forward(request, response);
        }
    }
}