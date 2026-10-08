package admin;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Inquiry;
import bean.InquiryReply;
import dao.InquiryDao;
import dao.InquiryReplyDao;
import tool.Action;

public class InquiryReplyAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // inquiryId を取得
        String idStr = request.getParameter("inquiryId");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("InquiryList.action");
            return;
        }

        int inquiryId = Integer.parseInt(idStr);

        // 対象のお問い合わせデータを取得
        InquiryDao inquiryDao = new InquiryDao();
        Inquiry inquiry = inquiryDao.get(inquiryId);

        // 該当データが存在しない場合は一覧へリダイレクト
        if (inquiry == null) {
            response.sendRedirect("InquiryList.action");
            return;
        }

        // 過去の返信履歴を取得
        InquiryReplyDao replyDao = new InquiryReplyDao();
        List<InquiryReply> replyList = replyDao.getByInquiryId(inquiryId);

        // 4. リクエストスコープにセット
        request.setAttribute("inquiry", inquiry);
        request.setAttribute("replyList", replyList);

        // 返信画面へ
        request.getRequestDispatcher("/admin/contact/reply.jsp").forward(request, response);
    }
}