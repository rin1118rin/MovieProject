package user;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        MovieDao dao = new MovieDao();

        // 映画一覧を取得（必要に応じて公開中・公開予定のみを抽出するメソッドに変更可能）
        List<Movie> movieList = dao.getAll();

        // リクエストスコープにセット
        request.setAttribute("movieList", movieList);

        // ユーザー用映画一覧JSPへフォワード
        request.getRequestDispatcher("/user/movies/list.jsp").forward(request, response);
    }
}