package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Admin;
import dao.AdminDao;
import tool.Action;

public class AdminLoginExecuteAction extends Action{
	@Override
	public void execute(HttpServletRequest request,HttpServletResponse response)
		throws Exception {
		// 画面で入力された管理者IDを取得
		int adminId = Integer.parseInt(request.getParameter("adminId"));
		// 画面で入力された管理者パスワードを取得
		String password = request.getParameter("password");
		// DAOを生成
		AdminDao dao = new AdminDao();
		// DBから管理者の情報を取得
		Admin admin  = dao.get(adminId);
		// 管理者が存在して、パスワードが合っている場合
		if (admin != null && password.equals(admin.getPassword())) {
			// セッションを取得
			HttpSession session =  request.getSession();
			// ログイン中の管理者の情報を保存
			session.setAttribute("loginAdmin", admin);
			// 管理者メニューへ偏移
			response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
			
		} else {
			request.setAttribute("error","IDまたはパスワードが正しくありません");
			// IDやパスワードが合っていない場合、ログイン画面に戻る
			request.getRequestDispatcher("/admin/login.jsp") .forward(request,response);
		}
	} 
}