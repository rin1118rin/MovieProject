<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>作品一覧 | O-HARAFILM</title>

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


<main class="movies-main">

    <!-- ==============================
         ページタイトル
         ============================== -->

    <div class="movies-title">

        <h1>作品一覧</h1>

        <p class="movies-description">
            気になる作品を選んで、作品情報や上映時間をご覧ください。
        </p>

    </div>


    <!-- ==============================
         作品一覧
         ============================== -->

    <div class="movies-list">

        <c:forEach var="movie" items="${movies}">

            <div class="movie-card">


                <!-- ==============================
                     映画イメージ部分
                     ※ポスター画像は使用しない
                     ============================== -->

                <div class="movie-poster">

                    <span>
                        CINEMA ORIGINAL / <c:out value="${movie.genre}" />
                    </span>

                    <strong>
                        <c:out value="${movie.title}" />
                    </strong>

                    <small>
                        <c:out value="${movie.catchphrase}" />
                    </small>

                </div>


                <!-- ==============================
                     映画情報
                     ============================== -->

                <div class="card-body">

                    <!-- 上映状況・ジャンル -->

                    <p class="eyebrow">

                        <c:out value="${movie.status}" />

                        /

                        <c:out value="${movie.genre}" />

                    </p>


                    <!-- 作品タイトル -->

                    <h2>
                        <c:out value="${movie.title}" />
                    </h2>


                    <!-- 作品情報 -->

                    <p>
                        <c:out value="${movie.duration}" />分
                        ・
                        <c:out value="${movie.language}" />
                        ・
                        <c:out value="${movie.format}" />
                    </p>


                    <!-- 詳細ページ -->

                    <a class="text-link"
                       href="${pageContext.request.contextPath}/movies/detail?id=${movie.movieId}">

                        作品詳細・チケット予約 →

                    </a>

                </div>

            </div>

        </c:forEach>


        <!-- ==============================
             作品がない場合
             ============================== -->

        <c:if test="${empty movies}">

            <div class="movies-empty">

                <p>
                    現在表示できる作品はありません。
                </p>

            </div>

        </c:if>

    </div>

</main>


<!-- ==============================
     共通フッター
     ============================== -->
<jsp:include page="/views/common/footer.jsp" />

</body>

</html>