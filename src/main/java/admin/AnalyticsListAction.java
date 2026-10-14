package admin;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.Schedule;
import dao.ReservationSeatDao;
import dao.ScheduleDao;
import tool.Action;

/**
 * 分析一覧。指定した日(パラメータ date=yyyy-MM-dd。なければ今日)に上映が始まる
 * 全作品・全上映回の座席の予約状況を集計して、分析画面(analysis/index.jsp)に渡す。
 * 既存の ScheduleDao / ReservationSeatDao だけを使い、集計はこのクラスの中で行う。
 */
public class AnalyticsListAction extends Action {

    /** 座席の列(予約画面 ReservationCreateAction と同じ。A〜E列) */
    static final String ROWS = "ABCDE";
    /** 1列あたりの座席数(同じく6席) */
    static final int SEATS_PER_ROW = 6;

    /** エリア名と、そのエリアに含まれる列(前方 → 中央 → 後方の順) */
    static final String[] AREA_NAMES = {"前方", "中央", "後方"};
    static final String[] AREA_ROWS = {"AB", "C", "DE"};

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 分析する日(書き方がおかしいときは今日にする)
        LocalDate day = LocalDate.now();
        String param = request.getParameter("date");
        if (param != null && !param.isBlank()) {
            try {
                day = LocalDate.parse(param.trim());
            } catch (DateTimeException e) {
                // 今日のまま
            }
        }

        // その日の上映回と、それぞれの予約済み座席番号を集める
        List<Schedule> schedules = new ScheduleDao().getByDate(java.sql.Date.valueOf(day));
        ReservationSeatDao seatDao = new ReservationSeatDao();
        List<String> reservedSeatNos = new ArrayList<>();
        for (Schedule s : schedules) {
            reservedSeatNos.addAll(seatDao.getReservedSeatNos(s.getScheduleId()));
        }

        // 集計して、分析画面が読む名前でセットする
        Map<String, Object> result = aggregate(day, schedules.size(), reservedSeatNos);
        for (Map.Entry<String, Object> e : result.entrySet()) {
            request.setAttribute(e.getKey(), e.getValue());
        }

        request.getRequestDispatcher("/admin/analysis/index.jsp").forward(request, response);
    }

    /**
     * 集計する(DBは使わない)。
     * @param reservedSeatNos その日の全上映回の、予約済み座席番号(A1 など)
     */
    static Map<String, Object> aggregate(LocalDate day, int scheduleCount, List<String> reservedSeatNos) {

        // 列ごとの予約済み座席数(座席番号の先頭1文字が列)
        Map<String, Integer> reservedByRow = new LinkedHashMap<>();
        for (String seatNo : reservedSeatNos) {
            if (seatNo == null || seatNo.isEmpty()) {
                continue;
            }
            String row = seatNo.substring(0, 1).toUpperCase();
            if (ROWS.contains(row)) {
                reservedByRow.merge(row, 1, Integer::sum);
            }
        }

        // 列別
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

        // エリア別(列別の合計)
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

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("analysisDate", day);
        result.put("scheduleCount", scheduleCount);
        result.put("totalSeats", totalSeats);
        result.put("reservedSeats", reservedSeats);
        result.put("areaStats", areaStats);
        result.put("rowStats", rowStats);
        result.put("topArea", topOf(areaStats));
        result.put("topRow", topOf(rowStats));
        return result;
    }

    /** 予約率がいちばん高いもの(同率なら先に並んでいる方)。総座席数が0のものは対象外 */
    private static Map<String, Object> topOf(List<Map<String, Object>> list) {
        Map<String, Object> top = null;
        double topRate = -1;
        for (Map<String, Object> m : list) {
            int total = (Integer) m.get("total");
            if (total == 0) {
                continue;
            }
            double rate = (Integer) m.get("reserved") * 100.0 / total;
            if (rate > topRate) {
                top = m;
                topRate = rate;
            }
        }
        return top;
    }

    private static String areaNameOf(String row) {
        for (int i = 0; i < AREA_ROWS.length; i++) {
            if (AREA_ROWS[i].contains(row)) {
                return AREA_NAMES[i];
            }
        }
        return "";
    }

    /** "AB" → "A・B列" */
    private static String rowsLabelOf(String rows) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.length(); i++) {
            if (i > 0) {
                sb.append('・');
            }
            sb.append(rows.charAt(i));
        }
        return sb.append('列').toString();
    }
}