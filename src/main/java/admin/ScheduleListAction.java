package admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Schedule;
import dao.ScheduleDao;
import tool.Action;

public class ScheduleListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 管理者ログインチェック
        HttpSession session = request.getSession();
        if (session.getAttribute("loginAdmin") == null) {
            // 未ログインの場合はログイン画面へ
            response.sendRedirect(request.getContextPath() + "/admin/adminLogin.action");
            return;
        }


        String dateStr = request.getParameter("date");
        Date targetDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                targetDate = sdf.parse(dateStr.trim());
            } catch (Exception e) {
                targetDate = new Date();
            }
        }

        ScheduleDao dao = new ScheduleDao();
        List<Schedule> scheduleList = dao.getByDate(targetDate);

        request.setAttribute("scheduleList", scheduleList);
        request.setAttribute("selectedDate", sdf.format(targetDate));

        // 管理者用JSPへ
        request.getRequestDispatcher("/admin/schedule/list.jsp").forward(request, response);
    }
}