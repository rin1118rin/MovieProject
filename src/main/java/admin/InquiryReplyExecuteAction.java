package admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.InquiryReply;
import dao.InquiryReplyDao;
import tool.Action;

public class InquiryReplyExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // リクエストパラメータの取得
        String inquiryIdStr = request.getParameter("inquiryId");
        String replyBody = request.getParameter("replyBody");
        String staffName = request.getParameter("staffName");

        // 必須入力チェック
        if (inquiryIdStr == null || replyBody == null || replyBody.trim().isEmpty()) {
            response.sendRedirect("InquiryList.action");
            return;
        }

        int inquiryId = Integer.parseInt(inquiryIdStr);

        // 担当者名が未指定の場合はデフォルト値をセット
        if (staffName == null || staffName.trim().isEmpty()) {
            staffName = "管理者";
        }

        // InquiryReply Bean の構築
        InquiryReply reply = new InquiryReply();
        reply.setInquiryId(inquiryId);
        reply.setReplyBody(replyBody.trim());
        reply.setStaffName(staffName.trim());

        // 返信登録と「対応済み」への更新をアトミックに実行
        InquiryReplyDao dao = new InquiryReplyDao();
        boolean isSuccess = dao.saveAndUpdateStatus(reply, "対応済み");

        // 完了後はお問い合わせ一覧へ
        response.sendRedirect("InquiryList.action");
    }
}