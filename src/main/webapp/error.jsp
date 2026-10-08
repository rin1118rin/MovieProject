
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<%
    Integer statusCode = (Integer) request.getAttribute(
        "jakarta.servlet.error.status_code"
    );

    boolean is404 = statusCode != null && statusCode == 404;

    String errorTitle = is404
        ? "ページが見つかりません"
        : "エラーが発生しました";

    String errorMessage = is404
        ? "お探しのページは存在しないか、移動した可能性があります。"
        : "処理中に問題が発生しました。しばらく時間をおいてから、再度お試しください。";
%>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>エラー | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<jsp:include page="/views/common/header.jsp" />

<main class="error-main">

    <section class="error-panel">

        <div class="error-symbol">
            !
        </div>

        <p class="error-code">
            ERROR <%= is404 ? "404" : "500" %>
        </p>

        <h1><%= errorTitle %></h1>

        <p class="error-message">
            <%= errorMessage %>
        </p>

        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/"
               class="error-home-button">
                トップページに戻る
            </a>
        </div>

        <p class="error-note">
            問題が解決しない場合は、お問い合わせください。
        </p>

    </section>

</main>

<jsp:include page="/views/common/footer.jsp" />

</body>
</html>
