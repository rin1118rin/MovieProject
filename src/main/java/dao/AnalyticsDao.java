package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.Analytics;

/**
 * 分析画面用の集計データを取得するクラス(専用のテーブルはなく、他のテーブルから集計する)。
 */
public class AnalyticsDao extends Dao {

    /**
     * 1スクリーンの総座席数。【仮の値】
     * 総座席数を持つテーブルがまだないため、決まるまでの仮置き。
     * 稼働率の計算に使うので、実際の座席数に合わせて変更すること。
     */
    public static final int TOTAL_SEATS = 100;

    /**
     * 上映スケジュールごとの座席稼働率を取得(上映開始日時の順)。
     * 予約済みの予約の座席だけを数える(取消済みは除く)。
     * occupancyRate は「予約済み座席数 ÷ 総座席数 × 100」(%)。
     */
    public List<Analytics> getOccupancyRate() throws Exception {
        String sql = "SELECT s.SCHEDULE_ID, m.TITLE, COUNT(seat.RESERVATION_SEAT_ID) AS RESERVED_SEATS"
                + " FROM SCREENING_SCHEDULES s"
                + " JOIN MOVIES m ON m.MOVIE_ID = s.MOVIE_ID"
                + " LEFT JOIN RESERVATIONS r ON r.SCHEDULE_ID = s.SCHEDULE_ID AND r.STATUS = ?"
                + " LEFT JOIN RESERVATION_SEATS seat ON seat.RESERVATION_ID = r.RESERVATION_ID"
                + " GROUP BY s.SCHEDULE_ID, m.TITLE, s.START_DATETIME"
                + " ORDER BY s.START_DATETIME, s.SCHEDULE_ID";
        List<Analytics> list = new ArrayList<>();
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, ReservationDao.STATUS_RESERVED);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Analytics a = new Analytics();
                    a.setScheduleId(rs.getInt("SCHEDULE_ID"));
                    a.setMovieTitle(rs.getString("TITLE"));
                    int reserved = rs.getInt("RESERVED_SEATS");
                    a.setReservedSeats(reserved);
                    a.setTotalSeats(TOTAL_SEATS);
                    a.setOccupancyRate(reserved * 100.0 / TOTAL_SEATS);
                    list.add(a);
                }
            }
        }
        return list;
    }

    /**
     * 客層データを取得。【未実装】
     * 男女比(maleRatio / femaleRatio)を出すための「性別」などの列が、
     * 現在のテーブルにないため作れない。テーブルを決めてから実装すること。
     */
    public List<Analytics> getDemographics() throws Exception {
        throw new UnsupportedOperationException("客層データ: 性別などを保存する列がテーブルにないため未実装");
    }
}