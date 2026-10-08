package action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tool.Action;

public class AdminLoginAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) { session.invalidate(); }
        //ログイン画面へ偏移
        request.getRequestDispatcher("/admin/logout.jsp") .forward(request, response);
    }
}