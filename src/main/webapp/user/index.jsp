<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>トップページ | O-HARAFILM</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
</head>
<body>

    <!-- 共通ヘッダー -->
    <jsp:include page="/views/common/header.jsp" />

    <main class="user-main">

        <!-- 宣伝画像：横にスクロールできます -->
        <section class="promotion" aria-label="上映作品の紹介">

            <!-- 左矢印 -->
            <button class="slider-arrow slider-prev" type="button" aria-label="前へ">
                &#10094;
            </button>

            <div class="promotion-list">

                <!-- 修正済み：予約リンクは scheduleList.action に飛ばす -->
                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/scheduleList.action">
                    <img src="${pageContext.request.contextPath}/images/banners/banner5.jpg"
                        alt="予約">

                    <div class="promotion-text">
                        <h2>次の感動を予約しよう。</h2>
                        <p>観たい作品と上映日時を選んで、チケット予約。</p>
                        <span>チケット予約はこちら →</span>
                    </div>
                </a>

                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/movieList.action?id=1">
                    <img src="${pageContext.request.contextPath}/images/banners/banner1.jpg"
                        alt="ますお">

                    <div class="promotion-text">
                        <h2>ますお</h2>
                        <p>ー その名前を呼んではいけない ー</p>
                        <span>作品詳細を見る →</span>
                    </div>
                </a>

                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/movieList.action?id=2">
                    <img src="${pageContext.request.contextPath}/images/banners/banner2.jpg"
                        alt="エイリアン">

                    <div class="promotion-text">
                        <h2>エイリアン</h2>
                        <p>ー ちがっているから、きっと、好きになった。 ー</p>
                        <span>作品詳細を見る →</span>
                    </div>
                </a>

                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/movieList.action?id=3">
                    <img src="${pageContext.request.contextPath}/images/banners/banner3.jpg"
                        alt="カンガルーマン">

                    <div class="promotion-text">
                        <h2>カンガルーマン</h2>
                        <p>～ 世界を跳ぶ ～</p>
                        <span>作品詳細を見る →</span>
                    </div>
                </a>

                <a class="promotion-item" href="#">
                    <img src="${pageContext.request.contextPath}/images/banners/banner4.jpg"
                        alt="ポップコーン">
                </a>

            </div>

            <!-- 右矢印 -->
            <button class="slider-arrow slider-next" type="button" aria-label="次へ">
                &#10095;
            </button>

            <div class="slider-dots"></div>

        </section>

        <!-- メニュー -->
        <section class="panel">
            <h1>映画を楽しむ</h1>

            <div class="home-menu">

                <a class="button"
                   href="${pageContext.request.contextPath}/user/scheduleList.action">
                    上映スケジュール
                </a>

                <a class="button"
                   href="${pageContext.request.contextPath}/user/reservationCreate.action">
                    予約
                </a>

                <a class="button"
                   href="${pageContext.request.contextPath}/user/reservationCancel.action">
                    予約取消
                </a>
            </div>
        </section>

        <!-- 広告バナー -->
        <section class="ad-banner">
            <img src="${pageContext.request.contextPath}/images/banners/banner6.png"
                alt="O-HARAFILM お知らせ">
        </section>

        <!-- 上映中映画ランキング -->
        <section class="ranking-section">

            <h2>上映中映画ランキング</h2>

            <div class="ranking-list">

                <article class="ranking-card">
                    <div class="ranking-number">1</div>
                    <img src="${pageContext.request.contextPath}/images/movies/ますお.png"
                        alt="ますお">

                    <div class="ranking-info">
                        <h3>ますお</h3>

                        <a class="ranking-button"
                           href="${pageContext.request.contextPath}/user/movieList.action?id=1">
                            作品詳細
                        </a>
                    </div>
                </article>

                <article class="ranking-card">
                    <div class="ranking-number">2</div>

                    <img src="${pageContext.request.contextPath}/images/movies/エイリアン.png"
                        alt="エイリアン">
                    <div class="ranking-info">
                        <h3>エイリアン</h3>

                        <a class="ranking-button"
                           href="${pageContext.request.contextPath}/user/movieList.action?id=2">
                            作品詳細
                        </a>
                    </div>
                </article>

                <article class="ranking-card">
                    <div class="ranking-number">3</div>

                    <img src="${pageContext.request.contextPath}/images/movies/カンガルーマン.png"
                        alt="カンガルーマン">

                    <div class="ranking-info">
                        <h3>カンガルーマン</h3>

                        <a class="ranking-button"
                           href="${pageContext.request.contextPath}/user/movieList.action?id=3">
                            作品詳細
                        </a>
                    </div>
                </article>
            </div>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/js/slider.js"></script>

    <!-- 共通フッター -->
    <jsp:include page="/views/common/footer.jsp" />

</body>
</html>
