package action;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import dao.MovieDao;
import tool.Action;

public class MovieCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
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

        // 日付フォーマットの変換準備
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Date releaseStartDate = null;
        if (releaseStartDateStr != null && !releaseStartDateStr.isEmpty()) {
            releaseStartDate = sdf.parse(releaseStartDateStr);
        }

        Date releaseEndDate = null;
        if (releaseEndDateStr != null && !releaseEndDateStr.isEmpty()) {
            releaseEndDate = sdf.parse(releaseEndDateStr);
        }

        int duration = 0;
        if (durationStr != null && !durationStr.isEmpty()) {
            duration = Integer.parseInt(durationStr);
        }

        // Movie Beanに値をセット
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

        // MovieDaoを使ってDBへ登録処理を実行
        MovieDao dao = new MovieDao();
        boolean isSuccess = dao.save(movie);

        //結果に応じた画面遷移
        if (isSuccess) {
            // 登録成功時：一覧表示画面へ
            request.getRequestDispatcher("/admin/movies/list.jsp").forward(request, response);
        } else {
            // 登録失敗時：エラーメッセージを渡して登録入力画面へ
            request.setAttribute("errorMessage", "映画情報の登録に失敗しました。");
            request.getRequestDispatcher("/admin/movies/create.jsp").forward(request, response);
        }
    }
}