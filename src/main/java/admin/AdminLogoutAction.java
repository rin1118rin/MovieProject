package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminLogoutAction {

    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    	// セッションを取得  存在すれば無効化する
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

     // ログアウト完了画面へ遷移
        req.getRequestDispatcher("/admin/logout.jsp").forward(req, resp);
    }
}