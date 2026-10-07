package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.InquiryReply;

/**
 * お問い合わせ返信テーブル(INQUIRY_REPLIES)を読み書きするクラス。
 */
public class InquiryReplyDao extends Dao {

    private static final String COLUMNS = "REPLY_ID, INQUIRY_ID, REPLY_BODY, STAFF_NAME, REPLIED_AT";

    private InquiryReply toBean(ResultSet rs) throws Exception {
        InquiryReply r = new InquiryReply();
        r.setReplyId(rs.getInt("REPLY_ID"));
        r.setInquiryId(rs.getInt("INQUIRY_ID"));
        r.setReplyBody(rs.getString("REPLY_BODY"));
        r.setStaffName(rs.getString("STAFF_NAME"));
        r.setRepliedAt(rs.getTimestamp("REPLIED_AT"));
        return r;
    }

    /** 指定したお問い合わせへの返信を、返信日時の古い順に取得(なければ空のリスト) */
    public List<InquiryReply> getByInquiryId(int inquiryId) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM INQUIRY_REPLIES WHERE INQUIRY_ID = ?"
                   + " ORDER BY REPLIED_AT, REPLY_ID";
        List<InquiryReply> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, inquiryId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(toBean(rs));
                }
            }
        }
        return list;
    }

    /**
     * 新規登録。自動採番された返信IDは、渡された reply にセットされる。
     * 返信日時が null なら現在日時を入れる。
     * ※ お問い合わせの対応状況は変わらない。返信と同時に変えたいときは saveAndUpdateStatus を使う。
     */
    public boolean save(InquiryReply reply) throws Exception {
        Date repliedAt = reply.getRepliedAt() != null ? reply.getRepliedAt() : new Date();
        try (Connection con = getConnection()) {
            boolean ok = insert(con, reply, repliedAt);
            if (ok) {
                reply.setRepliedAt(repliedAt);
            }
            return ok;
        }
    }

    /**
     * 返信の登録と、お問い合わせの対応状況の更新を、1つのまとまりとして行う。
     * どちらかが失敗したら両方とも取り消す(返信だけ残る、状況だけ変わる、を防ぐ)。
     * status には「対応済み」など、テーブル定義書で決めた値を渡す。
     */
    public boolean saveAndUpdateStatus(InquiryReply reply, String status) throws Exception {
        Date repliedAt = reply.getRepliedAt() != null ? reply.getRepliedAt() : new Date();
        try (Connection con = getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                boolean ok = insert(con, reply, repliedAt)
                          && new InquiryDao().updateStatus(con, reply.getInquiryId(), status);
                if (ok) {
                    con.commit();
                    reply.setRepliedAt(repliedAt);
                } else {
                    con.rollback();
                    reply.setReplyId(0);
                }
                return ok;
            } catch (Exception e) {
                con.rollback();
                reply.setReplyId(0);
                throw e;
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        }
    }

    /** 渡された接続で返信を1件登録する(接続は閉じない)。成功したら返信IDを reply にセットする */
    private boolean insert(Connection con, InquiryReply reply, Date repliedAt) throws Exception {
        String sql = "INSERT INTO INQUIRY_REPLIES (INQUIRY_ID, REPLY_BODY, STAFF_NAME, REPLIED_AT)"
                   + " VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(sql, new String[] {"REPLY_ID"})) {
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
            }
            return count > 0;
        }
    }
}