<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- 共通ヘッダー(管理者) -->
<header class="header">
    <div class="header-inner">
        <a class="logo" href="${pageContext.request.contextPath}/admin/index.jsp">
            <span class="logo-mark">O</span>
            <span>-HARAFILM <small>管理</small></span>
        </a>

        <nav aria-label="管理者メニュー">
            <a href="${pageContext.request.contextPath}/admin/movies/list.jsp">
                上映映画
            </a>

            <a href="${pageContext.request.contextPath}/admin/schedule/list.jsp">
                上映スケジュール
            </a>

            <a href="${pageContext.request.contextPath}/admin/analysis/index.jsp">
                分析一覧
            </a>

            <a href="${pageContext.request.contextPath}/admin/contact/list.jsp">
                お問い合わせ
            </a>

            <%-- ログイン済みなら「ログアウト」、未ログインなら「ログイン」 --%>
            <c:choose>
                <c:when test="${not empty sessionScope.adminId}">
                    <a href="${pageContext.request.contextPath}/admin/logout">
                        ログアウト
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/admin/login.jsp">
                        ログイン
                    </a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>