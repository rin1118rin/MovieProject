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
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / 上映スケジュール変更
    </p>
    <div class="page-title"><div><h1>上映スケジュール変更</h1></div></div>

    <%--
      Actionが request に入れておく値:
        schedule : 変更する上映スケジュール1件(scheduleId / movieId / startDateTime(LocalDateTime) / screenNo / screeningFormat)
        movies   : 選べる上映映画のリスト(movieId と title を持つ)
    --%>
    <section class="panel narrow">
      <h2>怪盗グルー<fmt:formatNumber value="${schedule.scheduleId}" pattern="000" /> / 上映情報を変更</h2>

      <%-- 入力に問題があったとき(同じスクリーンの時間が重なるなど)、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <form action="${pageContext.request.contextPath}/admin/schedules/update/execute" method="post">
        <%-- どの上映スケジュールを変更するか(画面には出ない) --%>
        <input type="hidden" name="scheduleId" value="<c:out value='${schedule.scheduleId}' />">

        <label for="movie">上映作品</label>
        <select id="movie" name="movieId" required>
          <c:forEach var="movie" items="${movies}">
            <option value="${movie.movieId}"${schedule.movieId == movie.movieId ? ' selected' : ''}><c:out value="${movie.title}" /></option>
          </c:forEach>
        </select>

        <label for="date">上映日</label>
        <input id="date" name="screeningDate" type="date" required
               value="<c:out value='${schedule.startDateTime.toLocalDate()}' />">

        <label for="start">上映開始時刻</label>
        <input id="start" name="startTime" type="time" required
               value="<fmt:formatNumber value='${schedule.startDateTime.hour}' pattern='00' />:<fmt:formatNumber value='${schedule.startDateTime.minute}' pattern='00' />">

        <label for="screen-no">スクリーン番号</label>
        <input id="screen-no" name="screenNo" type="number" required min="1"
               value="<c:out value='${schedule.screenNo}' />">

        <label for="format">上映形式</label>
        <select id="format" name="screeningFormat">
          <option${schedule.screeningFormat == '2D' ? ' selected' : ''}>2D</option>
          <option${schedule.screeningFormat == '3D' ? ' selected' : ''}>3D</option>
          <option${schedule.screeningFormat == 'IMAX' ? ' selected' : ''}>IMAX</option>
        </select>

        <div class="actions">
          <button class="button" type="submit">変更</button>
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/schedule/list.jsp">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>