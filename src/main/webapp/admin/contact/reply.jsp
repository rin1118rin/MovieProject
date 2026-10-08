<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>お問い合わせ回答 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / お問い合わせ回答
    </p>
    <div class="page-title"><div><h1>お問い合わせ回答</h1></div></div>

    <%--
      Actionが "inquiry" に、回答するお問い合わせ1件を入れておく。
      持っている値: inquiryId / name / email / subject / body
    --%>
    <section class="panel narrow">
      <h2>お問い合わせ内容</h2>

      <dl class="details">
        <dt>受付番号</dt>
        <dd>C<fmt:formatNumber value="${inquiry.inquiryId}" pattern="000" /></dd>
        <dt>お名前</dt>
        <dd><c:out value="${inquiry.name}" /></dd>
        <dt>メール</dt>
        <dd><c:out value="${inquiry.email}" /></dd>
        <dt>件名</dt>
        <dd><c:out value="${inquiry.subject}" /></dd>
        <dt>お問い合わせ</dt>
        <dd class="multiline"><c:out value="${inquiry.body}" /></dd>
      </dl>

      <%-- 送信に失敗したとき、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <form action="${pageContext.request.contextPath}/admin/contacts/reply/execute" method="post">
        <%-- どのお問い合わせへの回答か(画面には出ない) --%>
        <input type="hidden" name="inquiryId" value="<c:out value='${inquiry.inquiryId}' />">

        <label for="reply">回答内容</label>
        <textarea id="reply" name="replyBody" rows="7" required
                  placeholder="回答を入力してください。"><c:out value="${param.replyBody}" /></textarea>

        <div class="actions">
          <button class="button" type="submit">送信</button>
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/contact/list.jsp">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>