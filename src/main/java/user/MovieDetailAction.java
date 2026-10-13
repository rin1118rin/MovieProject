package user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieDetailAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            response.sendError(405);
            return;
        }

        int movieId;

        try {
            String id = request.getParameter("id");

            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException();
            }

            movieId = Integer.parseInt(id.trim());

            if (movieId <= 0) {
                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException e) {
            response.sendError(400, "映画IDが不正です。");
            return;
        }

        Movie movie = new MovieDao().get(movieId);

        if (movie == null) {
            response.sendError(404, "映画が見つかりません。");
            return;
        }

        request.setAttribute("movie", movie);

        request.getRequestDispatcher("/user/movies/detail.jsp")
               .forward(request, response);
    }
}