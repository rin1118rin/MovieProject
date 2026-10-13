<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<link rel="icon"
      type="image/x-icon"
      href="${pageContext.request.contextPath}/images/favicon.ico?v=4">
<!-- 共通ヘッダー -->
<header class="header">
    <a class="header-logo" href="${pageContext.request.contextPath}/user/userMenu.action">
        <img src="${pageContext.request.contextPath}/images/logo.png" alt="O-HARAFILM">
    </a>

    <nav aria-label="メインメニュー">
        <a href="${pageContext.request.contextPath}/user/scheduleList.action">
            上映スケジュール
        </a>

        <a href="${pageContext.request.contextPath}/user/movieList.action">
            作品一覧
        </a>

        <a href="${pageContext.request.contextPath}/user/reservationCreate.action">
            予約
        </a>

        <a href="${pageContext.request.contextPath}/user/reservationCancel.action">
            予約取消
        </a>

        <a href="${pageContext.request.contextPath}/user/inquiryCreate.action">
            お問い合わせ
        </a>
    </nav>
</header>