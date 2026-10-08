package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.ReservationSeat;

/**
 * 予約座席テーブル(RESERVATION_SEATS)を読み書きするクラス。
 */
public class ReservationSeatDao extends Dao {

    /** SQL標準で「一意制約の違反(重複)」を表すSQLState */
    private static final String SQLSTATE_DUPLICATE = "23505";

    /**
     * 指定した上映スケジュールで、すでに予約されている座席番号の一覧を取得(座席番号の順)。
     * 座席選択画面で「選べない席」を表示するのに使う。
     * 予約を取り消すと座席の行も削除されるので、ここに出る席はすべて「いま予約中」の席。
     */
    public List<String> getReservedSeatNos(int scheduleId) throws Exception {
        String sql = "SELECT SEAT_NO FROM RESERVATION_SEATS WHERE SCHEDULE_ID = ? ORDER BY SEAT_NO";
        List<String> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, scheduleId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("SEAT_NO"));
                }
            }
        }
        return list;
    }

    /**
     * 新規登録。自動採番された予約座席IDは、渡された seat にセットされる。
     * 同じスケジュールの同じ座席がすでに予約されている場合は、例外にせず false を返す。
     * ※ 予約と座席を一緒に登録するときは、まとめて取り消せる ReservationDao.saveWithSeats を使う。
     */
    public boolean save(ReservationSeat seat) throws Exception {
        String sql = "INSERT INTO RESERVATION_SEATS (RESERVATION_ID, SCHEDULE_ID, SEAT_NO) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql, new String[] {"RESERVATION_SEAT_ID"})) {
            st.setInt(1, seat.getReservationId());
            st.setInt(2, seat.getSchedule().getScheduleId());
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