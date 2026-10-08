package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieDeleteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 削除対象の映画IDを取得
        String movieIdStr = request.getParameter("movieId");

        if (movieIdStr != null && !movieIdStr.trim().isEmpty()) {
            try {
                int movieId = Integer.parseInt(movieIdStr.trim());

                // DBから対象の映画情報を取得
                MovieDao dao = new MovieDao();
                Movie movie = dao.get(movieId);

                if (movie != null) {
                    // 取得できた場合はリクエストスコープにセット
                    request.setAttribute("movie", movie);
                    request.getRequestDispatcher("/admin/movies/delete.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // IDの数値変換失敗時はエラー処理へ
            }
        }

        // 対象が見つからない場合は一覧画面へ戻す
        request.setAttribute("errorMessage", "指定された映画が見つかりませんでした。");
        request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
    }
}