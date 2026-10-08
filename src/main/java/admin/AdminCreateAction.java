package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import tool.Action;

public class AdminCreateAction extends Action {
	
	public void execute(HttpServletRequest req, HttpServletResponse res) {
		
		HttpSession session = req.getSession();
		
	}
	
}