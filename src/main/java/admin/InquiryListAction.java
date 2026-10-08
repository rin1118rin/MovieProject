package admin;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Inquiry;
import dao.InquiryDao;
import tool.Action;

public class InquiryListAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // お問い合わせ一覧を取得
        InquiryDao dao = new InquiryDao();
        List<Inquiry> inquiryList = dao.getAll();

        // リクエストスコープにセット
        request.setAttribute("inquiryList", inquiryList);

        // お問い合わせ一覧画面へ
        request.getRequestDispatcher("/admin/inquiry/list.jsp").forward(request, response);
    }
}