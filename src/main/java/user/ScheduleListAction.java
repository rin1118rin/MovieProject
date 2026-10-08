package user;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
import dao.ScheduleDao;
import tool.Action;

public class ScheduleListAction extends Action {
	
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String dateStr = request.getParameter("date");
		
		ScheduleDao sDao = new ScheduleDao();
		
		MovieDao dao = new MovieDao();
		
		LocalDate selectedDate = LocalDate.now();
		
		Date targetDate = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<LocalDate> dates = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
		    dates.add(selectedDate.plusDays(i));
		}
		
		if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                targetDate = sdf.parse(dateStr.trim());
            } catch (Exception e) {
                // 変換失敗時は本日日付を使用
                targetDate = new Date();
            }
		}
		
		Date date = java.sql.Date.valueOf(selectedDate);
		
		List<Schedule> scheduleList = sDao.getByDate(date);
		List<Movie> movies = new ArrayList<>();
		
		for (Schedule schedule : scheduleList) {
			
			Movie movie = dao.get(schedule.getMovieId());
			
			if (movie == null) {
				continue;
			}
			
			Movie existingMovie = null;
			
			for (Movie m : movies) {
				if (m.getMovieId() == movie.getMovieId()) {
					existingMovie = m;
					break;
				}
			}
			if (existingMovie == null) {
				List<Schedule> schedules = new ArrayList<>();
				schedules.add(schedule);
				
				movie.setSchedules(schedules);
				
				movies.add(movie);
			} else {
				existingMovie.getSchedules().add(schedule);
			}
		}

		request.setAttribute("dates", dates);
		request.setAttribute("selectDate", targetDate);
		request.setAttribute("movies", movies);
		
		request.getRequestDispatcher("/user/schedule/list.jsp").forward(request, response);
	}
}
