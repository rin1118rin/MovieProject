<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<link rel="icon"
      type="image/x-icon"
      href="${pageContext.request.contextPath}/images/favicon.ico?v=4">

<!-- 共通ヘッダー(管理者) -->
<header class="header">
    <div class="header-inner">
        <div class="header-brand">
            <a class="header-logo" href="${pageContext.request.contextPath}/admin/adminMenu.action">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="O-HARAFILM">
            </a>
            <span class="header-badge">管理</span>
        </div>

        <nav aria-label="管理者メニュー">
            <a href="${pageContext.request.contextPath}/admin/movieList.action">
                上映映画管理
            </a>

            <a href="${pageContext.request.contextPath}/admin/scheduleList.action">
                上映スケジュール管理
            </a>

            <a href="${pageContext.request.contextPath}/admin/analyticsList.action">
                分析一覧
            </a>

            <a href="${pageContext.request.contextPath}/admin/inquiryList.action">
                お問い合わせ管理
            </a>

            <%-- ログイン済みなら「ログアウト」、未ログインなら「ログイン」 --%>
            <c:choose>
                <c:when test="${not empty sessionScope.loginAdmin}">
                    <a href="${pageContext.request.contextPath}/admin/adminLogout.action">
                        ログアウト
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/admin/adminLogin.action">
                        ログイン
                    </a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>