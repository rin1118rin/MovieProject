package action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;

public class AdminMenuAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 管理者用メニュー画面へ
        request.getRequestDispatcher("/admin/index.jsp").forward(request, response);
    }
}