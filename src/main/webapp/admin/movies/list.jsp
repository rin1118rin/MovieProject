<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>上映映画一覧 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / 上映映画一覧
    </p>
    <div class="page-title"><div><h1>上映映画一覧</h1></div></div>

    <section class="panel">
      <div class="section-heading">
        <h2>登録済みの上映映画</h2>
        <a class="button" href="${pageContext.request.contextPath}/admin/movies/create.jsp">＋ 上映映画登録</a>
      </div>

      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th scope="col">作品ID</th>
              <th scope="col">作品名</th>
              <th scope="col">上映時間</th>
              <th scope="col">状態</th>
              <th scope="col">操作</th>
            </tr>
          </thead>
          <tbody>
            <%-- 今日の日付(yyyy-MM-dd)。公開期間と比べて「状態」を決めるのに使う --%>
            <jsp:useBean id="now" class="java.util.Date" />
            <fmt:formatDate var="today" value="${now}" pattern="yyyy-MM-dd" timeZone="Asia/Tokyo" />

            <%-- Actionが "movies" にリストを入れておく(1行 = 1本の映画) --%>
            <c:forEach var="movie" items="${movies}">
              <tr>
                <td>M<fmt:formatNumber value="${movie.movieId}" pattern="000" /></td>
                <td><c:out value="${movie.title}" /></td>
                <td><c:out value="${movie.duration}" />分</td>
                <td>
                  <%-- 日付は "2026-10-08" の形の文字列として比べる(この形なら文字の順 = 日付の順) --%>
                  <c:choose>
                    <c:when test="${today < movie.releaseStartDate.toString()}">公開予定</c:when>
                    <c:when test="${today > movie.releaseEndDate.toString()}">上映終了</c:when>
                    <c:otherwise>上映中</c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <a href="${pageContext.request.contextPath}/admin/movies/edit.jsp?Id=${movie.movieId}">編集 →</a>
                </td>
              </tr>
            </c:forEach>

            <c:if test="${empty movies}">
              <tr>
                <td colspan="5">登録されている上映映画はありません。</td>
              </tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>