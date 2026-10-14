package admin;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Inquiry;
import bean.InquiryReply;
import dao.InquiryDao;
import dao.InquiryReplyDao;
import tool.Action;

public class InquiryReplyExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Properties prop = new Properties();
    	
    	try (InputStream in = InquiryReplyExecuteAction.class.getResourceAsStream("mail.properties")) {
    		if (in == null) {
    			throw new FileNotFoundException("ファイルが見つかりません");
    		}
    		prop.load(in);
    	}
    	
    	final String username = prop.getProperty("mailaddress");
    	
    	final String password = prop.getProperty("password");
    	
    	
    	System.out.println(prop);
    	System.out.println(username + password);
    	
    	prop.forEach((key, value) -> {
    	    System.out.println("キー=[" + key + "]");
    	});

    	
    	Session session = Session.getInstance(prop, new Authenticator() {
    		protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
    	});
        // リクエストパラメータの取得
    	InquiryDao inquiryDao = new InquiryDao();
        String inquiryIdStr = request.getParameter("inquiryId");
        String replyBody = request.getParameter("replyBody");
        String staffName = request.getParameter("staffName");

        // 必須入力チェック
        if (inquiryIdStr == null || replyBody == null || replyBody.trim().isEmpty()) {
            response.sendRedirect("InquiryList.action");
            return;
        }

        int inquiryId = Integer.parseInt(inquiryIdStr);
        
        Inquiry inquiry = inquiryDao.get(inquiryId);

        // 担当者名が未指定の場合はデフォルト値をセット
        if (staffName == null || staffName.trim().isEmpty()) {
            staffName = "管理者";
        }
        
        try {
        	Message message = new MimeMessage(session);
        	
        	message.setFrom(new InternetAddress(username));
        	
        	message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(inquiry.getEmail()));
        	
        	message.setSubject(inquiry.getSubject());
        	
        	message.setText(staffName+ "\n\n" + replyBody);
        	
        	Transport.send(message);
        	
        	System.out.println("メールが正常に送信されました");
        } catch (MessagingException e) {
			System.out.println("メールが正常に送信されませんでした" + e);
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