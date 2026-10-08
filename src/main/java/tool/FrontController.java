package tool;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@WebServlet(urlPatterns={"*.action"})
 
public class FrontController extends HttpServlet {
 

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    	
        try {
            String path = req.getServletPath().substring(1);
            String base = path.replace(".action", "").replace("/", ".");
            // パッケージ名 + クラス名
            String className = "action." + base + "Action";

            System.out.println("★ servlet path -> " + req.getServletPath());
            System.out.println("★ class name -> " + className);
            Class<?> type = Class.forName(className);
            Action action = (Action) type.getDeclaredConstructor().newInstance();
            action.execute(req, res);
        } catch (Exception e) {
            e.printStackTrace();
            req.getRequestDispatcher("/error.jsp").forward(req, res);
        }
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
