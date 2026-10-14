package tool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import jakarta.activation.DataHandler;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

public final class ReservationMailSender {

    private ReservationMailSender() {
    }

    // 予約内容とQRコードをメール送信
    public static void send(
            Map<String, Object> reservation) throws Exception {

        // mail.propertiesを読み込む
        Properties config = new Properties();

        try (InputStream input =
                ReservationMailSender.class.getResourceAsStream(
                    "/admin/mail.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                    "mail.propertiesが見つかりません。"
                );
            }

            config.load(input);
        }

        String from = config.getProperty("mailaddress", "").trim();

        String password = config.getProperty(
                "password", "")
            .replace(" ", "")
            .trim();

        if (from.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException(
                "mail.propertiesの送信元とアプリパスワードを設定してください。"
            );
        }

        Session session = createSession();

        MimeMessage message = createMessage(
            session, from, reservation
        );

        // Gmailへ接続して送信
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect(
                "smtp.gmail.com", 587, from, password
            );

            transport.sendMessage(
                message, message.getAllRecipients()
            );
        }
    }

    // SMTP設定
    public static Session createSession() {
        Properties properties = new Properties();

        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");

        properties.setProperty(
            "mail.smtp.starttls.enable", "true"
        );
        properties.setProperty(
            "mail.smtp.starttls.required", "true"
        );
        properties.setProperty(
            "mail.smtp.ssl.checkserveridentity", "true"
        );

        properties.setProperty(
            "mail.smtp.connectiontimeout", "10000"
        );
        properties.setProperty(
            "mail.smtp.timeout", "15000"
        );
        properties.setProperty(
            "mail.smtp.writetimeout", "15000"
        );

        return Session.getInstance(properties);
    }

    // 本文・QR画像・添付ファイルを組み立てる
    public static MimeMessage createMessage(
            Session session,
            String from,
            Map<String, Object> reservation) throws Exception {

        int id = ((Number) reservation.get(
            "reservationId")).intValue();

        if (id <= 0) {
            throw new IllegalArgumentException(
                "予約番号が不正です。"
            );
        }

        String number = String.format(
            Locale.ROOT, "%06d", id
        );

        InternetAddress sender = new InternetAddress(from, true);
        sender.validate();
        sender.setPersonal("O-HARAFILM", "UTF-8");

        InternetAddress recipient = new InternetAddress(
            text(reservation, "email"), true
        );
        recipient.validate();

        MimeMessage message = new MimeMessage(session);

        message.setFrom(sender);
        message.setRecipient(Message.RecipientType.TO, recipient);
        message.setSentDate(new Date());

        message.setSubject(
            "【O-HARAFILM】予約完了のお知らせ（" + number + "）",
            "UTF-8"
        );

        String payment =
            "card".equals(text(reservation, "paymentMethod"))
                ? "クレジットカード（決済処理は未実装）"
                : "窓口支払い";

        String price = String.format(
            Locale.JAPAN,
            "%,d",
            ((Number) reservation.get("totalPrice")).intValue()
        );

        String details =
            text(reservation, "customerName") + " 様\n\n"
            + "ご予約ありがとうございます。\n"
            + "以下の内容で受け付けました。\n\n"
            + "予約番号：" + number + "\n"
            + "作品名：" + text(reservation, "movieTitle") + "\n"
            + "上映日時：" + text(reservation, "showtime") + "\n"
            + "スクリーン：" + text(reservation, "screenNo") + "\n"
            + "座席：" + text(reservation, "seats") + "\n"
            + "券種：" + text(reservation, "ticketInfo") + "\n"
            + "合計金額：" + price + "円\n"
            + "支払い方法：" + payment + "\n\n"
            + "予約QRコードを本文と添付画像でお送りします。\n"
            + "現在のQRは予約番号のみを含み、"
            + "読取後のページは未実装です。\n"
            + "このメールは学校での開発用です。\n\n"
            + "O-HARAFILM";

        // テキスト形式の本文
        MimeBodyPart plain = new MimeBodyPart();
        plain.setText(details, "UTF-8");

        // HTML形式の本文
        MimeBodyPart html = new MimeBodyPart();

        html.setContent(
            "<!doctype html>"
            + "<html lang=\"ja\"><body>"
            + "<h2>ご予約が完了しました</h2>"
            + "<p>" + escape(text(reservation, "customerName")) + " 様</p>"
            + "<p>ご予約ありがとうございます。以下の内容で受け付けました。</p>"

            + "<ul style=\"line-height:2;\">"
            + "<li>予約番号：" + escape(number) + "</li>"
            + "<li>作品名：" + escape(text(reservation, "movieTitle")) + "</li>"
            + "<li>上映日時：" + escape(text(reservation, "showtime")) + "</li>"
            + "<li>スクリーン：" + escape(text(reservation, "screenNo")) + "</li>"
            + "<li>座席：" + escape(text(reservation, "seats")) + "</li>"
            + "<li>券種：" + escape(text(reservation, "ticketInfo")) + "</li>"
            + "<li>合計金額：" + escape(price) + "円</li>"
            + "<li>支払い方法：" + escape(payment) + "</li>"
            + "</ul>"

            + "<h3>予約QRコード</h3>"
            + "<p><img src=\"cid:reservation-qr\""
            + " width=\"320\" height=\"320\" alt=\"予約QRコード\"></p>"
            + "<p>O-HARAFILM</p>"
            + "</body></html>",

            "text/html; charset=UTF-8"
        );

        MimeMultipart alternative =
            new MimeMultipart("alternative");

        alternative.addBodyPart(plain);
        alternative.addBodyPart(html);

        MimeBodyPart body = new MimeBodyPart();
        body.setContent(alternative);

        // QRには予約番号を入れる
        byte[] png = createQr(
            "OHARAFILM:RESERVATION:" + number
        );

        // 本文内に表示するQR画像
        MimeBodyPart inlineQr = image(png);
        inlineQr.setDisposition(MimeBodyPart.INLINE);
        inlineQr.setHeader(
            "Content-ID", "<reservation-qr>"
        );

        MimeMultipart related = new MimeMultipart("related");
        related.addBodyPart(body);
        related.addBodyPart(inlineQr);

        MimeBodyPart relatedBody = new MimeBodyPart();
        relatedBody.setContent(related);

        // 添付するQR画像
        MimeBodyPart attachment = image(png);
        attachment.setDisposition(MimeBodyPart.ATTACHMENT);
        attachment.setFileName(
            "reservation-" + number + ".png"
        );

        MimeMultipart mixed = new MimeMultipart("mixed");
        mixed.addBodyPart(relatedBody);
        mixed.addBodyPart(attachment);

        message.setContent(mixed);
        message.saveChanges();

        return message;
    }

    // QRコードをPNG形式で生成
    public static byte[] createQr(String content) throws Exception {

        BitMatrix matrix = new MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            320,
            320,
            Map.of(
                EncodeHintType.CHARACTER_SET,
                StandardCharsets.UTF_8.name(),
                EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN,
                4
            )
        );

        BufferedImage image = new BufferedImage(
            320, 320, BufferedImage.TYPE_INT_RGB
        );

        for (int y = 0; y < 320; y++) {
            for (int x = 0; x < 320; x++) {
                image.setRGB(
                    x,
                    y,
                    matrix.get(x, y) ? 0x000000 : 0xFFFFFF
                );
            }
        }

        try (ByteArrayOutputStream output =
                new ByteArrayOutputStream()) {

            if (!ImageIO.write(image, "png", output)) {
                throw new IllegalStateException(
                    "QR画像を生成できませんでした。"
                );
            }

            return output.toByteArray();
        }
    }

    private static MimeBodyPart image(byte[] png) throws Exception {
        MimeBodyPart part = new MimeBodyPart();

        part.setDataHandler(
            new DataHandler(
                new ByteArrayDataSource(png, "image/png")
            )
        );

        return part;
    }

    private static String text(
            Map<String, Object> values, String key) {

        Object value = values.get(key);

        if (value == null) {
            throw new IllegalArgumentException(
                "メール情報が不足しています：" + key
            );
        }

        return value.toString();
    }

    // 入力された文字をHTMLとして実行させない
    private static String escape(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}