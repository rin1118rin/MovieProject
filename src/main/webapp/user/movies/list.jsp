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

<!-- 共通ヘッダー -->
<jsp:include page="/views/common/header.jsp" />

<main class="movies-main">

    <div class="movies-title">
        <h1>作品一覧</h1>
        <p class="movies-description">
            気になる作品を選んで、作品情報や上映時間をご覧ください。
        </p>
    </div>

    <div class="movies-list">

        <c:forEach var="movie" items="${movieList}">
            <div class="movie-card">

                <!-- 映画イメージ -->
                <div class="movie-poster">
                    <c:if test="${not empty movie.posterUrl}">
                        <c:url var="posterUrl" value="/images/movies/${movie.posterUrl}" />
                        <img src="<c:out value='${posterUrl}' />" alt="" loading="lazy">
                    </c:if>
                </div>

                <!-- 映画情報 -->
                <div class="card-body">
                    <p class="eyebrow">
                        <c:out value="${movie.ageLimit}" />
                        /
                        <c:out value="${movie.genre}" />
                    </p>

                    <h2>
                        <c:out value="${movie.title}" />
                    </h2>

                    <p>
                        <c:out value="${movie.duration}" />分
                    </p>

                    <c:url var="detailUrl" value="/movies/detail">
                        <c:param name="id" value="${movie.movieId}" />
                    </c:url>

                    <a class="text-link"
                       href="<c:out value='${detailUrl}' />">
                        作品詳細・チケット予約 →
                    </a>
                </div>

            </div>
        </c:forEach>

        <!-- 作品がない場合 -->
        <c:if test="${empty movieList}">
            <div class="movies-empty">
                <p>現在表示できる作品はありません。</p>
            </div>
        </c:if>

    </div>

</main>

<!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />

</body>
</html>