<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!-- 共通ヘッダー -->
<header class="header">
    <a class="logo"
       href="${pageContext.request.contextPath}/home">
        <span class="logo-mark">O</span>
        <span>-HARAFILM</span>
    </a>

    <nav aria-label="メインメニュー">
        <a href="${pageContext.request.contextPath}/schedule">
            上映スケジュール
        </a>

        <a href="${pageContext.request.contextPath}/movies">
            作品一覧
        </a>

        <a href="${pageContext.request.contextPath}/reservation">
            予約
        </a>

        <a href="${pageContext.request.contextPath}/reservation/cancel">
            予約取消
        </a>

        <a href="${pageContext.request.contextPath}/contact">
            お問い合わせ
        </a>
    </nav>
</header>