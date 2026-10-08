package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;

public class UserMenuAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // ユーザー用メニュー画面へ
        request.getRequestDispatcher("/user/index.jsp").forward(request, response);
    }
}