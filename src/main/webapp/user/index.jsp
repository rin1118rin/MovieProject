<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

                <a class="promotion-item"
                href="${pageContext.request.contextPath}/movies/detail?id=1">
                    <img src="${pageContext.request.contextPath}/images/banners/banner5.jpg"
                        alt="予約">

                    <div class="promotion-text">
                        <h2>次の感動を予約しよう。</h2>
                        <p>観たい作品と上映日時を選んで、チケット予約。</p>
                        <span>チケット予約はこちら →</span>
                    </div>
                </a>


                <a class="promotion-item"
                href="${pageContext.request.contextPath}/movies/detail?id=1">
                    <img src="${pageContext.request.contextPath}/images/banners/banner1.jpg"
                        alt="ますお">

                    <div class="promotion-text">
                        <h2>ますお</h2>
                        <p>ー その名前を呼んではいけない ー</p>
                        <span>作品詳細を見る →</span>
                    </div>
                </a>


                <a class="promotion-item"
                href="${pageContext.request.contextPath}/movies/detail?id=2">
                    <img src="${pageContext.request.contextPath}/images/banners/banner2.jpg"
                        alt="エイリアン">

                    <div class="promotion-text">
                        <h2>エイリアン</h2>
                        <p>ー ちがっているから、きっと、好きになった。 ー</p>
                        <span>作品詳細を見る →</span>
                    </div>
                </a>


                <a class="promotion-item"
                href="${pageContext.request.contextPath}/movies/detail?id=3">
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

            <!-- ● ● ● ● ● がここに自動生成される -->
            <div class="slider-dots"></div>

        </section>

        <!-- メニュー -->
        <section class="panel">
            <h1>映画を楽しむ</h1>

            <div class="home-menu">
                <a class="button"
                   href="${pageContext.request.contextPath}/schedule">
                    上映スケジュール
                </a>

                <a class="button"
                   href="${pageContext.request.contextPath}/reservation">
                    予約
                </a>

                <a class="button"
                   href="${pageContext.request.contextPath}/reservation/cancel">
                    予約取消
                </a>
            </div>
        </section>

        <!-- 上映作品 -->
        <section>
            <h2>上映中の作品</h2>

            <div class="movie-list">

                <article class="movie-card">
                    <h3>星降る駅で</h3>
                    <p>ドラマ ／ 106分</p>
                    <a class="button"
                       href="${pageContext.request.contextPath}/movies/detail?id=1">
                        作品詳細
                    </a>
                </article>

                <article class="movie-card">
                    <h3>海辺の約束</h3>
                    <p>青春 ／ 112分</p>
                    <a class="button"
                       href="${pageContext.request.contextPath}/movies/detail?id=2">
                        作品詳細
                    </a>
                </article>

                <article class="movie-card">
                    <h3>LAST ORBIT</h3>
                    <p>SF ／ 124分</p>
                    <a class="button"
                       href="${pageContext.request.contextPath}/movies/detail?id=3">
                        作品詳細
                    </a>
                </article>

            </div>

            <p>
                <a href="${pageContext.request.contextPath}/movies">
                    上映映画一覧へ →
                </a>
            </p>
        </section>

    </main>

    <script src="${pageContext.request.contextPath}/js/slider.js"></script>

    <!-- 共通フッター -->
    <jsp:include page="/views/common/footer.jsp" />

</body>
</html>