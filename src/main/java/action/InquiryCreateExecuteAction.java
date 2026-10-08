package action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Inquiry;
import dao.InquiryDao;
import tool.Action;

public class InquiryCreateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // フォームからのリクエストパラメータ取得
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");

        // 入力チェック
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            subject == null || subject.trim().isEmpty() ||
            body == null || body.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "すべての項目を入力してください。");
            // 入力画面へフォワード
            request.getRequestDispatcher("/user/contact/input.jsp").forward(request, response);
            return;
        }

        // Inquiry Bean の作成とセット
        Inquiry inquiry = new Inquiry();
        inquiry.setName(name.trim());
        inquiry.setEmail(email.trim());
        inquiry.setSubject(subject.trim());
        inquiry.setBody(body.trim());
        // sentAt と statusはnullで渡すと、InquiryDao 内で現在日時と「未対応」が自動設定

        // DBへの登録
        InquiryDao dao = new InquiryDao();
        boolean isSuccess = dao.save(inquiry);

        if (isSuccess) {
            // 登録成功時は完了画面へ
            request.getRequestDispatcher("/user/contact/complete.jsp").forward(request, response);
        } else {
            // 失敗時はエラーメッセージを持って入力画面へ
            request.setAttribute("errorMessage", "送信に失敗しました。もう一度お試しください。");
            request.getRequestDispatcher("/user/contact/input.jsp").forward(request, response);
        }
    }
}