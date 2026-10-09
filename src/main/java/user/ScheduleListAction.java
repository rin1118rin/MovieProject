package user;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
import dao.ScheduleDao;
import tool.Action;

public class ScheduleListAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        // 選択日。指定されていなければ今日
        LocalDate selectedDate = LocalDate.now();
        String dateStr = request.getParameter("date");

        if (dateStr != null && !dateStr.isBlank()) {
            try {
                selectedDate = LocalDate.parse(dateStr.trim());
            } catch (DateTimeParseException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "日付が不正です。");
                return;
            }
        }

        // 日付ボタン用：今日から7日分
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            dates.add(today.plusDays(i));
        }

        // 選択日の上映スケジュールを取得
        ScheduleDao scheduleDao = new ScheduleDao();
        List<Schedule> scheduleList =
                scheduleDao.getByDate(
                        java.sql.Date.valueOf(selectedDate));

        // 映画情報を映画IDで検索できるようにする
        MovieDao movieDao = new MovieDao();
        Map<Integer, Movie> movieById = new LinkedHashMap<>();

        for (Movie movie : movieDao.getAll()) {
            movie.setSchedules(new ArrayList<Schedule>());
            movieById.put(movie.getMovieId(), movie);
        }

        // 上映回を映画ごとにまとめる
        Map<Integer, Movie> scheduledMovies = new LinkedHashMap<>();

        for (Schedule schedule : scheduleList) {
            Movie movie = movieById.get(schedule.getMovieId());

            if (movie != null) {
                movie.getSchedules().add(schedule);
                scheduledMovies.putIfAbsent(
                        movie.getMovieId(), movie);
            }
        }

        // JSPへ渡す
        request.setAttribute("dates", dates);
        request.setAttribute("selectedDate", selectedDate);
        request.setAttribute(
                "movies",
                new ArrayList<>(scheduledMovies.values()));

        // 上映スケジュール一覧を表示
        request.getRequestDispatcher("/user/schedule/list.jsp")
               .forward(request, response);
    }
}