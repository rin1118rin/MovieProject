package action;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieListAction extends Action {

    @Override
    public void execute(
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        MovieDao dao = new MovieDao();8

        List<Movie> movieList = dao.getAll();

        request.setAttribute("movieList", movieList);

        request.getRequestDispatcher(
                "/admin/movies/list.jsp")
                .forward(request, response);
    }
}