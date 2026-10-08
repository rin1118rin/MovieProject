package admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;


public class AdminCreateAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
        throws Exception {

        request.getRequestDispatcher("/admin/register.jsp") .forward(request, response);
    }
}