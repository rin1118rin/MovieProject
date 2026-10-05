package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import bean.Reservation;

/**
 * 予約テーブル(RESERVATIONS)を読み書きするクラス。
 */
public class ReservationDao extends Dao {

    private static final String COLUMNS =
        "RESERVATION_ID, SCHEDULE_ID, TICKET_TYPE, TOTAL_PRICE, RESERVED_AT, STATUS";

    /** 予約状態の初期値(テーブル定義書の初期値と同じ) */
    public static final String STATUS_RESERVED = "予約済み";

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
     * 新規登録。自動採番された予約IDは、渡された reservation にセットされる
     * (続けて予約座席を登録するときに使う)。
     * 予約日時が null なら現在日時、予約状態が null なら「予約済み」を入れる。
     */
    public boolean save(Reservation reservation) throws Exception {
        Date reservedAt = reservation.getReservedAt() != null ? reservation.getReservedAt() : new Date();
        String status = reservation.getStatus() != null ? reservation.getStatus() : STATUS_RESERVED;

        String sql = "INSERT INTO RESERVATIONS (SCHEDULE_ID, TICKET_TYPE, TOTAL_PRICE, RESERVED_AT, STATUS)"
                   + " VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql, new String[] {"RESERVATION_ID"})) {
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
                reservation.setReservedAt(reservedAt);
                reservation.setStatus(status);
            }
            return count > 0;
        }
    }

    /** 更新(予約IDで特定)。予約取消では、予約状態を変えるのに使う */
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
}