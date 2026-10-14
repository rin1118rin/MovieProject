package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Reservation;

public class ReservationDao extends Dao {

    public static final String STATUS_RESERVED = "予約済み";
    public static final String STATUS_CANCELED = "取消済み";

    // TICKET_TYPEは別テーブルなので、ここには含めない
    private static final String COLUMNS =
        "RESERVATION_ID, SCHEDULE_ID, TOTAL_PRICE, RESERVED_AT, STATUS";

    private Reservation toBean(ResultSet rs) throws Exception {
        Reservation reservation = new Reservation();

        reservation.setReservationId(
            rs.getInt("RESERVATION_ID")
        );
        reservation.setScheduleId(
            rs.getInt("SCHEDULE_ID")
        );
        reservation.setTotalPrice(
            rs.getInt("TOTAL_PRICE")
        );
        reservation.setReservedAt(
            rs.getTimestamp("RESERVED_AT")
        );
        reservation.setStatus(
            rs.getString("STATUS")
        );

        return reservation;
    }

    // 予約番号で予約を取得。存在しなければnull
    public Reservation get(int reservationId) throws Exception {

        String sql =
            "SELECT " + COLUMNS
            + " FROM RESERVATIONS WHERE RESERVATION_ID = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, reservationId);

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? toBean(rs) : null;
            }
        }
    }

    // 予約番号とメールアドレスが一致する予約を取得
    public Reservation get(
            int reservationId, String email) throws Exception {

        if (email == null || email.isBlank()) {
            return null;
        }

        String sql =
            "SELECT " + COLUMNS
            + " FROM RESERVATIONS"
            + " WHERE RESERVATION_ID = ? AND EMAIL = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, reservationId);
            st.setString(2, email.trim());

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? toBean(rs) : null;
            }
        }
    }

    // メールアドレスを照合して予約を取り消す
    public boolean cancel(
            int reservationId, String email) throws Exception {

        if (reservationId <= 0 || email == null || email.isBlank()) {
            return false;
        }

        String updateSql =
            "UPDATE RESERVATIONS SET STATUS = ?"
            + " WHERE RESERVATION_ID = ?"
            + " AND EMAIL = ?"
            + " AND STATUS = ?";

        String deleteSql =
            "DELETE FROM RESERVATION_SEATS"
            + " WHERE RESERVATION_ID = ?";

        try (Connection con = getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                // 予約番号・メール・予約状態が一致した場合だけ更新
                try (PreparedStatement st =
                        con.prepareStatement(updateSql)) {

                    st.setString(1, STATUS_CANCELED);
                    st.setInt(2, reservationId);
                    st.setString(3, email.trim());
                    st.setString(4, STATUS_RESERVED);

                    if (st.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                // 座席を解放
                try (PreparedStatement st =
                        con.prepareStatement(deleteSql)) {

                    st.setInt(1, reservationId);
                    st.executeUpdate();
                }

                // 予約情報と券種情報は履歴として残す
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

    // 予約番号とメールアドレスから映画名を取得
    public String getMovieTitle(
            int reservationId, String email) throws Exception {

        String sql =
            "SELECT M.TITLE "
            + "FROM RESERVATIONS R "
            + "JOIN SCREENING_SCHEDULES S "
            + "ON R.SCHEDULE_ID = S.SCHEDULE_ID "
            + "JOIN MOVIES M ON S.MOVIE_ID = M.MOVIE_ID "
            + "WHERE R.RESERVATION_ID = ? AND R.EMAIL = ?";

        try (Connection con = getConnection();
            PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, reservationId);
            st.setString(2, email.trim());

            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getString("TITLE") : null;
            }
        }
    }
    
 // 管理者用 指定したスケジュールの全予約情報を取得
    public List<Reservation> getReservationsByScheduleId(int scheduleId) throws Exception {
        List<Reservation> reservationList = new ArrayList<>();
        
        String sql = "SELECT RESERVATION_ID, SCHEDULE_ID, CUSTOMER_NAME, TOTAL_PRICE, RESERVED_AT, STATUS "
                   + "FROM RESERVATIONS WHERE SCHEDULE_ID = ?";

        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, scheduleId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    int resId = rs.getInt("RESERVATION_ID");
                    r.setReservationId(resId);
                    r.setScheduleId(rs.getInt("SCHEDULE_ID"));
                    r.setCustomerName(rs.getString("CUSTOMER_NAME"));
                    r.setTotalPrice(rs.getInt("TOTAL_PRICE"));
                    r.setReservedAt(rs.getTimestamp("RESERVED_AT"));
                    r.setStatus(rs.getString("STATUS"));

                    // 予約の座席番号一覧を取得
                    List<String> seats = new ArrayList<>();
                    String seatSql = "SELECT SEAT_NO FROM RESERVATION_SEATS WHERE RESERVATION_ID = ?";
                    try (PreparedStatement seatSt = con.prepareStatement(seatSql)) {
                        seatSt.setInt(1, resId);
                        try (ResultSet seatRs = seatSt.executeQuery()) {
                            while (seatRs.next()) {
                                seats.add(seatRs.getString("SEAT_NO"));
                            }
                        }
                    }
                    r.setSeats(seats);

                    // 予約の券種・数量一覧を取得
                    List<Reservation.TicketInfo> tickets = new ArrayList<>();
                    String ticketSql = "SELECT TICKET_TYPE, QUANTITY FROM RESERVATION_TICKETS WHERE RESERVATION_ID = ? AND QUANTITY > 0";
                    try (PreparedStatement ticketSt = con.prepareStatement(ticketSql)) {
                        ticketSt.setInt(1, resId);
                        try (ResultSet ticketRs = ticketSt.executeQuery()) {
                            while (ticketRs.next()) {
                                tickets.add(new Reservation.TicketInfo(
                                    ticketRs.getString("TICKET_TYPE"),
                                    ticketRs.getInt("QUANTITY")
                                ));
                            }
                        }
                    }
                    r.setTickets(tickets);

                    reservationList.add(r);
                }
            }
        }
        return reservationList;
    }

}