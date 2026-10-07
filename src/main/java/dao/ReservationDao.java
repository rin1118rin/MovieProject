package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import bean.Reservation;

/**
 * 予約テーブル(RESERVATIONS)を読み書きするクラス。
 */
public class ReservationDao extends Dao {

    private static final String COLUMNS =
        "RESERVATION_ID, SCHEDULE_ID, TICKET_TYPE, TOTAL_PRICE, RESERVED_AT, STATUS";

    /** 予約状態の初期値(テーブル定義書の初期値と同じ) */
    public static final String STATUS_RESERVED = "予約済み";

    /** 取消後の予約状態(テーブル定義書の「予約済み・取消済み等」の記載にならった値) */
    public static final String STATUS_CANCELED = "取消済み";

    /** SQL標準で「一意制約の違反(重複)」を表すSQLState */
    private static final String SQLSTATE_DUPLICATE = "23505";

    private Reservation toBean(ResultSet rs) throws Exception {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("RESERVATION_ID"));
        r.setScheduleId(rs.getInt("SCHEDULE_ID"));
        r.setTicketType(rs.getString("TICKET_TYPE"));
        r.setTotalPrice(rs.getInt("TOTAL_PRICE"));
        r.setReservedAt(rs.getTimestamp("RESERVED_AT"));
        r.setStatus(rs.getString("STATUS"));
        return r;
    }

    /** 1件取得(なければ null) */
    public Reservation get(int reservationId) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM RESERVATIONS WHERE RESERVATION_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, reservationId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? toBean(rs) : null;
            }
        }
    }

    /**
     * 新規登録。自動採番された予約IDは、渡された reservation にセットされる。
     * 予約日時が null なら現在日時、予約状態が null なら「予約済み」を入れる。
     * ※ 座席は登録されない。予約と座席を一緒に登録するときは saveWithSeats を使う。
     */
    public boolean save(Reservation reservation) throws Exception {
        Date reservedAt = reservation.getReservedAt() != null ? reservation.getReservedAt() : new Date();
        String status = reservation.getStatus() != null ? reservation.getStatus() : STATUS_RESERVED;
        try (Connection con = getConnection()) {
            boolean ok = insert(con, reservation, reservedAt, status);
            if (ok) {
                reservation.setReservedAt(reservedAt);
                reservation.setStatus(status);
            }
            return ok;
        }
    }

    /**
     * 予約と座席を、1つのまとまりとして登録する。
     * どれか1席でも失敗したら、予約も他の座席も全部取り消す(予約だけ残る、を防ぐ)。
     * 座席の番号(seatNos)は、reservation の上映スケジュールの座席として登録される。
     * 同じ上映の同じ座席がすでに予約されている場合は、例外にせず false を返す。
     * 成功すると、自動採番された予約IDが reservation にセットされる。
     */
    public boolean saveWithSeats(Reservation reservation, List<String> seatNos) throws Exception {
        if (seatNos == null || seatNos.isEmpty()) {
            return false;
        }
        Date reservedAt = reservation.getReservedAt() != null ? reservation.getReservedAt() : new Date();
        String status = reservation.getStatus() != null ? reservation.getStatus() : STATUS_RESERVED;

        try (Connection con = getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                if (!insert(con, reservation, reservedAt, status)) {
                    con.rollback();
                    reservation.setReservationId(0);
                    return false;
                }
                String seatSql = "INSERT INTO RESERVATION_SEATS (RESERVATION_ID, SCHEDULE_ID, SEAT_NO)"
                               + " VALUES (?, ?, ?)";
                try (PreparedStatement st = con.prepareStatement(seatSql)) {
                    for (String seatNo : seatNos) {
                        st.setInt(1, reservation.getReservationId());
                        st.setInt(2, reservation.getScheduleId());
                        st.setString(3, seatNo);
                        st.executeUpdate();
                    }
                }
                con.commit();
                reservation.setReservedAt(reservedAt);
                reservation.setStatus(status);
                return true;
            } catch (SQLException e) {
                con.rollback();
                reservation.setReservationId(0);
                if (SQLSTATE_DUPLICATE.equals(e.getSQLState())) {
                    return false;   // 座席の二重予約
                }
                throw e;
            } catch (Exception e) {
                con.rollback();
                reservation.setReservationId(0);
                throw e;
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        }
    }

    /**
     * 更新(予約IDで特定)。
     * reservation には、get で取得した内容を変更したものを渡すこと(予約日時が null だとエラーになる)。
     * ※ 予約の取消には、座席も解放する cancel を使う。
     */
    public boolean update(Reservation reservation) throws Exception {
        String sql = "UPDATE RESERVATIONS"
                   + " SET SCHEDULE_ID = ?, TICKET_TYPE = ?, TOTAL_PRICE = ?, RESERVED_AT = ?, STATUS = ?"
                   + " WHERE RESERVATION_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, reservation.getScheduleId());
            st.setString(2, reservation.getTicketType());
            st.setInt(3, reservation.getTotalPrice());
            st.setTimestamp(4, new Timestamp(reservation.getReservedAt().getTime()));
            st.setString(5, reservation.getStatus());
            st.setInt(6, reservation.getReservationId());
            return st.executeUpdate() > 0;
        }
    }

    /**
     * 予約を取り消す。予約状態を「取消済み」にして、その予約の座席を削除する(座席が空く)。
     * 2つの処理は1つのまとまりで、片方だけ実行されることはない。
     * 予約がない場合や、すでに取消済みの場合は何もせず false を返す。
     */
    public boolean cancel(int reservationId) throws Exception {
        try (Connection con = getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                int updated;
                String updateSql = "UPDATE RESERVATIONS SET STATUS = ? WHERE RESERVATION_ID = ? AND STATUS = ?";
                try (PreparedStatement st = con.prepareStatement(updateSql)) {
                    st.setString(1, STATUS_CANCELED);
                    st.setInt(2, reservationId);
                    st.setString(3, STATUS_RESERVED);
                    updated = st.executeUpdate();
                }
                if (updated == 0) {
                    con.rollback();
                    return false;
                }
                String deleteSql = "DELETE FROM RESERVATION_SEATS WHERE RESERVATION_ID = ?";
                try (PreparedStatement st = con.prepareStatement(deleteSql)) {
                    st.setInt(1, reservationId);
                    st.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        }
    }

    /** 渡された接続で予約を1件登録する(接続は閉じない)。成功したら予約IDを reservation にセットする */
    private boolean insert(Connection con, Reservation reservation, Date reservedAt, String status)
            throws Exception {
        String sql = "INSERT INTO RESERVATIONS (SCHEDULE_ID, TICKET_TYPE, TOTAL_PRICE, RESERVED_AT, STATUS)"
                   + " VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(sql, new String[] {"RESERVATION_ID"})) {
            st.setInt(1, reservation.getScheduleId());
            st.setString(2, reservation.getTicketType());
            st.setInt(3, reservation.getTotalPrice());
            st.setTimestamp(4, new Timestamp(reservedAt.getTime()));
            st.setString(5, status);
            int count = st.executeUpdate();
            if (count > 0) {
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        reservation.setReservationId(keys.getInt(1));
                    }
                }
            }
            return count > 0;
        }
    }
}