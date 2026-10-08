<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>ログアウト | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / ログアウト
    </p>
    <div class="page-title"><div><h1>ログアウト</h1></div></div>

    <section class="panel narrow completion">
      <span class="checkmark" aria-hidden="true">✓</span>
      <h2>ログアウトしました</h2>
      <a class="button" href="${pageContext.request.contextPath}/admin/login.jsp">管理者ログインへ</a>
      <a class="button secondary" href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニューへ</a>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>