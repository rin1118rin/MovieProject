package user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.MovieDao;
import tool.Action;

public class UserMenuAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        request.setAttribute(
            "rankingMovies", new MovieDao().getRanking()
        );

        request.getRequestDispatcher("/user/index.jsp")
               .forward(request, response);
    }
}