
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>予約取消完了 | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">

    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined"
          rel="stylesheet">
</head>

<body>

<jsp:include page="/views/common/header.jsp" />

<main class="cancel-complete-main">

    <div class="cancel-complete-title">
        <h1>予約取消完了</h1>
    </div>

    <section class="cancel-complete-panel">

        <div class="cancel-complete-icon">
            <span class="material-symbols-outlined">
                check
            </span>
        </div>

        <h2>予約の取消が完了しました</h2>

        <p class="cancel-complete-message">
            ご予約の取消手続きが正常に完了しました。<br>
            ご利用ありがとうございました。
        </p>

        <div class="cancel-complete-details">

            <div class="cancel-complete-row">
                <span>予約番号</span>
                <strong>
                    <c:out value="${reservation.reservationId}" />
                </strong>
            </div>

            <div class="cancel-complete-row">
                <span>作品名</span>
                <strong>
                    <c:out value="${reservation.movieTitle}" />
                </strong>
            </div>

            <div class="cancel-complete-row">
                <span>予約状況</span>
                <strong class="cancel-complete-status">
                    取消済み
                </strong>
            </div>

        </div>

        <p class="cancel-complete-note">
            ※ 取消済みの予約はご利用いただけません。
        </p>

        <a href="${pageContext.request.contextPath}/"
           class="cancel-complete-button">
            トップページに戻る
        </a>

    </section>

</main>

<jsp:include page="/views/common/footer.jsp" />

</body>
</html>
