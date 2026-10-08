package action;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
import dao.ScheduleDao;
import tool.Action;

public class ScheduleUpdateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String scheduleIdStr = request.getParameter("scheduleId");

        if (scheduleIdStr != null && !scheduleIdStr.trim().isEmpty()) {
            int scheduleId = Integer.parseInt(scheduleIdStr.trim());

            ScheduleDao scheduleDao = new ScheduleDao();
            Schedule schedule = scheduleDao.get(scheduleId);

            if (schedule != null) {
                MovieDao movieDao = new MovieDao();
                List<Movie> movieList = movieDao.getAll();

                request.setAttribute("schedule", schedule);
                request.setAttribute("movieList", movieList);
                request.getRequestDispatcher("/admin/schedules/edit.jsp").forward(request, response);
                return;
            }
        }

        request.setAttribute("errorMessage", "指定されたスケジュールが見つかりませんでした。");
        request.getRequestDispatcher("/admin/schedule/list.jsp").forward(request, response);
    }
}