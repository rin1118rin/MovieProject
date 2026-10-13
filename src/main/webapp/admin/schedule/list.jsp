<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>上映スケジュール一覧 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/adminMenu.action">管理者メインメニュー</a> / 上映スケジュール一覧
    </p>
    <div class="page-title"><div><h1>上映スケジュール一覧</h1></div></div>

    <c:set var="today" value="${LocalDate.now()}" />

    <fieldset class="schedule-picker">
      <legend>上映日を選択</legend>

      <%-- 日付ボタン(今日が最初に選ばれた状態) --%>
      <c:forEach var="i" begin="0" end="6">
        <c:set var="date" value="${today.plusDays(i)}" />
        <c:set var="wd" value="${date.dayOfWeek.value}" />
        <input class="date-choice" type="radio" name="schedule-day" id="day-${i}"${i == 0 ? ' checked' : ''}>
        <label class="date-label" for="day-${i}">
          <strong>${date.monthValue}/${date.dayOfMonth}</strong>
          <span>(${fn:substring('月火水木金土日', wd - 1, wd)})</span>
        </label>
      </c:forEach>

      <%-- 日ごとの一覧 --%>
      <div class="date-panels">
        <c:forEach var="i" begin="0" end="6">
          <c:set var="date" value="${today.plusDays(i)}" />
          <c:set var="wd" value="${date.dayOfWeek.value}" />
          <div class="date-panel date-panel-${i}">
            <h2 class="selected-date">${date.year}年${date.monthValue}月${date.dayOfMonth}日(${fn:substring('月火水木金土日', wd - 1, wd)})</h2>

            <section class="panel">
              <div class="section-heading">
                <h2>上映スケジュール</h2>
                <a class="button" href="${pageContext.request.contextPath}/admin/scheduleCreate.action">＋ 上映スケジュール登録</a>
              </div>

              <div class="table-scroll">
                <table>
                  <thead>
                    <tr>
                      <th scope="col">上映ID</th>
                      <th scope="col">作品ID / フォーマット</th>
                      <th scope="col">開始時刻</th>
                      <th scope="col">スクリーン</th>
                      <th scope="col">操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <%-- この日の上映スケジュールだけを取り出して表示する --%>
                    <c:set var="found" value="false" />
                    <c:forEach var="schedule" items="${schedules}">
                      <%-- Timestamp を LocalDateTime に変換してから日付を比較する --%>
                      <c:set var="t" value="${schedule.startDatetime.toLocalDateTime()}" />
                      <c:set var="scheduleDate" value="${t.toLocalDate()}" />
                      
                      <c:if test="${scheduleDate == date}">
                        <c:set var="found" value="true" />
                        <tr>
                          <td>S<fmt:formatNumber value="${schedule.scheduleId}" pattern="000" /></td>
                          <td>映画ID: ${schedule.movieId} (${schedule.screeningFormat})</td>
                          <td><fmt:formatNumber value="${t.hour}" pattern="00" />:<fmt:formatNumber value="${t.minute}" pattern="00" /></td>
                          <td>Screen ${schedule.screenNo}</td>
                          <td>
                            <a href="${pageContext.request.contextPath}/admin/scheduleUpdate.action?id=${schedule.scheduleId}">編集</a>
                            /
                            <a href="${pageContext.request.contextPath}/admin/scheduleReservation.action?scheduleId=${schedule.scheduleId}">予約管理</a>
                          </td>
                        </tr>
                      </c:if>
                    </c:forEach>

                    <c:if test="${not found}">
                      <tr>
                        <td colspan="5">この日の上映スケジュールはありません。</td>
                      </tr>
                    </c:if>
                  </tbody>
                </table>
              </div>
            </section>
          </div>
        </c:forEach>
      </div>
    </fieldset>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>