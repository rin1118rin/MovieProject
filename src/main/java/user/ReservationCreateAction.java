package user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Schedule;
import dao.ScheduleDao;
import tool.Action;


public class ReservationCreateAction extends Action {
    @Override 
    public void execute(HttpServletRequest req,HttpServletResponse res) throws Exception{
        //スケジュールIDを取得
        int scheduleId = Integer.parseInt(req.getParameter("scheduleId"));
        //DAOからスケジュールを取得
        ScheduleDao dao = new ScheduleDao();
        Schedule schedule = dao.get(scheduleId);
        //JSPへ渡す
        req.setAttribute("schedule",schedule);
        //予約登録画面へ
       req.getRequestDispatcher("create.jsp").forward(req, res);
    }
}

    