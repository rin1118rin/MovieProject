<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>予約登録 | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<!-- 共通ヘッダー -->
<jsp:include page="/views/common/header.jsp" />


<main class="reservation-main">

    <!-- ==============================
         ページタイトル
         ============================== -->

    <div class="reservation-title">
        <h1>チケット予約</h1>
    </div>


    <!-- エラー表示 -->
    <c:if test="${not empty errorMessage}">
        <div class="reservation-error">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>


    <form
        action="${pageContext.request.contextPath}/user/reservationCreateExecute.action"
        method="post"
        id="reservationForm">

        <div class="booking-layout">


            <!-- =========================================
                 左側
                 ========================================= -->

            <div class="booking-content">


                <!-- =====================================
                     01 作品・上映日時
                     ===================================== -->

                <section class="reservation-panel">

                    <div class="step-title">
                        <span>01</span>
                        <h2>作品・上映日時</h2>
                    </div>


                    <!-- 作品 -->

                    <div class="form-group">

                        <label for="movie">
                            作品
                            <span class="required">必須</span>
                        </label>

                        <select
                            id="movie"
                            name="movieId"
                            required>

                            <option value="">
                                作品を選択してください
                            </option>

                            <c:forEach
                                var="movie"
                                items="${movies}">

                                <option
                                    value="${movie.movieId}"
                                    <c:if test="${movie.movieId == selectedMovieId}">
                                        selected
                                    </c:if>>

                                    <c:out value="${movie.title}" />

                                </option>

                            </c:forEach>

                        </select>

                    </div>


                    <!-- 上映日時 -->

                    <div class="form-group">

                        <label for="showtime">
                            上映日時
                            <span class="required">必須</span>
                        </label>

                        <select
                            id="showtime"
                            name="scheduleId"
                            required>

                            <option value="">
                                上映日時を選択してください
                            </option>

                            <c:forEach
                                var="schedule"
                                items="${schedules}">

                                <option
                                    value="${schedule.scheduleId}"
                                    data-movie-id="${schedule.movieId}"
                                    data-screen-no="${schedule.screenNo}"
                                    data-format="${schedule.screeningFormat}"
                                    data-start="${schedule.startDatetime}"
                                    <c:if test="${schedule.scheduleId == selectedScheduleId}">
                                        selected
                                    </c:if>>

                                    ${schedule.startDatetime}
                                    /
                                    SCREEN ${schedule.screenNo}
                                    /
                                    ${schedule.screeningFormat}

                                </option>

                            </c:forEach>

                        </select>

                    </div>

                </section>



                <!-- =====================================
                     02 座席
                     ===================================== -->

                <section class="reservation-panel">

                    <div class="step-title">
                        <span>02</span>
                        <h2>座席を選択</h2>
                    </div>


                    <!-- スクリーン -->

                    <div class="screen-area">

                        <div class="screen">
                            SCREEN
                        </div>

                        <p>
                            スクリーン
                        </p>

                    </div>


                    <!--
                        seats はServletから渡す。

                        seat.seatNo
                        seat.reserved

                        例：
                        A1 false
                        A2 false
                        A3 true
                    -->

                    <div class="seats">

                        <c:forEach
                            var="seat"
                            items="${seats}">

                            <label class="seat">

                                <input
                                    type="checkbox"
                                    name="seat"
                                    value="${seat.seatNo}"

                                    <c:if test="${seat.reserved}">
                                        disabled
                                    </c:if>>

                                <span>
                                    <c:out value="${seat.seatNo}" />
                                </span>

                            </label>

                        </c:forEach>

                    </div>


                    <!-- 座席凡例 -->

                    <div class="seat-legend">

                        <div>
                            <span class="legend-seat available"></span>
                            選択可能
                        </div>

                        <div>
                            <span class="legend-seat selected"></span>
                            選択中
                        </div>

                        <div>
                            <span class="legend-seat sold"></span>
                            販売済み
                        </div>

                    </div>

                </section>



                <!-- =====================================
                     03 券種・枚数
                     ===================================== -->

                <section class="reservation-panel">

                    <div class="step-title">
                        <span>03</span>
                        <h2>券種・枚数</h2>
                    </div>


                    <!-- 一般 -->

                    <div class="ticket-row">

                        <div>
                            <strong>一般</strong>
                            <p>1,900円</p>
                        </div>

                        <select
                            id="adult"
                            name="adult">

                            <option value="0" selected>0枚</option>
                            <option value="1">1枚</option>
                            <option value="2">2枚</option>
                            <option value="3">3枚</option>
                            <option value="4">4枚</option>

                        </select>

                    </div>


                    <!-- 学生 -->

                    <div class="ticket-row">

                        <div>
                            <strong>学生</strong>
                            <p>1,500円</p>
                        </div>

                        <select
                            id="student"
                            name="student">

                            <option value="0" selected>0枚</option>
                            <option value="1">1枚</option>
                            <option value="2">2枚</option>
                            <option value="3">3枚</option>
                            <option value="4">4枚</option>

                        </select>

                    </div>


                    <!-- シニア -->

                    <div class="ticket-row">

                        <div>
                            <strong>シニア</strong>
                            <p>1,200円</p>
                        </div>

                        <select
                            id="senior"
                            name="senior">

                            <option value="0" selected>0枚</option>
                            <option value="1">1枚</option>
                            <option value="2">2枚</option>
                            <option value="3">3枚</option>
                            <option value="4">4枚</option>

                        </select>

                    </div>

                </section>



                <!-- =====================================
                     04 お客様情報
                     ===================================== -->

                <section class="reservation-panel">

                    <div class="step-title">
                        <span>04</span>
                        <h2>お客様情報</h2>
                    </div>


                    <p class="customer-note">
                        予約に必要なお客様情報を入力してください。
                    </p>


                    <!-- 氏名 -->

                    <div class="form-group">

                        <label for="customerName">

                            氏名

                            <span class="required">
                                必須
                            </span>

                        </label>

                        <input
                            type="text"
                            id="customerName"
                            name="customerName"
                            placeholder="例：大原 太郎"
                            autocomplete="name"
                            maxlength="100"
                            required>

                    </div>


                    <!-- フリガナ -->

                    <div class="form-group">

                        <label for="customerKana">

                            フリガナ

                            <span class="required">
                                必須
                            </span>

                        </label>

                        <input
                            type="text"
                            id="customerKana"
                            name="customerKana"
                            placeholder="例：オオハラ タロウ"
                            maxlength="100"
                            required>

                    </div>


                    <!-- メールアドレス -->

                    <div class="form-group">

                        <label for="email">

                            メールアドレス

                            <span class="required">
                                必須
                            </span>

                        </label>

                        <input
                            type="email"
                            id="email"
                            name="email"
                            placeholder="例：example@mail.com"
                            autocomplete="email"
                            maxlength="255"
                            required>

                        <p class="input-note">
                            予約完了メールをこちらのアドレスに送信します。
                        </p>

                    </div>


                    <!-- 電話番号 -->

                    <div class="form-group">

                        <label for="phone">

                            電話番号

                            <span class="required">
                                必須
                            </span>

                        </label>

                        <input
                            type="tel"
                            id="phone"
                            name="phone"
                            placeholder="例：09012345678"
                            autocomplete="tel"
                            inputmode="tel"
                            maxlength="20"
                            required>

                    </div>

                </section>



                <!-- =====================================
                     05 支払い方法
                     ===================================== -->

                <section class="reservation-panel">

                    <div class="step-title">
                        <span>05</span>
                        <h2>支払い方法</h2>
                    </div>


                    <div class="payment-list">


                        <!-- 窓口 -->

                        <label class="payment-option">

                            <input
                                type="radio"
                                name="payment"
                                value="counter"
                                checked>

                            <span>

                                <strong>
                                    窓口支払い
                                </strong>

                                <small>
                                    劇場窓口でお支払い
                                </small>

                            </span>

                        </label>


                        <!-- クレジットカード -->

                        <label class="payment-option">

                            <input
                                type="radio"
                                name="payment"
                                value="card">

                            <span>

                                <strong>
                                    クレジットカード
                                </strong>

                                <small>
                                    オンラインで決済
                                </small>

                            </span>

                        </label>

                    </div>

                </section>

            </div>



            <!-- =========================================
                 右側
                 ========================================= -->

            <aside class="booking-summary">

                <div class="summary-title">

                    <span>06</span>

                    <h2>
                        予約内容
                    </h2>

                </div>


                <dl class="details">

                    <dt>
                        作品
                    </dt>

                    <dd id="summaryMovie">
                        未選択
                    </dd>


                    <dt>
                        上映日時
                    </dt>

                    <dd id="summaryShowtime">
                        未選択
                    </dd>


                    <dt>
                        座席
                    </dt>

                    <dd id="summarySeats">
                        未選択
                    </dd>


                    <dt>
                        券種
                    </dt>

                    <dd id="summaryTickets">
                        未選択
                    </dd>


                    <dt>
                        支払い
                    </dt>

                    <dd id="summaryPayment">
                        窓口支払い
                    </dd>

                </dl>


                <!-- 合計 -->

                <div class="total">

                    <span>
                        合計
                    </span>

                    <strong>

                        <span id="summaryTotal">
                            0
                        </span>

                        <small>
                            円
                        </small>

                    </strong>

                </div>


                <!-- 確認 -->

                <label class="confirm-check">

                    <input
                        type="checkbox"
                        required>

                    <span>
                        予約内容を確認しました
                    </span>

                </label>


                <!-- 予約ボタン -->

                <button
                    class="reservation-button"
                    type="submit">

                    予約内容を確定する

                </button>


                <a
                    class="back-link"
                    href="${pageContext.request.contextPath}/user/scheduleList.action">

                    ← 上映スケジュールに戻る

                </a>

            </aside>

        </div>

    </form>

</main>


<!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />


<!-- =========================================
     予約内容リアルタイム表示
     ========================================= -->

<script src="${pageContext.request.contextPath}/js/reservation.js"></script>


</body>
</html>