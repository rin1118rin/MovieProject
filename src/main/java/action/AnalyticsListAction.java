package action;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Analytics;
import dao.AnalyticsDao;
import tool.Action;

public class AnalyticsListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // AnalyticsDaoをインスタンス化
        AnalyticsDao dao = new AnalyticsDao();

        // 上映スケジュールごとの稼働率データを取得
        List<Analytics> analyticsList = dao.getOccupancyRate();

        // リクエストスコープに集計結果をセット
        request.setAttribute("analyticsList", analyticsList);

        // 分析一覧画面へ
        request.getRequestDispatcher("/admin/analysis/list.jsp").forward(request, response);
    }
}