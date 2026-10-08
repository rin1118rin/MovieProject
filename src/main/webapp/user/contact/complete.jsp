<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>お問い合わせ完了 | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<jsp:include page="/views/common/header.jsp" />

<main class="contact-complete-main">

    <div class="contact-complete-title">
        <h1>お問い合わせ完了</h1>
    </div>

    <section class="contact-complete-panel">

        <div class="contact-complete-check">✓</div>

        <h2>お問い合わせを受け付けました</h2>

        <p class="contact-complete-number">
            受付番号：
            <strong>
                <c:out value="${inquiry.inquiryId}" />
            </strong>
        </p>

        <p class="contact-complete-message">
            お問い合わせいただきありがとうございます。<br>
            内容を確認のうえ、担当者よりご連絡いたします。
        </p>

        <a href="${pageContext.request.contextPath}/"
           class="contact-complete-button">
            トップページに戻る
        </a>

    </section>

</main>

<jsp:include page="/views/common/footer.jsp" />

</body>
</html>