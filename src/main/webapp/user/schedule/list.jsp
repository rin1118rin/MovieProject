<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>上映スケジュール一覧 | O-HARAFILM</title>
  
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<!-- 共通ヘッダー -->
<jsp:include page="/views/common/header.jsp" />

<main class="schedule-main">
    <!-- ==============================
         ページタイトル
         ============================== -->

    <div class="schedule-title">
        <h1>上映スケジュール</h1>
    </div>

    <!-- ==============================
         日付選択
         ============================== -->
    <div class="schedule-date-list">
        <c:forEach var="date" items="${dates}">
            <a href="${pageContext.request.contextPath}/user/scheduleList.action?date=${date}"
                    class="schedule-date">
                <span class="schedule-month-day">
                    ${date.monthValue}/${date.dayOfMonth}
                </span>

                <span class="schedule-week">
                    <c:choose>
                        <c:when test="${date.dayOfWeek.value == 1}">
                            月
                        </c:when>
                        <c:when test="${date.dayOfWeek.value == 2}">
                            火
                        </c:when>
                        <c:when test="${date.dayOfWeek.value == 3}">
                            水
                        </c:when>
                        <c:when test="${date.dayOfWeek.value == 4}">
                            木
                        </c:when>
                        <c:when test="${date.dayOfWeek.value == 5}">
                            金
                        </c:when>
                        <c:when test="${date.dayOfWeek.value == 6}">
                            土
                        </c:when>
                        <c:otherwise>
                            日
                        </c:otherwise>
                    </c:choose>
                </span>
            </a>
        </c:forEach>
    </div>

    <!-- ==============================
         上映作品
         ============================== -->

    <section class="schedule-content">
        <h2 class="schedule-section-title">
            <c:out value="${selectedDate}"/> の上映作品
        </h2>

        <!-- 上映作品がない場合 -->
        <c:if test="${empty movies}">
            <div class="schedule-empty">
                <span class="material-symbols-outlined">
                    event_busy
                </span>
                <p>この日の上映作品はありません</p>
            </div>
        </c:if>

        <!-- 映画一覧 -->
        <c:forEach var="movie" items="${movies}">
            <div class="schedule-movie">
                <!-- ポスター -->
                <div class="schedule-poster">
                    <c:url var="posterUrl"
                        value="/images/movies/${movie.posterUrl}" />
                    <img src="<c:out value='${posterUrl}' />"
                         alt="<c:out value='${movie.title}' />のポスター">
                </div>

                <!-- 映画情報 -->
                <div class="schedule-movie-info">
                    <h3 class="schedule-movie-title">
                        <c:out value="${movie.title}"/>
                    </h3>
                    <div class="schedule-movie-meta">
                        <span>
                            上映時間:<c:out value="${movie.duration}"/>分
                        </span>

                        <span>
                            ジャンル:<c:out value="${movie.genre}"/>
                        </span>
                    </div>
                    <h4>上映時間</h4>

                    <!-- 上映時間 -->
                    <div class="schedule-times">
                        <c:forEach var="schedule" items="${movie.schedules}">
                            <a href="${pageContext.request.contextPath}/user/reservationCreate.action?scheduleId=${schedule.scheduleId}""
                                class="schedule-time">
                                <span class="schedule-start">
                                    <fmt:formatDate value="${schedule.startDatetime}" pattern="yyyy-MM-dd"/>
                                </span>
                                <span class="schdule-end">
                                    <fmt:formatDate value="${schedule.endDatetime}" pattern="yyyy-MM-dd"/>
                                </span>
                                <span class="schedule-screen">
                                    ${schedule.screenNo}
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:forEach>
    </section>
</main>

<!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />

<script src="${pageContext.request.contextPath}/js/schedule.js"></script>

</body>
</html>
