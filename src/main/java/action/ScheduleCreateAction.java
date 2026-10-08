package action;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class ScheduleCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 映画選択ボックス用に映画一覧を取得
        MovieDao movieDao = new MovieDao();
        List<Movie> movieList = movieDao.getAll();

        request.setAttribute("movieList", movieList);
        request.getRequestDispatcher("/admin/schedules/create.jsp").forward(request, response);
    }
}