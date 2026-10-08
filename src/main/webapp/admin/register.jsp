<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>管理者登録 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / 管理者登録
    </p>
    <div class="page-title"><div><h1>管理者登録</h1></div></div>

    <section class="panel narrow">
      <h2>管理者情報を入力</h2>

      <%-- 登録に失敗したとき(IDが使用済みなど)、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <form action="${pageContext.request.contextPath}/admin/create/execute" method="post">
        <label for="adminId">管理者ID</label>
        <input id="adminId" name="adminId" type="text" inputmode="numeric"
               pattern="[0-9]+" maxlength="9" title="数字で入力してください"
               value="<c:out value='${param.adminId}' />"
               required autocomplete="username">

        <label for="password">パスワード(8文字以上)</label>
        <input id="password" name="password" type="password" required minlength="8"
               autocomplete="new-password">

        <div class="actions">
          <button class="button" type="submit">登録</button>
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/index.jsp">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>