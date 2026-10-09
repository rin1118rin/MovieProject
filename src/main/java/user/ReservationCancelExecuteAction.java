package user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.ReservationDao;
import tool.Action;

public class ReservationCancelExecuteAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //予約IDを取得
        int reservationId = Integer.parseInt(request.getParameter("reservationId"));
        //DAOを作成
        ReservationDao dao = new ReservationDao();
        //予約を取り消す
        boolean result = dao.cancel(reservationId);
        // 取消結果をJSPに渡す
        request.setAttribute("result", result);
        //予約取消完了画面へ
        request.getRequestDispatcher("cancel-complete.jsp").forward(request, response);
    }
}
