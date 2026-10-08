package action;

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
		
		int adminId = Integer.parseInt(request.getParameter("adminId"));
		
		String password = request.getParameter("password");
		
		AdminDao dao = new AdminDao();
		
		Admin admin  = dao.get(adminId);
		
		if (admin != null && password.equals(admin.getPassword())) {
		
			HttpSession session =  request.getSession();
			
			session.setAttribute("loginAdmin", admin);
			
			response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
			
		} else {
			
			request.setAttribute("error","IDまたはパスワードが正しくありません");
			
			request.getRequestDispatcher("/admin/login.jsp") .forward(request,response);
		}
	} 
}
