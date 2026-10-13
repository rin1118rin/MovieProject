package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReservationRegisterDao extends Dao {

    public int register(
            Map<String, Object> reservation,
            List<String> seats,
            int[] quantities) throws Exception {

        String reservationSql =
            "INSERT INTO RESERVATIONS "
            + "(SCHEDULE_ID, CUSTOMER_NAME, CUSTOMER_KANA, EMAIL, PHONE, "
            + "PAYMENT_METHOD, TOTAL_PRICE, RESERVED_AT, STATUS) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

        String seatSql =
            "INSERT INTO RESERVATION_SEATS "
            + "(RESERVATION_ID, SCHEDULE_ID, SEAT_NO) VALUES (?, ?, ?)";

        String ticketSql =
            "INSERT INTO RESERVATION_TICKETS "
            + "(RESERVATION_ID, TICKET_TYPE, QUANTITY, UNIT_PRICE) "
            + "VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try {
                int reservationId;

                // 予約情報を保存
                try (PreparedStatement st = con.prepareStatement(
                        reservationSql,
                        new String[] { "RESERVATION_ID" })) {

                    st.setInt(1, (Integer) reservation.get("scheduleId"));
                    st.setString(2, (String) reservation.get("customerName"));
                    st.setString(3, (String) reservation.get("customerKana"));
                    st.setString(4, (String) reservation.get("email"));
                    st.setString(5, (String) reservation.get("phone"));
                    st.setString(6, (String) reservation.get("paymentMethod"));
                    st.setInt(7, (Integer) reservation.get("totalPrice"));
                    st.setString(8, "予約済み");

                    if (st.executeUpdate() != 1) {
                        throw new SQLException("予約を登録できませんでした。");
                    }

                    try (ResultSet keys = st.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("予約番号を取得できませんでした。");
                        }
                        reservationId = keys.getInt(1);
                    }
                }

                // 座席を保存
                try (PreparedStatement st = con.prepareStatement(seatSql)) {
                    for (String seat : seats) {
                        st.setInt(1, reservationId);
                        st.setInt(2, (Integer) reservation.get("scheduleId"));
                        st.setString(3, seat);
                        st.executeUpdate();
                    }
                }

                // 券種ごとの枚数と単価を保存
                String[] types = { "一般", "学生", "シニア" };
                int[] prices = { 1900, 1500, 1200 };

                try (PreparedStatement st = con.prepareStatement(ticketSql)) {
                    for (int i = 0; i < types.length; i++) {
                        if (quantities[i] == 0) {
                            continue;
                        }

                        st.setInt(1, reservationId);
                        st.setString(2, types[i]);
                        st.setInt(3, quantities[i]);
                        st.setInt(4, prices[i]);
                        st.executeUpdate();
                    }
                }

                con.commit();
                return reservationId;

            } catch (Exception e) {
                // 途中で失敗したら全部取り消す
                con.rollback();
                throw e;
            }
        }
    }
}