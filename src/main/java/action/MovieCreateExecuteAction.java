package action;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieCreateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 文字コード設定
        request.setCharacterEncoding("UTF-8");

        // フォームからの送信データを取得
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
                releaseStartDate = new Date(); // 変換失敗時は現在の日付をセット
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

        // Movie Bean へ取得値をセット
        Movie movie = new Movie();
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

        // MovieDao を使用して DB への保存処理を実行
        MovieDao dao = new MovieDao();
        boolean isSuccess = dao.save(movie);

        // 実行結果に応じた画面遷移
        if (isSuccess) {
            // 登録成功時：映画一覧表示へフォワード
            request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
        } else {
            // 登録失敗時：エラーメッセージと入力データを保持して入力画面へ
            request.setAttribute("errorMessage", "映画情報の登録に失敗しました。");
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/admin/movies/create.jsp").forward(request, response);
        }
    }
}