<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>お問い合わせ回答確認・完了 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / お問い合わせ回答確認・完了
    </p>
    <div class="page-title"><div><h1>お問い合わせ回答確認・完了</h1></div></div>

    <%-- Actionが "inquiryId" に、回答したお問い合わせの受付番号(数字)を入れておく --%>
    <section class="panel narrow completion" id="reply-complete">
      <span class="checkmark" aria-hidden="true">✓</span>
      <h2>回答を送信しました</h2>
      <p>受付番号：C<fmt:formatNumber value="${inquiryId}" pattern="000" /></p>
      <a class="button" href="${pageContext.request.contextPath}/admin/contact/list.jsp">戻る</a>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>