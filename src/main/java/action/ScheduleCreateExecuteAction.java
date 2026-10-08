package action;

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
        String startDatetimeStr = request.getParameter("startDatetime"); // 例: "2026-10-08T10:00" または "2026-10-08 10:00"
        String endDatetimeStr = request.getParameter("endDatetime");
        String screeningFormat = request.getParameter("screeningFormat");

        int movieId = Integer.parseInt(movieIdStr);
        int screenNo = Integer.parseInt(screenNoStr);

        // HTML5の datetime-local 形式と標準形式の両方に対応
        SimpleDateFormat sdf = new SimpleDateFormat(startDatetimeStr.contains("T") ? "yyyy-MM-dd'T'HH:mm" : "yyyy-MM-dd HH:mm");
        Date startDatetime = sdf.parse(startDatetimeStr);
        Date endDatetime = sdf.parse(endDatetimeStr);

        ScheduleDao scheduleDao = new ScheduleDao();

        // 時間帯の重複チェック
        if (scheduleDao.isOverlapped(screenNo, startDatetime, endDatetime)) {
            MovieDao movieDao = new MovieDao();
            List<Movie> movieList = movieDao.getAll();

            request.setAttribute("movieList", movieList);
            request.setAttribute("errorMessage", "指定したスクリーンの時間帯にすでに別の上映スケジュールが存在します。");
            request.getRequestDispatcher("/admin/schedules/create.jsp").forward(request, response);
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
            request.setAttribute("message", "スケジュールを登録しました。");
            request.getRequestDispatcher("/admin/schedules/list.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "スケジュールの登録に失敗しました。");
            request.getRequestDispatcher("/admin/schedules/create.jsp").forward(request, response);
        }
    }
}