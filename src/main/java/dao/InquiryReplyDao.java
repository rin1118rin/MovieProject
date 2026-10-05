package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import bean.InquiryReply;

/**
 * お問い合わせ返信テーブル(INQUIRY_REPLIES)を読み書きするクラス。
 */
public class InquiryReplyDao extends Dao {

    /**
     * 新規登録。自動採番された返信IDは、渡された reply にセットされる。
     * 返信日時が null なら現在日時を入れる。
     */
    public boolean save(InquiryReply reply) throws Exception {
        Date repliedAt = reply.getRepliedAt() != null ? reply.getRepliedAt() : new Date();

        String sql = "INSERT INTO INQUIRY_REPLIES (INQUIRY_ID, REPLY_BODY, STAFF_NAME, REPLIED_AT) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql, new String[] {"REPLY_ID"})) {
            st.setInt(1, reply.getInquiryId());
            st.setString(2, reply.getReplyBody());
            st.setString(3, reply.getStaffName());
            st.setTimestamp(4, new Timestamp(repliedAt.getTime()));
            int count = st.executeUpdate();
            if (count > 0) {
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        reply.setReplyId(keys.getInt(1));
                    }
                }
                reply.setRepliedAt(repliedAt);
            }
            return count > 0;
        }
    }
}