package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Movie;

/**
 * 映画テーブル(MOVIES)を読み書きするクラス。
 */
public class MovieDao extends Dao {

    /** SQL標準で「外部キー制約の違反(参照している行がある)」を表すSQLState */
    private static final String SQLSTATE_FK_VIOLATION = "23503";

    // ※ CAST は H2 の予約語の可能性があるため、引用符で囲んでいる
    private static final String COLUMNS = "MOVIE_ID, TITLE, DURATION, RELEASE_START_DATE, RELEASE_END_DATE, GENRE, AGE_LIMIT,"
            + " DESCRIPTION, DIRECTOR, \"CAST\", POSTER_URL";

    private Movie toBean(ResultSet rs) throws Exception {
        Movie m = new Movie();
        m.setMovieId(rs.getInt("MOVIE_ID"));
        m.setTitle(rs.getString("TITLE"));
        m.setDuration(rs.getInt("DURATION"));
        m.setReleaseStartDate(rs.getDate("RELEASE_START_DATE"));
        m.setReleaseEndDate(rs.getDate("RELEASE_END_DATE"));
        m.setGenre(rs.getString("GENRE"));
        m.setAgeLimit(rs.getString("AGE_LIMIT"));
        m.setDescription(rs.getString("DESCRIPTION"));
        m.setDirector(rs.getString("DIRECTOR"));
        m.setCast(rs.getString("CAST"));
        m.setPosterUrl(rs.getString("POSTER_URL"));
        return m;
    }

    private List<Movie> toList(ResultSet rs) throws Exception {
        List<Movie> list = new ArrayList<>();
        while (rs.next()) {
            list.add(toBean(rs));
        }
        return list;
    }

    /** 全件取得(映画IDの順) */
    public List<Movie> getAll() throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM MOVIES ORDER BY MOVIE_ID";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql);
                ResultSet rs = st.executeQuery()) {
            return toList(rs);
        }
    }

    /** 条件で絞り込み */
    public List<Movie> filter(String condition) throws Exception {
        if (condition == null || condition.trim().isEmpty()) {
            return getAll();
        }
        String escaped = condition.trim()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        String pattern = "%" + escaped + "%";

        String sql = "SELECT " + COLUMNS + " FROM MOVIES WHERE"
                + " TITLE LIKE ? ESCAPE '\\' OR GENRE LIKE ? ESCAPE '\\'"
                + " OR DIRECTOR LIKE ? ESCAPE '\\' OR \"CAST\" LIKE ? ESCAPE '\\'"
                + " ORDER BY MOVIE_ID";

        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                st.setString(i, pattern);
            }
            try (ResultSet rs = st.executeQuery()) {
                return toList(rs);
            }
        }
    }

    /** 映画IDで1件取得 */
    public Movie get(int movieId) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM MOVIES WHERE MOVIE_ID = ?";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, movieId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? toBean(rs) : null;
            }
        }
    }

    /** 新規登録（NPE対策済み） */
    public boolean save(Movie movie) throws Exception {
        String sql = "INSERT INTO MOVIES (TITLE, DURATION, RELEASE_START_DATE, RELEASE_END_DATE,"
                + " GENRE, AGE_LIMIT, DESCRIPTION, DIRECTOR, \"CAST\", POSTER_URL)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql, new String[] { "MOVIE_ID" })) {

            st.setString(1, movie.getTitle());
            st.setInt(2, movie.getDuration());

            // ★ null を許容する安全な日付セット
            if (movie.getReleaseStartDate() != null) {
                st.setDate(3, new Date(movie.getReleaseStartDate().getTime()));
            } else {
                st.setNull(3, java.sql.Types.DATE);
            }

            if (movie.getReleaseEndDate() != null) {
                st.setDate(4, new Date(movie.getReleaseEndDate().getTime()));
            } else {
                st.setNull(4, java.sql.Types.DATE);
            }

            st.setString(5, movie.getGenre());
            st.setString(6, movie.getAgeLimit());
            st.setString(7, movie.getDescription());
            st.setString(8, movie.getDirector());
            st.setString(9, movie.getCast());
            st.setString(10, movie.getPosterUrl());

            int count = st.executeUpdate();

            if (count > 0) {
                try (ResultSet keys = st.getGeneratedKeys()) {
                    if (keys.next()) {
                        movie.setMovieId(keys.getInt(1));
                    }
                }
            }
            return count > 0;
        }
    }

    /** 更新（NPE対策済み） */
    public boolean update(Movie movie) throws Exception {
        String sql = "UPDATE MOVIES SET TITLE = ?, DURATION = ?, RELEASE_START_DATE = ?, RELEASE_END_DATE = ?,"
                + " GENRE = ?, AGE_LIMIT = ?, DESCRIPTION = ?, DIRECTOR = ?, \"CAST\" = ?, POSTER_URL = ?"
                + " WHERE MOVIE_ID = ?";

        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, movie.getTitle());
            st.setInt(2, movie.getDuration());

            // ★ null を許容する安全な日付セット
            if (movie.getReleaseStartDate() != null) {
                st.setDate(3, new Date(movie.getReleaseStartDate().getTime()));
            } else {
                st.setNull(3, java.sql.Types.DATE);
            }

            if (movie.getReleaseEndDate() != null) {
                st.setDate(4, new Date(movie.getReleaseEndDate().getTime()));
            } else {
                st.setNull(4, java.sql.Types.DATE);
            }

            st.setString(5, movie.getGenre());
            st.setString(6, movie.getAgeLimit());
            st.setString(7, movie.getDescription());
            st.setString(8, movie.getDirector());
            st.setString(9, movie.getCast());
            st.setString(10, movie.getPosterUrl());
            st.setInt(11, movie.getMovieId());

            return st.executeUpdate() > 0;
        }
    }

    /** 削除 */
    public boolean delete(Movie movie) throws Exception {
        String sql = "DELETE FROM MOVIES WHERE MOVIE_ID = ?";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, movie.getMovieId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            if (SQLSTATE_FK_VIOLATION.equals(e.getSQLState())) {
                return false;
            }
            throw e;
        }
    }

//公開中の作品を予約数の多い順に取得
public List<Movie> getRanking() throws Exception {
    String sql =
        "SELECT M.* "
        + "FROM MOVIES M "
        + "LEFT JOIN("
        + "    SELECT S.MOVIE_ID, COUNT(*) AS RESERVED_SEATS "
        + "    FROM RESERVATIONS R "
        + "    JOIN SCREENING_SCHEDULES S "
        + "      ON R.SCHEDULE_ID = S.SCHEDULE_ID "
        + "    JOIN RESERVATION_SEATS RS "
        + "      ON RS.RESERVATION_ID = R.RESERVATION_ID "
        + "     AND RS.SCHEDULE_ID = R.SCHEDULE_ID "
        + "    WHERE R.STATUS = ? "
        + "    GROUP BY S.MOVIE_ID"
        + ") P ON P.MOVIE_ID = M.MOVIE_ID "
        + "WHERE M.RELEASE_START_DATE <= CURRENT_DATE "
        + "  AND (M.RELEASE_END_DATE IS NULL "
        + "       OR M.RELEASE_END_DATE >= CURRENT_DATE) "
        + "ORDER BY COALESCE(P.RESERVED_SEATS, 0) DESC, M.MOVIE_ID "
        + "LIMIT 3";
    
    List<Movie> ranking = new ArrayList<>();

    try (Connection con = getConnection();
         PreparedStatement st = con.prepareStatement(sql)) {
        st.setString(1, "予約済み");

        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                ranking.add(toBean(rs));
            }
        }
    }
    return ranking;
}

}
