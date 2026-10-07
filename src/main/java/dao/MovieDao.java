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

    /**
     * 条件で絞り込み。タイトル・ジャンル・監督・主要キャストのどれかに、
     * condition を含む映画を返す(部分一致)。condition が空なら全件を返す。
     */
    public List<Movie> filter(String condition) throws Exception {
        if (condition == null || condition.trim().isEmpty()) {
            return getAll();
        }
        // LIKE の特別な文字(\ % _)を、ただの文字として扱うようにする
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

    /** 映画IDで1件取得(なければ null) */
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

    /** 新規登録。自動採番された映画IDは、渡された movie にセットされる */
    public boolean save(Movie movie) throws Exception {
        String sql = "INSERT INTO MOVIES (TITLE, DURATION, RELEASE_START_DATE, RELEASE_END_DATE,"
                + " GENRE, AGE_LIMIT, DESCRIPTION, DIRECTOR, \"CAST\", POSTER_URL)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql, new String[] { "MOVIE_ID" })) {
            st.setString(1, movie.getTitle());
            st.setInt(2, movie.getDuration());
            st.setDate(3, new Date(movie.getReleaseStartDate().getTime()));
            st.setDate(4, new Date(movie.getReleaseEndDate().getTime()));
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

    /** 更新(映画IDで特定) */
    public boolean update(Movie movie) throws Exception {
        String sql = "UPDATE MOVIES SET TITLE = ?, DURATION = ?, RELEASE_START_DATE = ?, RELEASE_END_DATE = ?,"
                + " GENRE = ?, AGE_LIMIT = ?, DESCRIPTION = ?, DIRECTOR = ?, \"CAST\" = ?, POSTER_URL = ?"
                + " WHERE MOVIE_ID = ?";
        try (Connection con = getConnection();
                PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, movie.getTitle());
            st.setInt(2, movie.getDuration());
            st.setDate(3, new Date(movie.getReleaseStartDate().getTime()));
            st.setDate(4, new Date(movie.getReleaseEndDate().getTime()));
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

    /**
     * 削除(映画IDで特定)。
     * 上映スケジュールがこの映画を使っている場合は、例外にせず false を返す。
     * (映画が存在しない場合も false なので、区別したいときは先に get で確認する)
     */
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
}