package action;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;


public class AdminCreateAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
    	// 管理者登録画面へ偏移
        request.getRequestDispatcher("/admin/register.jsp") .forward(request, response);
    }
}