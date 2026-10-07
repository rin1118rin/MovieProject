package action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieDeleteExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 削除対象の映画IDを取得
        String movieIdStr = request.getParameter("movieId");

        if (movieIdStr != null && !movieIdStr.trim().isEmpty()) {
            try {
                int movieId = Integer.parseInt(movieIdStr.trim());

                // 削除用のMovie Beanを作成（IDを設定）
                Movie movie = new Movie();
                movie.setMovieId(movieId);

                // DBから削除処理を実行
                MovieDao dao = new MovieDao();
                boolean isSuccess = dao.delete(movie);

                if (isSuccess) {
                    // 削除成功時：一覧画面へフォワード
                    request.setAttribute("message", "映画情報を削除しました。");
                    request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
                    return;
                }
            } catch (Exception e) {
                // 上映スケジュール等の外部キー制約違反や例外が発生した場合
                request.setAttribute("errorMessage", "上映スケジュールが登録されているため削除できません。");
                request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
                return;
            }
        }

        // IDが無効な場合
        request.setAttribute("errorMessage", "削除処理に失敗しました。");
        request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
    }
}