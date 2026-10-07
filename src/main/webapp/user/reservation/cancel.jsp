<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>予約取消 | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">

</head>

<!-- 共通ヘッダー -->
<jsp:include page="/views/common/header.jsp" />

<main class="cancel-main">

    <section class="cancel-card">

        <div class="cancel-header">
            <!--タイトル-->
            <h1>予約取消</h1>

            <p>
                予約時に発行された予約番号と<br>
                登録したメールアドレスを入力してください。
            </p>

            <!-- エラーページ -->
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}" />
                </div>
            </c:if>

            <!-- 取消フォーム -->
            <form action="${pageContext.request.contextPath}/reservation/cancel" method="post" class="cancel-form">

                <!-- 予約番号 -->
                <div class="cancel-form-group">
                    <label for="reservationId">予約番号
                        <span class="required">必須</span>
                    </label>

                    <input
                        type="text"
                        id="reservationId"
                        name="reservaionId"
                        placeholder="例:1001"
                        min="1"
                        required>
                </div>

                <!-- メールアドレス -->
                <div class="cancel-form-group">
                    <label for="email">メールアドレス
                        <span class="required">必須</span>
                    </label>

                    <input
                        type="email"
                        id="email"
                        name="email"
                        placeholder="例: example@mail.com"
                        maxlength="255"
                        autocomplete="email"
                        required>
                </div>

                <!-- 注意 -->
                <div class="cancel-notice">
                    <p>
                        ※予約番号とメールアドレスが一致した予約のみ
                        取り消すことができます。
                    </p>

                    <p>
                        ※予約取消後は元に戻すことができません。
                    </p>
                </div>

                <!-- ボタン -->
                <button type="submit" class="cancel-button">
                    予約を取り消す
                </button>

                <a href="${pageContext.request.contextPath}/" class="cancel-back">
                    トップページに戻る
                </a>
            </form>
        </section>
</main>

<!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />

</body>
</html>