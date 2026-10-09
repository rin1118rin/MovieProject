package admin;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 管理者ログインチェック
        HttpSession session = request.getSession();
        if (session.getAttribute("admin") == null) {
            // 未ログインの場合はログイン画面へ
            response.sendRedirect(request.getContextPath() + "/admin/login.action");
            return;
        }

        MovieDao dao = new MovieDao();

        // 全映画情報を取得（非表示・終了作品含む）
        List<Movie> movieList = dao.getAll();

        // リクエストスコープにセット
        request.setAttribute("movieList", movieList);

        // 管理者用映画一覧JSPへフォワード
        request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
    }
}