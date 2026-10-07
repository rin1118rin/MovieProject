package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import bean.Inquiry;

/**
 * お問い合わせテーブル(INQUIRIES)を読み書きするクラス。
 */
public class InquiryDao extends Dao {

    private static final String COLUMNS = "INQUIRY_ID, NAME, EMAIL, SUBJECT, BODY, SENT_AT, STATUS";

    /** 対応状況の初期値(テーブル定義書の初期値と同じ) */
    public static final String STATUS_NOT_YET = "未対応";

    private Inquiry toBean(ResultSet rs) throws Exception {
        Inquiry i = new Inquiry();
        i.setInquiryId(rs.getInt("INQUIRY_ID"));
        i.setName(rs.getString("NAME"));
        i.setEmail(rs.getString("EMAIL"));
        i.setSubject(rs.getString("SUBJECT"));
        i.setBody(rs.getString("BODY"));
        i.setSentAt(rs.getTimestamp("SENT_AT"));
        i.setStatus(rs.getString("STATUS"));
        return i;
    }

    /** 1件取得(なければ null) */
    public Inquiry get(int inquiryId) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM INQUIRIES WHERE INQUIRY_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, inquiryId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? toBean(rs) : null;
            }
        }
    }

    /**
     * 新規登録(お問い合わせ送信)。自動採番されたIDは、渡された inquiry にセットされる。
     * 送信日時が null なら現在日時、対応状況が null なら「未対応」を入れる。
     */
    public boolean save(Inquiry inquiry) throws Exception {
        Date sentAt = inquiry.getSentAt() != null ? inquiry.getSentAt() : new Date();
        String status = inquiry.getStatus() != null ? inquiry.getStatus() : STATUS_NOT_YET;

        String sql = "INSERT INTO INQUIRIES (NAME, EMAIL, SUBJECT, BODY, SENT_AT, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql, new String[] {"INQUIRY_ID"})) {
            st.setString(1, inquiry.getName());
            st.setString(2, inquiry.getEmail());
            st.setString(3, inquiry.getSubject());
            st.setString(4, inquiry.getBody());
            st.setTimestamp(5, new Timestamp(sentAt.getTime()));
            st.setString(6, status);
            int count = st.executeUpdate();
            if (count > 0) {
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        inquiry.setInquiryId(keys.getInt(1));
                    }
                }
                inquiry.setSentAt(sentAt);
                inquiry.setStatus(status);
            }
            return count > 0;
        }
    }

    /** 対応状況だけを更新(返信を登録したあとに「対応済み」などへ変えるのに使う) */
    public boolean updateStatus(int inquiryId, String status) throws Exception {
        String sql = "UPDATE INQUIRIES SET STATUS = ? WHERE INQUIRY_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, status);
            st.setInt(2, inquiryId);
            return st.executeUpdate() > 0;
        }
    }
}