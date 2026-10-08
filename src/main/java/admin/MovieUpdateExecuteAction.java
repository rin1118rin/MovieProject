package admin;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieUpdateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	
        // リクエストの文字コード設定
        request.setCharacterEncoding("UTF-8");

        // 送信データを取得
        String movieIdStr = request.getParameter("movieId");
        String title = request.getParameter("title");
        String durationStr = request.getParameter("duration");
        String releaseStartDateStr = request.getParameter("releaseStartDate");
        String releaseEndDateStr = request.getParameter("releaseEndDate");
        String genre = request.getParameter("genre");
        String ageLimit = request.getParameter("ageLimit");
        String description = request.getParameter("description");
        String director = request.getParameter("director");
        String cast = request.getParameter("cast");
        String posterUrl = request.getParameter("posterUrl");

        // 数値・日付データの変換処理
        int movieId = 0;
        if (movieIdStr != null && !movieIdStr.trim().isEmpty()) {
            movieId = Integer.parseInt(movieIdStr.trim());
        }

        int duration = 0;
        if (durationStr != null && !durationStr.trim().isEmpty()) {
            try {
                duration = Integer.parseInt(durationStr.trim());
            } catch (NumberFormatException e) {
                duration = 0;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date releaseStartDate = null;
        if (releaseStartDateStr != null && !releaseStartDateStr.trim().isEmpty()) {
            try {
                releaseStartDate = sdf.parse(releaseStartDateStr.trim());
            } catch (Exception e) {
                releaseStartDate = new Date();
            }
        }

        Date releaseEndDate = null;
        if (releaseEndDateStr != null && !releaseEndDateStr.trim().isEmpty()) {
            try {
                releaseEndDate = sdf.parse(releaseEndDateStr.trim());
            } catch (Exception e) {
                releaseEndDate = new Date();
            }
        }

        // Movie Bean に変更後の値をセット
        Movie movie = new Movie();
        movie.setMovieId(movieId);
        movie.setTitle(title);
        movie.setDuration(duration);
        movie.setReleaseStartDate(releaseStartDate);
        movie.setReleaseEndDate(releaseEndDate);
        movie.setGenre(genre);
        movie.setAgeLimit(ageLimit);
        movie.setDescription(description);
        movie.setDirector(director);
        movie.setCast(cast);
        movie.setPosterUrl(posterUrl);

        // MovieDao を使用して DB の更新処理を実行
        MovieDao dao = new MovieDao();
        boolean isSuccess = dao.update(movie);

        // 実行結果に応じた画面遷移
        if (isSuccess) {
            // 更新成功時：メッセージを保持して一覧画面へ
            request.setAttribute("message", "映画情報を更新しました。");
            request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
        } else {
            // 更新失敗時：入力データを保持して元の編集画面へ
            request.setAttribute("errorMessage", "映画情報の更新に失敗しました。");
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/admin/movies/edit.jsp").forward(request, response);
        }
    }
}