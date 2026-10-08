<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>お問い合わせ | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<!-- ==============================
     共通ヘッダー
     ============================== -->
<jsp:include page="/views/common/header.jsp" />


<main class="contact-main">

    <!-- ==============================
         ページタイトル
         ============================== -->

    <div class="contact-title">

        <h1>お問い合わせ</h1>
        <p>O-HARAFILMへのお問い合わせはこちらからお願いいたします。</p>

    </div>


    <!-- ==============================
         お問い合わせフォーム
         ============================== -->

    <section class="contact-panel">

        <!-- エラー表示 -->
        <c:if test="${not empty errorMessage}">

            <div class="contact-error">
                <c:out value="${errorMessage}" />
            </div>

        </c:if>


        <form
            action="${pageContext.request.contextPath}/contact"
            method="post"
            class="contact-form">

            <!-- お名前 -->

            <div class="contact-form-group">

                <label for="name">お名前
                    <span class="contact-required">必須</span>
                </label>

                <input
                    type="text"
                    id="name"
                    name="name"
                    placeholder="例：シネマ 太郎"
                    required>

            </div>


            <!-- メールアドレス -->

            <div class="contact-form-group">

                <label for="email">メールアドレス
                    <span class="contact-required">必須</span>
                </label>

                <input
                    type="email"
                    id="email"
                    name="email"
                    placeholder="例：cinema@example.com"
                    required>

            </div>

            <!-- お問い合わせ種別 -->
            <div class="contact-form-group">

                <label for="category">お問い合わせ種別
                    <span class="contact-required">必須</span>
                </label>

                <select
                    id="category"
                    name="category"
                    required>

                    <option value="">
                        選択してください
                    </option>

                    <option value="reservation">
                        チケット・予約について
                    </option>

                    <option value="movie">
                        上映作品について
                    </option>

                    <option value="facility">
                        劇場・施設について
                    </option>

                    <option value="other">
                        その他
                    </option>

                </select>

            </div>


            <!-- お問い合わせ内容 -->

            <div class="contact-form-group">

                <label for="message">お問い合わせ内容
                    <span class="contact-required">必須</span>
                </label>

                <textarea
                    id="message"
                    name="message"
                    rows="7"
                    placeholder="お問い合わせ内容を入力してください。"
                    required></textarea>

            </div>


            <!-- 注意 -->

            <div class="contact-notice">
                <p>※ 内容によっては回答までにお時間をいただく場合があります。</p>
                <p>※ 入力されたメールアドレスに返信いたします。</p>
            </div>

            <!-- 送信 -->
            <div class="contact-actions">

                <button type="submit" class="contact-submit">
                    送信する
                </button>

            </div>

        </form>

    </section>

</main>


<!-- ==============================
     共通フッター
     ============================== -->
<jsp:include page="/views/common/footer.jsp" />

</body>
</html>