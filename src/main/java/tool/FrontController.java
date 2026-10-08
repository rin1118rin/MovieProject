package tool;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// すべての *.action リクエストを一括受領し、対応する actionクラスを動的にロード・実行するフロントコントローラー
 
@WebServlet("*.action")
public class FrontController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcess(request, response);
    }

    private void doProcess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // リクエストURIからaction名を取得
            String path = request.getRequestURI().substring(request.getContextPath().length() + 1);
            String name = path.substring(0, path.indexOf(".action"));

            // クラス名の組み立て
            String className = "action." + Character.toUpperCase(name.charAt(0)) + name.substring(1) + "Action";

            // クラスの動的ロードとインスタンス化
            Action action = (Action) Class.forName(className).getDeclaredConstructor().newInstance();

            // 各Actionの処理を実行
            action.execute(request, response);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "指定されたActionクラスが存在しません。");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}