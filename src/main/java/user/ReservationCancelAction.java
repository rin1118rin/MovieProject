package user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Reservation;
import dao.ReservationDao;
import tool.Action;

public class ReservationCancelAction extends Action {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //予約IDを取得
        int reservationId = Integer.parseInt(request.getParameter("reservationId"));
        //DAOから予約情報を取得
        ReservationDao dao = new ReservationDao();
        Reservation reservation = dao.get(reservationId);
        //予約情報をJSPに渡す
        request.setAttribute("reservation", reservation);
        //予約取消画面を表示
        request.getRequestDispatcher("/reservation/cancel.jsp").forward(request, response);
    }
}
