<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>上映スケジュール予約管理 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/adminMenu.action">管理者メインメニュー</a> / 上映スケジュール予約管理
    </p>
    <div class="page-title"><div><h1>上映スケジュール予約管理</h1></div></div>

    <%-- Timestamp を LocalDateTime に変換してから日付・曜日・時刻を取得 --%>
    <c:set var="start" value="${schedule.startDatetime.toLocalDateTime()}" />
    <c:set var="wd" value="${start.dayOfWeek.value}" />

    <section class="panel">
      <h2><c:out value="${schedule.movieTitle}" /> / S<fmt:formatNumber value="${schedule.scheduleId}" pattern="000" /></h2>
      <p>
        ${start.year}年${start.monthValue}月${start.dayOfMonth}日(
        <c:choose>
          <c:when test="${wd == 1}">月</c:when>
          <c:when test="${wd == 2}">火</c:when>
          <c:when test="${wd == 3}">水</c:when>
          <c:when test="${wd == 4}">木</c:when>
          <c:when test="${wd == 5}">金</c:when>
          <c:when test="${wd == 6}">土</c:when>
          <c:otherwise>日</c:otherwise>
        </c:choose>
        )
        <fmt:formatNumber value="${start.hour}" pattern="00" />:<fmt:formatNumber value="${start.minute}" pattern="00" />
      </p>

      <div class="admin-stats">
        <div><small>総座席数</small><strong>${schedule.totalSeats}席</strong></div>
        <div><small>予約済み</small><strong>${schedule.reservedSeats}席</strong></div>
        <div><small>空席</small><strong>${schedule.totalSeats - schedule.reservedSeats}席</strong></div>
      </div>

      <h2>予約一覧</h2>
      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th scope="col">予約番号</th>
              <th scope="col">予約者</th>
              <th scope="col">券種</th>
              <th scope="col">座席</th>
              <th scope="col">枚数</th>
              <th scope="col">金額</th>
              <th scope="col">状態</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${reservations}">
              <tr>
                <td>R<fmt:formatNumber value="${r.reservationId}" pattern="000" /></td>
                <td><c:out value="${r.customerName}" /></td>
                <td>
                  <c:forEach var="t" items="${r.tickets}" varStatus="st"><c:if test="${not st.first}">・</c:if><c:out value="${t.ticketType}" />×${t.quantity}</c:forEach>
                  <c:if test="${empty r.tickets}">—</c:if>
                </td>
                <td>
                  <c:forEach var="seat" items="${r.seats}" varStatus="st"><c:if test="${not st.first}">・</c:if><c:out value="${seat}" /></c:forEach>
                  <c:if test="${empty r.seats}">—</c:if>
                </td>
                <td>${fn:length(r.seats)}枚</td>
                <td><fmt:formatNumber value="${r.totalPrice}" pattern="#,##0" />円</td>
                <td><c:out value="${r.status}" /></td>
              </tr>
            </c:forEach>

            <c:if test="${empty reservations}">
              <tr>
                <td colspan="7">この上映の予約はありません。</td>
              </tr>
            </c:if>
          </tbody>
        </table>
      </div>

      <a class="button secondary" href="${pageContext.request.contextPath}/admin/scheduleList.action">戻る</a>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>