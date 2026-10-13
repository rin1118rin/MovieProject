package admin;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Movie;
import dao.MovieDao;
import tool.Action;


public class ScheduleCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // ログインチェック
        HttpSession session = request.getSession();
        if (session.getAttribute("loginAdmin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login.action");
            return;
        }

        // MovieDaoを使ってすべての映画を取得する
        MovieDao movieDao = new MovieDao();
        List<Movie> movieList = movieDao.getAll();

        request.setAttribute("movies", movieList);

        // スケジュール登録画面のJSPへフォワード
        request.getRequestDispatcher("/admin/schedule/create.jsp").forward(request, response);
    }
}