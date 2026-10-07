package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.Schedule;

/**
 * 上映スケジュールテーブル(SCREENING_SCHEDULES)を読み書きするクラス。
 */
public class ScheduleDao extends Dao {

    /** SQL標準で「外部キー制約の違反(参照している行がある)」を表すSQLState */
    private static final String SQLSTATE_FK_VIOLATION = "23503";

    private static final String COLUMNS =
        "SCHEDULE_ID, MOVIE_ID, SCREEN_NO, START_DATETIME, END_DATETIME, SCREENING_FORMAT";

    /** 検索結果の1行を Schedule に詰め替える */
    private Schedule toBean(ResultSet rs) throws Exception {
        Schedule s = new Schedule();
        s.setScheduleId(rs.getInt("SCHEDULE_ID"));
        s.setMovieId(rs.getInt("MOVIE_ID"));
        s.setScreenNo(rs.getInt("SCREEN_NO"));
        s.setStartDatetime(rs.getTimestamp("START_DATETIME"));
        s.setEndDatetime(rs.getTimestamp("END_DATETIME"));
        s.setScreeningFormat(rs.getString("SCREENING_FORMAT"));
        return s;
    }

    /** 1件取得(なければ null) */
    public Schedule get(int scheduleId) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM SCREENING_SCHEDULES WHERE SCHEDULE_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, scheduleId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? toBean(rs) : null;
            }
        }
    }

    /**
     * 指定日(0:00〜24:00)に上映が始まるスケジュールを、開始時刻順に取得。
     * date は java.util.Date でも java.sql.Date でもよい。
     */
    public List<Schedule> getByDate(Date date) throws Exception {
        LocalDate day = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        Timestamp from = Timestamp.valueOf(day.atStartOfDay());
        Timestamp to = Timestamp.valueOf(day.plusDays(1).atStartOfDay());

        String sql = "SELECT " + COLUMNS + " FROM SCREENING_SCHEDULES"
                   + " WHERE START_DATETIME >= ? AND START_DATETIME < ?"
                   + " ORDER BY START_DATETIME, SCREEN_NO";
        List<Schedule> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setTimestamp(1, from);
            st.setTimestamp(2, to);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(toBean(rs));
                }
            }
        }
        return list;
    }

    /**
     * 同じスクリーンで、指定した時間帯と重なる上映がすでにあるか(新規登録のときに使う)。
     * 前の上映の終了時刻ちょうどに次が始まるのは「重ならない」扱い。
     */
    public boolean isOverlapped(int screenNo, Date start, Date end) throws Exception {
        return isOverlapped(screenNo, start, end, 0);   // スケジュールIDは1から始まるので、0は「除外なし」
    }

    /**
     * 同じスクリーンで、指定した時間帯と重なる上映がすでにあるか(変更のときに使う)。
     * excludeScheduleId のスケジュール(変更中の自分自身)は、重なりの判定から除く。
     */
    public boolean isOverlapped(int screenNo, Date start, Date end, int excludeScheduleId) throws Exception {
        String sql = "SELECT COUNT(*) FROM SCREENING_SCHEDULES"
                   + " WHERE SCREEN_NO = ? AND START_DATETIME < ? AND END_DATETIME > ?"
                   + " AND SCHEDULE_ID <> ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, screenNo);
            st.setTimestamp(2, new Timestamp(end.getTime()));
            st.setTimestamp(3, new Timestamp(start.getTime()));
            st.setInt(4, excludeScheduleId);
            try (ResultSet rs = st.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** 新規登録。自動採番されたIDは、渡された schedule にセットされる */
    public boolean save(Schedule schedule) throws Exception {
        String sql = "INSERT INTO SCREENING_SCHEDULES"
                   + " (MOVIE_ID, SCREEN_NO, START_DATETIME, END_DATETIME, SCREENING_FORMAT)"
                   + " VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql, new String[] {"SCHEDULE_ID"})) {
            st.setInt(1, schedule.getMovieId());
            st.setInt(2, schedule.getScreenNo());
            st.setTimestamp(3, new Timestamp(schedule.getStartDatetime().getTime()));
            st.setTimestamp(4, new Timestamp(schedule.getEndDatetime().getTime()));
            st.setString(5, schedule.getScreeningFormat());
            int count = st.executeUpdate();
            if (count > 0) {
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        schedule.setScheduleId(keys.getInt(1));
                    }
                }
            }
            return count > 0;
        }
    }

    /** 更新(スケジュールIDで特定) */
    public boolean update(Schedule schedule) throws Exception {
        String sql = "UPDATE SCREENING_SCHEDULES"
                   + " SET MOVIE_ID = ?, SCREEN_NO = ?, START_DATETIME = ?, END_DATETIME = ?, SCREENING_FORMAT = ?"
                   + " WHERE SCHEDULE_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, schedule.getMovieId());
            st.setInt(2, schedule.getScreenNo());
            st.setTimestamp(3, new Timestamp(schedule.getStartDatetime().getTime()));
            st.setTimestamp(4, new Timestamp(schedule.getEndDatetime().getTime()));
            st.setString(5, schedule.getScreeningFormat());
            st.setInt(6, schedule.getScheduleId());
            return st.executeUpdate() > 0;
        }
    }

    /**
     * 削除。予約が入っている(取り消し済みの予約も含む)スケジュールは、例外にせず false を返す。
     * (スケジュールが存在しない場合も false なので、区別したいときは先に get で確認する)
     */
    public boolean delete(int scheduleId) throws Exception {
        String sql = "DELETE FROM SCREENING_SCHEDULES WHERE SCHEDULE_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, scheduleId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_FK_VIOLATION.equals(e.getSQLState())) {
                return false;
            }
            throw e;
        }
    }
}