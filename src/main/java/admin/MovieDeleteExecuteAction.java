package admin;

import java.io.File;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Movie;
import dao.MovieDao;
import tool.Action;
import tool.PosterUploader;

public class MovieDeleteExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String listUrl = request.getContextPath() + "/admin/movieList.action";

        // 削除は、削除確認画面のボタン(POST)からだけ実行する
        if (!"POST".equals(request.getMethod())) {
            response.sendRedirect(listUrl);
            return;
        }

        HttpSession session = request.getSession();
        String errorMessage = "削除処理に失敗しました。";

        // 削除対象の映画IDを取得
        String movieIdStr = request.getParameter("movieId");

        if (movieIdStr != null && !movieIdStr.trim().isEmpty()) {
            try {
                int movieId = Integer.parseInt(movieIdStr.trim());

                MovieDao dao = new MovieDao();

                // 削除したあとで画像も消せるように、先にポスター画像の名前を控えておく
                Movie current = dao.get(movieId);
                String posterUrl = (current != null) ? current.getPosterUrl() : null;

                // 削除用のMovie Beanを作成（IDを設定）
                Movie movie = new Movie();
                movie.setMovieId(movieId);

                // DBから削除処理を実行
                if (dao.delete(movie)) {
                    // ポスター画像を削除する(ほかの映画が同じ画像を使っていなければ)
                    File posterDir = new File(request.getServletContext().getRealPath("/images/movies"));
                    PosterUploader.deleteIfUnused(posterUrl, posterDir, dao.getAll());
                    // 削除成功
                    session.setAttribute("message", "映画情報を削除しました。");
                    response.sendRedirect(listUrl);
                    return;
                }
                errorMessage = "削除できませんでした。上映スケジュールが登録されている映画は削除できません。";

            } catch (NumberFormatException e) {
                // IDが数字でない場合は、最初のメッセージのまま
            } catch (Exception e) {
                // 上映スケジュール等の外部キー制約違反や例外が発生した場合
                errorMessage = "上映スケジュールが登録されているため削除できません。";
            }
        }

        session.setAttribute("errorMessage", errorMessage);
        response.sendRedirect(listUrl);
    }
}