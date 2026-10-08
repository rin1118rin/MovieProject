package action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import tool.Action;

public class InquiryCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // お問い合わせ入力画面へ
        request.getRequestDispatcher("/user/contact/input.jsp").forward(request, response);
    }
}