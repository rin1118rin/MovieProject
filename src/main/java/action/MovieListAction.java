package action;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieListAction extends Action {

    public void execute(
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        MovieDao dao = new MovieDao();

        // DBから全映画情報を取得
        List<Movie> movieList = dao.getAll();

        // リクエストスコープにセット
        request.setAttribute("movieList", movieList);

        // FrontController 側に遷移先JSPのパスを返却
        return;
    }
}