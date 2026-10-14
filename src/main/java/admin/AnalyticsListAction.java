package admin;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Movie;
import bean.Schedule;
import dao.MovieDao;
import dao.ReservationSeatDao;
import dao.ScheduleDao;
import tool.Action;

// 分析一覧 日別、映画別(全期間)、映画別（指定日のみ）の３つの分析モードを切り替えて集計可能

public class AnalyticsListAction extends Action {

    // 座席の列定義（A〜E列）と1列あたりの座席数
    static final String ROWS = "ABCDE";
    static final int SEATS_PER_ROW = 6;

    // エリアの分類名と、それに属する列の定義
    static final String[] AREA_NAMES = {"前方", "中央", "後方"};
    static final String[] AREA_ROWS = {"AB", "C", "DE"};

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 画面のセレクトボックス（プルダウン）用に全映画のリストを取得してリクエストにセット
        MovieDao movieDao = new MovieDao();
        List<Movie> movieList = movieDao.getAll();
        request.setAttribute("movieList", movieList);

        // 画面から送信された分析モード（modeパラメータ）を取得
        String mode = request.getParameter("mode");

        if ("movie".equals(mode)) {
            
            // 映画別分析（全期間の累計）
            
            String movieIdStr = request.getParameter("movieId");
            int movieId = 0;
            if (movieIdStr != null && !movieIdStr.isBlank()) {
                try {
                    movieId = Integer.parseInt(movieIdStr.trim());
                } catch (NumberFormatException e) {}
            }

            // 指定された映画の全期間の予約データを集計
            Map<String, Object> result = aggregateByMovie(movieId);
            for (Map.Entry<String, Object> e : result.entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());
            }

        } else if ("date_movie".equals(mode)) {
            
            // 映画別（指定日のみ）分析
            
            // 対象日の取得（未指定の場合は現在の日付を使用）
            LocalDate day = LocalDate.now();
            String dateParam = request.getParameter("date");
            if (dateParam != null && !dateParam.isBlank()) {
                try {
                    day = LocalDate.parse(dateParam.trim());
                } catch (DateTimeException e) {}
            }

            // 対象映画IDの取得
            String movieIdStr = request.getParameter("movieId");
            int movieId = 0;
            if (movieIdStr != null && !movieIdStr.isBlank()) {
                try {
                    movieId = Integer.parseInt(movieIdStr.trim());
                } catch (NumberFormatException e) {}
            }

            // 指定された日付かつ指定された映画のスケジュール・予約データを集計
            Map<String, Object> result = aggregateByDateAndMovie(day, movieId);
            for (Map.Entry<String, Object> e : result.entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());
            }

        } else {
            
            // 日別分析（その日の全映画）

            // 対象日の取得（未指定の場合は現在の日付を使用）
            LocalDate day = LocalDate.now();
            String param = request.getParameter("date");
            if (param != null && !param.isBlank()) {
                try {
                    day = LocalDate.parse(param.trim());
                } catch (DateTimeException e) {}
            }

            // 指定日に上映されるスケジュール一覧を取得し、それぞれの予約済み座席番号を収集
            List<Schedule> schedules = new ScheduleDao().getByDate(java.sql.Date.valueOf(day));
            ReservationSeatDao seatDao = new ReservationSeatDao();
            List<String> reservedSeatNos = new ArrayList<>();
            for (Schedule s : schedules) {
                reservedSeatNos.addAll(seatDao.getReservedSeatNos(s.getScheduleId()));
            }

            // 収集したデータを基に日別集計を実行
            Map<String, Object> result = aggregate(day, schedules.size(), reservedSeatNos);
            for (Map.Entry<String, Object> e : result.entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());
            }
        }

        // 集計結果を格納して分析画面（index.jsp）へフォワード
        request.getRequestDispatcher("/admin/analysis/index.jsp").forward(request, response);
    }

    // 日別集計ロジック 指定された日付の全上映回における座席・エリア・列ごとの予約状況を集計
    static Map<String, Object> aggregate(LocalDate day, int scheduleCount, List<String> reservedSeatNos) {
        // 列ごとの予約数をカウントするためのマップ
        Map<String, Integer> reservedByRow = new LinkedHashMap<>();
        
        // 座席ごとの予約回数を集計するマップ (例: "A1" -> 2回)
        Map<String, Integer> seatReservedCountMap = new LinkedHashMap<>();
        
        for (String seatNo : reservedSeatNos) {
            if (seatNo == null || seatNo.isEmpty()) continue;
            String row = seatNo.substring(0, 1).toUpperCase();
            if (ROWS.contains(row)) {
                reservedByRow.merge(row, 1, Integer::sum);
            }
            seatReservedCountMap.merge(seatNo, 1, Integer::sum);
        }

        // 列ごとの統計データ（総座席数・予約済み数）を算出
        List<Map<String, Object>> rowStats = new ArrayList<>();
        for (char c : ROWS.toCharArray()) {
            String name = String.valueOf(c);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", name);
            row.put("areaName", areaNameOf(name));
            row.put("total", scheduleCount * SEATS_PER_ROW);
            row.put("reserved", reservedByRow.getOrDefault(name, 0));
            rowStats.add(row);
        }

        // エリアごと（前方・中央・後方）の統計データを算出
        List<Map<String, Object>> areaStats = new ArrayList<>();
        for (int i = 0; i < AREA_NAMES.length; i++) {
            int total = 0;
            int reserved = 0;
            for (Map<String, Object> row : rowStats) {
                if (AREA_ROWS[i].contains((String) row.get("name"))) {
                    total += (Integer) row.get("total");
                    reserved += (Integer) row.get("reserved");
                }
            }
            Map<String, Object> area = new LinkedHashMap<>();
            area.put("name", AREA_NAMES[i]);
            area.put("rowsLabel", rowsLabelOf(AREA_ROWS[i]));
            area.put("total", total);
            area.put("reserved", reserved);
            areaStats.add(area);
        }

        // 全体の総座席数と予約済み座席数を合算
        int totalSeats = 0;
        int reservedSeats = 0;
        for (Map<String, Object> row : rowStats) {
            totalSeats += (Integer) row.get("total");
            reservedSeats += (Integer) row.get("reserved");
        }

        // 最終的な結果マップにまとめて返却
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("analysisMode", "daily");
        result.put("analysisDate", day);
        result.put("scheduleCount", scheduleCount);
        result.put("totalSeats", totalSeats);
        result.put("reservedSeats", reservedSeats);
        result.put("areaStats", areaStats);
        result.put("rowStats", rowStats);
        result.put("topArea", topOf(areaStats));
        result.put("topRow", topOf(rowStats));
        result.put("reservedSeatNos", reservedSeatNos);
        result.put("seatReservedCountMap", seatReservedCountMap); // 座席ごとの予約回数マップを追加
        return result;
    }

    // 映画別集計ロジック（全期間） 指定された映画IDに紐づく全スケジュールを抽出し、全期間の累計予約状況を集計
    static Map<String, Object> aggregateByMovie(int movieId) throws Exception {
        MovieDao movieDao = new MovieDao();
        Movie movie = movieDao.get(movieId);

        ScheduleDao scheduleDao = new ScheduleDao();
        List<Schedule> allSchedules = scheduleDao.getAll();
        
        // 該当する映画のスケジュールのみをフィルタリング
        List<Schedule> targetSchedules = new ArrayList<>();
        for (Schedule s : allSchedules) {
            if (s.getMovieId() == movieId) {
                targetSchedules.add(s);
            }
        }

        // 該当スケジュールの予約済み座席番号を収集
        ReservationSeatDao seatDao = new ReservationSeatDao();
        List<String> reservedSeatNos = new ArrayList<>();
        for (Schedule s : targetSchedules) {
            reservedSeatNos.addAll(seatDao.getReservedSeatNos(s.getScheduleId()));
        }

        int scheduleCount = targetSchedules.size();
        Map<String, Integer> reservedByRow = new LinkedHashMap<>();
        Map<String, Integer> seatReservedCountMap = new LinkedHashMap<>();
        
        for (String seatNo : reservedSeatNos) {
            if (seatNo == null || seatNo.isEmpty()) continue;
            String row = seatNo.substring(0, 1).toUpperCase();
            if (ROWS.contains(row)) {
                reservedByRow.merge(row, 1, Integer::sum);
            }
            seatReservedCountMap.merge(seatNo, 1, Integer::sum);
        }

        // 列ごとの統計データを算出
        List<Map<String, Object>> rowStats = new ArrayList<>();
        for (char c : ROWS.toCharArray()) {
            String name = String.valueOf(c);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", name);
            row.put("areaName", areaNameOf(name));
            row.put("total", scheduleCount * SEATS_PER_ROW);
            row.put("reserved", reservedByRow.getOrDefault(name, 0));
            rowStats.add(row);
        }

        // エリアごとの統計データを算出
        List<Map<String, Object>> areaStats = new ArrayList<>();
        for (int i = 0; i < AREA_NAMES.length; i++) {
            int total = 0;
            int reserved = 0;
            for (Map<String, Object> row : rowStats) {
                if (AREA_ROWS[i].contains((String) row.get("name"))) {
                    total += (Integer) row.get("total");
                    reserved += (Integer) row.get("reserved");
                }
            }
            Map<String, Object> area = new LinkedHashMap<>();
            area.put("name", AREA_NAMES[i]);
            area.put("rowsLabel", rowsLabelOf(AREA_ROWS[i]));
            area.put("total", total);
            area.put("reserved", reserved);
            areaStats.add(area);
        }

        int totalSeats = 0;
        int reservedSeats = 0;
        for (Map<String, Object> row : rowStats) {
            totalSeats += (Integer) row.get("total");
            reservedSeats += (Integer) row.get("reserved");
        }

        // 結果マップに格納して返却
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("analysisMode", "movie");
        result.put("selectedMovie", movie);
        result.put("scheduleCount", scheduleCount);
        result.put("totalSeats", totalSeats);
        result.put("reservedSeats", reservedSeats);
        result.put("areaStats", areaStats);
        result.put("rowStats", rowStats);
        result.put("topArea", topOf(areaStats));
        result.put("topRow", topOf(rowStats));
        result.put("reservedSeatNos", reservedSeatNos);
        result.put("seatReservedCountMap", seatReservedCountMap); // 座席ごとの予約回数マップを追加
        return result;
    }

    // 映画別(指定日のみ)集計ロジック 指定された日付かつ指定された映画IDに一致するスケジュールのみを抽出し、その日の予約状況を集計
    static Map<String, Object> aggregateByDateAndMovie(LocalDate day, int movieId) throws Exception {
        MovieDao movieDao = new MovieDao();
        Movie movie = movieDao.get(movieId);

        ScheduleDao scheduleDao = new ScheduleDao();
        List<Schedule> daySchedules = scheduleDao.getByDate(java.sql.Date.valueOf(day));

        // 指定日かつ指定映画のスケジュールをフィルタリング
        List<Schedule> targetSchedules = new ArrayList<>();
        for (Schedule s : daySchedules) {
            if (s.getMovieId() == movieId) {
                targetSchedules.add(s);
            }
        }

        // 該当スケジュールの予約済み座席番号を収集
        ReservationSeatDao seatDao = new ReservationSeatDao();
        List<String> reservedSeatNos = new ArrayList<>();
        for (Schedule s : targetSchedules) {
            reservedSeatNos.addAll(seatDao.getReservedSeatNos(s.getScheduleId()));
        }

        int scheduleCount = targetSchedules.size();
        Map<String, Integer> reservedByRow = new LinkedHashMap<>();
        Map<String, Integer> seatReservedCountMap = new LinkedHashMap<>();
        
        for (String seatNo : reservedSeatNos) {
            if (seatNo == null || seatNo.isEmpty()) continue;
            String row = seatNo.substring(0, 1).toUpperCase();
            if (ROWS.contains(row)) {
                reservedByRow.merge(row, 1, Integer::sum);
            }
            seatReservedCountMap.merge(seatNo, 1, Integer::sum);
        }

        // 列ごとの統計データを算出
        List<Map<String, Object>> rowStats = new ArrayList<>();
        for (char c : ROWS.toCharArray()) {
            String name = String.valueOf(c);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", name);
            row.put("areaName", areaNameOf(name));
            row.put("total", scheduleCount * SEATS_PER_ROW);
            row.put("reserved", reservedByRow.getOrDefault(name, 0));
            rowStats.add(row);
        }

        // エリアごとの統計データを算出
        List<Map<String, Object>> areaStats = new ArrayList<>();
        for (int i = 0; i < AREA_NAMES.length; i++) {
            int total = 0;
            int reserved = 0;
            for (Map<String, Object> row : rowStats) {
                if (AREA_ROWS[i].contains((String) row.get("name"))) {
                    total += (Integer) row.get("total");
                    reserved += (Integer) row.get("reserved");
                }
            }
            Map<String, Object> area = new LinkedHashMap<>();
            area.put("name", AREA_NAMES[i]);
            area.put("rowsLabel", rowsLabelOf(AREA_ROWS[i]));
            area.put("total", total);
            area.put("reserved", reserved);
            areaStats.add(area);
        }

        int totalSeats = 0;
        int reservedSeats = 0;
        for (Map<String, Object> row : rowStats) {
            totalSeats += (Integer) row.get("total");
            reservedSeats += (Integer) row.get("reserved");
        }

        // 結果マップに格納して返却
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("analysisMode", "date_movie");
        result.put("analysisDate", day);
        result.put("selectedMovie", movie);
        result.put("scheduleCount", scheduleCount);
        result.put("totalSeats", totalSeats);
        result.put("reservedSeats", reservedSeats);
        result.put("areaStats", areaStats);
        result.put("rowStats", rowStats);
        result.put("topArea", topOf(areaStats));
        result.put("topRow", topOf(rowStats));
        result.put("reservedSeatNos", reservedSeatNos);
        result.put("seatReservedCountMap", seatReservedCountMap); // 座席ごとの予約回数マップを追加
        return result;
    }

    // 予約率が最も高い項目（エリアまたは列）を判定するメソッド
    private static Map<String, Object> topOf(List<Map<String, Object>> list) {
        Map<String, Object> top = null;
        double topRate = -1;
        for (Map<String, Object> m : list) {
            int total = (Integer) m.get("total");
            if (total == 0) continue;
            double rate = (Integer) m.get("reserved") * 100.0 / total;
            if (rate > topRate) {
                top = m;
                topRate = rate;
            }
        }
        return top;
    }

    // 指定された列文字から所属するエリア名（前方・中央・後方）を逆引きするメソッド
    private static String areaNameOf(String row) {
        for (int i = 0; i < AREA_ROWS.length; i++) {
            if (AREA_ROWS[i].contains(row)) return AREA_NAMES[i];
        }
        return "";
    }

    // エリアに属する列の文字列表記を生成するメソッド
    private static String rowsLabelOf(String rows) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.length(); i++) {
            if (i > 0) sb.append('・');
            sb.append(rows.charAt(i));
        }
        return sb.append('列').toString();
    }
}