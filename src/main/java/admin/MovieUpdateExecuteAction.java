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

        MovieDao dao = new MovieDao();

        // いま登録されているポスター画像(新しい画像が選ばれなければ、そのまま使う)
        Movie current = dao.get(movieId);
        String oldPosterUrl = (current != null) ? current.getPosterUrl() : null;
        movie.setPosterUrl(oldPosterUrl);

        // 新しいポスター画像が選ばれていれば、アップロードされた名前のまま、実行中のアプリ(Tomcat)の images/movies に保存する
        File posterDir = new File(request.getServletContext().getRealPath("/images/movies"));
        String newPosterUrl = null;
        try {
            Part posterPart = request.getPart("posterFile");
            newPosterUrl = PosterUploader.save(posterPart, posterDir);
        } catch (PosterUploader.UploadException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/admin/movies/edit.jsp").forward(request, response);
            return;
        }
        if (newPosterUrl != null) {
            movie.setPosterUrl(newPosterUrl);
        }

        // MovieDao を使用して DB の更新処理を実行
        boolean isSuccess = dao.update(movie);

        // 実行結果に応じた画面遷移
        if (isSuccess) {
            // 画像を差し替えた場合は、古い画像を削除する(ほかの映画が使っていなければ)
            if (newPosterUrl != null && oldPosterUrl != null && !oldPosterUrl.equals(newPosterUrl)) {
                PosterUploader.deleteIfUnused(oldPosterUrl, posterDir, dao.getAll());
            }
            // 更新成功時：メッセージを保持して一覧画面へ
            request.setAttribute("message", "映画情報を更新しました。");
            request.getRequestDispatcher("/admin/movieList.action").forward(request, response);
        } else {
            // 更新失敗時：今回あたらしく保存した画像を消して、入力データを保持して元の編集画面へ
            if (newPosterUrl != null) {
                PosterUploader.delete(newPosterUrl, posterDir);
            }
            movie.setPosterUrl(oldPosterUrl);
            request.setAttribute("errorMessage", "映画情報の更新に失敗しました。");
            request.setAttribute("movie", movie);
            request.getRequestDispatcher("/admin/movies/edit.jsp").forward(request, response);
        }
    }
}