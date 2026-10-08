package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Admin;
import dao.AdminDao;
import tool.Action;

public class AdminCreateExecuteAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
    	// 入力された管理者IDを取得
        int adminId = Integer.parseInt(request.getParameter("adminId"));
        // 入力されたパスワードを取得
        String password = request.getParameter("password");
        // adminオブジェクトを生成
        Admin admin = new Admin();
        // IDとパスワードの値をセット
        admin.setAdminId(adminId);
        admin.setPassword(password);
        // DAOを生成
        AdminDao dao = new AdminDao();
        // 管理者登録を実行
        boolean result = dao.save(admin);

        if (result) {
        	//  登録成功のとき、ログイン画面へ偏移
            response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
        } else {
        	// 失敗したとき、登録画面へ戻る
            request.setAttribute( "error", "管理者IDが既に存在します");
            request.getRequestDispatcher("/admin/register.jsp") .forward(request, response);
        }
    }
}