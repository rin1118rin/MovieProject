<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>上映スケジュール変更 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/adminMenu.action">管理者メインメニュー</a> / 上映スケジュール変更
    </p>
    <div class="page-title"><div><h1>上映スケジュール変更</h1></div></div>

    <section class="panel narrow">
      <h2>ID: <fmt:formatNumber value="${schedule.scheduleId}" pattern="000" /> / 上映情報を変更</h2>

      <%-- 入力に問題があったとき(同じスクリーンの時間が重なるなど)、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <form action="${pageContext.request.contextPath}/admin/scheduleUpdateExecute.action" method="post">
        <%-- どの上映スケジュールを変更するか(画面には出ない) --%>
        <input type="hidden" name="scheduleId" value="<c:out value='${schedule.scheduleId}' />">

        <label for="movie">上映作品</label>
        <select id="movie" name="movieId" required>
          <c:forEach var="movie" items="${movieList}">
            <option value="${movie.movieId}"${schedule.movieId == movie.movieId ? ' selected' : ''}><c:out value="${movie.title}" /></option>
          </c:forEach>
        </select>

        <%-- Timestamp を LocalDateTime に変換してから日付・時刻を取り出す --%>
        <c:set var="dt" value="${schedule.startDatetime.toLocalDateTime()}" />

        <label for="date">上映日</label>
        <input id="date" name="screeningDate" type="date" required
               value="${dt.toLocalDate()}">

        <label for="start">上映開始時刻</label>
        <input id="start" name="startTime" type="time" required
               value="<fmt:formatNumber value='${dt.hour}' pattern='00' />:<fmt:formatNumber value='${dt.minute}' pattern='00' />">

        <label for="screen-no">スクリーン番号</label>
        <input id="screen-no" name="screenNo" type="number" required min="1"
               value="<c:out value='${schedule.screenNo}' />">

        <label for="format">上映形式</label>
        <select id="format" name="screeningFormat" required>
          <option value="2D"${schedule.screeningFormat == '2D' ? ' selected' : ''}>2D</option>
          <option value="3D"${schedule.screeningFormat == '3D' ? ' selected' : ''}>3D</option>
          <option value="IMAX"${schedule.screeningFormat == 'IMAX' ? ' selected' : ''}>IMAX</option>
          <option value="4DX"${schedule.screeningFormat == '4DX' ? ' selected' : ''}>4DX</option>
        </select>

        <div class="actions">
          <button class="button" type="submit">変更</button>
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/scheduleList.action">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>