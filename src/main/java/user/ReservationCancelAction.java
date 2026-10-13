package user;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;

public class ReservationCancelAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            response.sendError(405);
            return;
        }

        String token = UUID.randomUUID().toString();

        request.getSession().setAttribute("cancelToken", token);
        request.setAttribute("cancelToken", token);

        response.setHeader("Cache-Control", "no-store");

        request.getRequestDispatcher("/user/reservation/cancel.jsp")
               .forward(request, response);
    }
}