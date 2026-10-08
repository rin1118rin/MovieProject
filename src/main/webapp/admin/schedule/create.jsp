<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>上映スケジュール登録 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / 上映スケジュール登録
    </p>
    <div class="page-title"><div><h1>上映スケジュール登録</h1></div></div>

    <%-- Actionが "movies" に、選べる上映映画のリスト(movieId と title を持つ)を入れておく --%>
    <section class="panel narrow">
      <h2>新しい上映回を登録</h2>

      <%-- 入力に問題があったとき(同じスクリーンの時間が重なるなど)、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <form action="${pageContext.request.contextPath}/admin/schedules/create/execute" method="post">
        <label for="movie">上映作品</label>
        <select id="movie" name="movieId" required>
          <c:forEach var="movie" items="${movies}">
            <option value="${movie.movieId}"${param.movieId == movie.movieId ? ' selected' : ''}><c:out value="${movie.title}" /></option>
          </c:forEach>
        </select>
        <c:if test="${empty movies}">
          <p class="muted">上映映画が登録されていません。先に上映映画を登録してください。</p>
        </c:if>

        <label for="date">上映日</label>
        <input id="date" name="screeningDate" type="date" required
               value="<c:out value='${param.screeningDate}' />">

        <label for="start">上映開始時刻</label>
        <input id="start" name="startTime" type="time" required
               value="<c:out value='${param.startTime}' />">

        <label for="screen-no">スクリーン番号</label>
        <input id="screen-no" name="screenNo" type="number" required min="1"
               value="<c:out value='${param.screenNo}' />">

        <label for="format">上映形式</label>
        <select id="format" name="screeningFormat">
          <option${param.screeningFormat == '2D' ? ' selected' : ''}>2D</option>
          <option${param.screeningFormat == '3D' ? ' selected' : ''}>3D</option>
          <option${param.screeningFormat == 'IMAX' ? ' selected' : ''}>IMAX</option>
          <option${param.screeningFormat == '4DX' ? ' selected' : ''}>4DX</option>
        </select>

        <div class="actions">
          <button class="button" type="submit">登録</button>
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/schedule/list.jsp">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>