package tool;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
            // リクエストURIからパスを抽出 (例: "user/scheduleList.action")
            String path = request.getRequestURI().substring(request.getContextPath().length() + 1);
            String name = path.substring(0, path.indexOf(".action"));

            // スラッシュが含まれていない場合のエラーハンドリング
            if (!name.contains("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                    "URLの形式が正しくありません。パッケージ名(/user/ または /admin/)を含めて指定してください。");
                return;
            }

            // パッケージ名とアクション名に分割
            int lastSlash = name.lastIndexOf("/");
            String pkg = name.substring(0, lastSlash).replace('/', '.'); // "user" や "admin"
            String actionName = name.substring(lastSlash + 1);          // "scheduleList"

            // 先頭を大文字化
            String formattedActionName = Character.toUpperCase(actionName.charAt(0)) + actionName.substring(1);

            // 末尾に"Action"がなければ補完
            if (!formattedActionName.endsWith("Action")) {
                formattedActionName += "Action";
            }

            // 完全限定クラス名の組み立て
            String className = pkg + "." + formattedActionName;

            // クラスの動的ロードとインスタンス化
            Action action = (Action) Class.forName(className).getDeclaredConstructor().newInstance();

            // Actionの実行
            action.execute(request, response);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "指定されたActionクラスが存在しません: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}