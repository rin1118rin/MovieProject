package user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import bean.Inquiry;
import dao.InquiryDao;
import tool.Action;

public class InquiryCreateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {

        // 保存後の完了画面
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(false);

            Object inquiry = session == null
                ? null
                : session.getAttribute("completedInquiry");

            if (inquiry == null) {
                response.sendRedirect(
                    request.getContextPath()
                    + "/user/inquiryCreate.action"
                );
                return;
            }

            response.setHeader("Cache-Control", "no-store");
            request.setAttribute("inquiry", inquiry);

            request.getRequestDispatcher("/user/contact/complete.jsp")
                   .forward(request, response);
            return;
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET, POST");
            response.sendError(405);
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String name = value(request, "name");
        String email = value(request, "email");
        String category = value(request, "category");
        String body = value(request, "message");

        String subject;

        switch (category) {
            case "reservation":
                subject = "チケット・予約について";
                break;
            case "movie":
                subject = "上映作品について";
                break;
            case "facility":
                subject = "劇場・施設について";
                break;
            case "other":
                subject = "その他";
                break;
            default:
                subject = null;
        }

        if (name.isEmpty() || email.isEmpty()
                || body.isEmpty() || subject == null) {
            showError(
                request, response,
                "すべての項目を入力し、お問い合わせ種別を選択してください。"
            );
            return;
        }

        if (name.length() > 50 || email.length() > 255) {
            showError(
                request, response,
                "お名前は50文字以内、メールアドレスは255文字以内で入力してください。"
            );
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showError(
                request, response,
                "メールアドレスを確認してください。"
            );
            return;
        }

        Inquiry inquiry = new Inquiry();
        inquiry.setName(name);
        inquiry.setEmail(email);
        inquiry.setSubject(subject);
        inquiry.setBody(body);

        boolean success = new InquiryDao().save(inquiry);

        if (!success) {
            showError(
                request, response,
                "送信に失敗しました。もう一度お試しください。"
            );
            return;
        }

        request.getSession().setAttribute(
            "completedInquiry", inquiry
        );

        // 完了画面の再読み込みで再登録しない
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader(
            "Location",
            response.encodeRedirectURL(
                request.getContextPath()
                + "/user/inquiryCreateExecute.action"
            )
        );
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message) throws Exception {

        request.setAttribute("errorMessage", message);

        request.getRequestDispatcher("/user/contact/input.jsp")
               .forward(request, response);
    }
}