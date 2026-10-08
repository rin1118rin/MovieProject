<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>作品詳細 | O-HARAFILM</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<!-- 共通ヘッダー -->
<jsp:include page="/views/common/header.jsp" />


<main class="movie-detail-main">

    <!-- ページタイトル -->
    <div class="movie-detail-page-title">
        <h1>作品詳細</h1>
    </div>


    <!-- 作品詳細 -->
    <section class="movie-detail-card">


        <!-- ==============================
             左側 ポスター
             ============================== -->

        <div class="movie-detail-poster">

            <img
                src="${pageContext.request.contextPath}/images/movies/${movie.posterUrl}"
                alt="${movie.title}">

        </div>


        <!-- ==============================
             右側 作品情報
             ============================== -->

        <div class="movie-detail-content">

            <p class="movie-detail-status">
                MOVIE /
                <c:out value="${movie.genre}" />
            </p>


            <!-- タイトル -->
            <h2 class="movie-detail-title">
                <c:out value="${movie.title}" />
            </h2>


            <!-- あらすじ -->
            <p class="movie-detail-description">
                <c:out value="${movie.description}" />
            </p>


            <!-- 作品情報 -->
            <dl class="movie-detail-info">


                <div class="movie-detail-info-row">

                    <dt>上映時間</dt>

                    <dd>
                        <c:out value="${movie.duration}" />分
                    </dd>

                </div>


                <div class="movie-detail-info-row">

                    <dt>ジャンル</dt>

                    <dd>
                        <c:out value="${movie.genre}" />
                    </dd>

                </div>


                <div class="movie-detail-info-row">

                    <dt>年齢制限</dt>

                    <dd>
                        <c:out value="${movie.ageLimit}" />
                    </dd>

                </div>


                <div class="movie-detail-info-row">

                    <dt>監督</dt>

                    <dd>
                        <c:out value="${movie.director}" />
                    </dd>

                </div>


                <div class="movie-detail-info-row">

                    <dt>出演</dt>

                    <dd>
                        <c:out value="${movie.cast}" />
                    </dd>

                </div>


                <div class="movie-detail-info-row">

                    <dt>公開日</dt>

                    <dd>
                        <c:out value="${movie.releaseStartDate}" />
                    </dd>

                </div>

            </dl>


            <!-- ボタン -->
            <div class="movie-detail-actions">

                <a href="${pageContext.request.contextPath}/schedule?movieId=${movie.movieId}"
                   class="movie-detail-schedule-button">

                    上映時間を選ぶ →

                </a>


                <a href="${pageContext.request.contextPath}/movies"
                   class="movie-detail-back-button">

                    作品一覧へ

                </a>

            </div>

        </div>

    </section>

</main>


<!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />

</body>

</html>