package admin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import bean.Movie;
import dao.MovieDao;
import tool.Action;
import tool.PosterUploader;

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

        // Movie Bean へ取得値をセット(ポスター画像は、保存できたあとでセットする)
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

        // ポスター画像を、アップロードされた名前のまま、実行中のアプリ(Tomcat)の images/movies に保存する
        File posterDir = new File(request.getServletContext().getRealPath("/images/movies"));
        String posterUrl = null;
        try {
            Part posterPart = request.getPart("posterFile");
            posterUrl = PosterUploader.save(posterPart, posterDir);
            if (posterUrl == null) {
                request.setAttribute("errorMessage", "ポスター画像を選択してください。");
            }
        } catch (PosterUploader.UploadException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        if (posterUrl == null) {
            // 画像に問題がある場合は、DBに登録せず入力画面へ戻す
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/admin/movies/create.jsp").forward(request, response);
            return;
        }
        movie.setPosterUrl(posterUrl);

        // MovieDao を使用して DB への保存処理を実行
        MovieDao dao = new MovieDao();
        boolean isSuccess = dao.save(movie);

        // 実行結果に応じた画面遷移
        if (isSuccess) {
            // 登録成功時：完了メッセージを保持して一覧画面へ
            request.setAttribute("message", "映画情報を登録しました。");
            request.getRequestDispatcher("/admin/movieList.action").forward(request, response);
        } else {
            // 登録失敗時：保存した画像を消して、エラーメッセージと入力データを保持して入力画面へ
            PosterUploader.delete(posterUrl, posterDir);
            request.setAttribute("errorMessage", "映画情報の登録に失敗しました。");
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/admin/movies/create.jsp").forward(request, response);
        }
    }
}