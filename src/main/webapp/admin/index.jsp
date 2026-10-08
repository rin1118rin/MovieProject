<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>管理者メインメニュー | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin">管理者メインメニュー</a> / 管理者メインメニュー
    </p>
    <div class="page-title"><div><h1>管理者メインメニュー</h1></div></div>

    <div class="quick-links">
      <a href="${pageContext.request.contextPath}/admin/movies/list.jsp"><strong>上映映画管理 →</strong></a>
      <a href="${pageContext.request.contextPath}/admin/schedule/list.jsp"><strong>上映スケジュール管理 →</strong></a>
      <a href="${pageContext.request.contextPath}/admin/analysis/index.jsp"><strong>分析一覧 →</strong></a>
      <a href="${pageContext.request.contextPath}/admin/contact/list.jsp"><strong>お問い合わせ管理 →</strong></a>
      <a href="${pageContext.request.contextPath}/admin/register.jsp"><strong>管理者登録 →</strong></a>
    </div>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>