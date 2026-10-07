package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.Admin;

//管理者テーブル(ADMINS)を読み書きするクラス。
public class AdminDao extends Dao {
    // SQL標準で「一意制約の違反(重複)」を表すSQLState
    private static final String SQLSTATE_DUPLICATE = "23505";

    /** 管理者IDで1件取得(なければ null) */
    public Admin get(int adminId) throws Exception {
        String sql = "SELECT ADMIN_ID, PASSWORD FROM ADMINS WHERE ADMIN_ID = ?";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, adminId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminId(rs.getInt("ADMIN_ID"));
                    admin.setPassword(rs.getString("PASSWORD"));
                    return admin;
                }
                return null;
            }
        }
    }

    /**
     * 新規登録。管理者IDは自動採番ではなく、渡された値をそのまま使う。
     * パスワードは「ハッシュ化した値」を渡すこと(このクラスでは変換しない)。
     * 同じ管理者IDがすでにある場合は、例外にせず false を返す。
     */
    public boolean save(Admin admin) throws Exception {
        String sql = "INSERT INTO ADMINS(ADMIN_ID, PASSWORD) VALUES (?, ?)";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, admin.getAdminId());
            st.setString(2, admin.getPassword());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_DUPLICATE.equals(e.getSQLState())) {
                return false;
            }
            throw e;
        }
    }
}
