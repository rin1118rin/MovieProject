package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;

public class MovieCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 登録入力画面を表示するだけ(保存は MovieCreateExecuteAction が行う)
        request.getRequestDispatcher("/admin/movies/create.jsp").forward(request, response);
    }
}