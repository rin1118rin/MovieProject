<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>予約完了 | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<!-- 共通ヘッダー -->
<jsp:include page="/views/common/header.jsp" />


<main class="reservation-complete-main">

    <section class="reservation-complete-card">


        <!-- ==============================
             完了メッセージ
             ============================== -->

        <div class="reservation-complete-header">

            <div class="reservation-complete-check">
                ✓
            </div>

            <h1>予約が完了しました</h1>

            <p class="reservation-complete-message">
                ご予約ありがとうございます。<br>
                以下の内容で予約を受け付けました。
            </p>


            <div class="reservation-number">

                <span>予約番号</span>

                <strong>
                    <c:out value="${reservation.reservationId}" />
                </strong>

            </div>

        </div>



        <!-- ==============================
             予約内容
             ============================== -->

        <div class="reservation-complete-body">

            <h2>予約内容</h2>


            <dl class="reservation-complete-details">


                <!-- お名前 -->

                <div class="reservation-complete-row">

                    <dt>お名前</dt>

                    <dd>
                        <c:out value="${reservation.customerName}" />
                    </dd>

                </div>


                <!-- 作品 -->

                <div class="reservation-complete-row">

                    <dt>作品</dt>

                    <dd>
                        <c:out value="${reservation.movieTitle}" />
                    </dd>

                </div>


                <!-- 上映日時 -->

                <div class="reservation-complete-row">

                    <dt>上映日時</dt>

                    <dd>
                        <c:out value="${reservation.showtime}" />
                    </dd>

                </div>


                <!-- 座席 -->

                <div class="reservation-complete-row">

                    <dt>座席</dt>

                    <dd>
                        <c:out value="${reservation.seats}" />
                    </dd>

                </div>


                <!-- 券種 -->

                <div class="reservation-complete-row">

                    <dt>券種</dt>

                    <dd>
                        <c:out value="${reservation.ticketInfo}" />
                    </dd>

                </div>


                <!-- 支払い -->

                <div class="reservation-complete-row">

                    <dt>支払い方法</dt>

                    <dd>

                        <c:choose>

                            <c:when test="${reservation.paymentMethod == 'counter'}">
                                窓口支払い
                            </c:when>

                            <c:when test="${reservation.paymentMethod == 'card'}">
                                クレジットカード
                            </c:when>

                            <c:otherwise>
                                <c:out value="${reservation.paymentMethod}" />
                            </c:otherwise>

                        </c:choose>

                    </dd>

                </div>


                <!-- 合計 -->

                <div class="reservation-complete-row reservation-complete-total">

                    <dt>合計金額</dt>

                    <dd>
                        <strong>
                            <c:out value="${reservation.totalPrice}" />
                            <span>円</span>
                        </strong>
                    </dd>

                </div>

            </dl>

        </div>



        <!-- ==============================
             注意
             ============================== -->

        <div class="reservation-complete-notice">

            <p>
                ※ 予約番号は劇場で確認する場合があります。
            </p>

            <p>
                ※ ご来場の際はメールアドレスに送信されたQRコードをお控えください。
            </p>

        </div>



        <!-- ==============================
             ボタン
             ============================== -->

        <div class="reservation-complete-actions">

            <a
                href="${pageContext.request.contextPath}/"
                class="reservation-complete-button">

                トップページに戻る

            </a>

        </div>

    </section>

</main>


<!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />

</body>
</html>