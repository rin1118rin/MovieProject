package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.ReservationSeat;

/**
 * 予約座席テーブル(RESERVATION_SEATS)を読み書きするクラス。
 */
public class ReservationSeatDao extends Dao {

    /** SQL標準で「一意制約の違反(重複)」を表すSQLState */
    private static final String SQLSTATE_DUPLICATE = "23505";

    /**
     * 新規登録。自動採番された予約座席IDは、渡された seat にセットされる。
     * 同じスケジュールの同じ座席がすでに予約されている場合は、例外にせず false を返す。
     */
    public boolean save(ReservationSeat seat) throws Exception {
        String sql = "INSERT INTO RESERVATION_SEATS (RESERVATION_ID, SCHEDULE_ID, SEAT_NO) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql, new String[] {"RESERVATION_SEAT_ID"})) {
            st.setInt(1, seat.getReservationId());
            st.setInt(2, seat.getScheduleId());
            st.setString(3, seat.getSeatNo());
            int count = st.executeUpdate();
            if (count > 0) {
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        seat.setReservationSeatId(keys.getInt(1));
                    }
                }
            }
            return count > 0;
        } catch (SQLException e) {
            if (SQLSTATE_DUPLICATE.equals(e.getSQLState())) {
                return false;   // 座席の二重予約
            }
            throw e;
        }
    }
}