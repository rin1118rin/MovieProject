<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>お問い合わせ一覧 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / お問い合わせ一覧
    </p>
    <div class="page-title"><div><h1>お問い合わせ一覧</h1></div></div>

    <%--
      Actionが "inquiries" にリストを入れておく(1行 = 1件のお問い合わせ。新しい順がおすすめ)。
      1件(inquiry)が持つ値:
        inquiryId / name / subject / sentAt(LocalDateTime) / status
    --%>
    <section class="panel">
      <h2>お問い合わせ受付状況</h2>

      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th scope="col">受付番号</th>
              <th scope="col">受付日</th>
              <th scope="col">お名前</th>
              <th scope="col">件名</th>
              <th scope="col">状態</th>
              <th scope="col">操作</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="inquiry" items="${inquiries}">
              <tr>
                <td>C<fmt:formatNumber value="${inquiry.inquiryId}" pattern="000" /></td>
                <td>${inquiry.sentAt.year}/<fmt:formatNumber value="${inquiry.sentAt.monthValue}" pattern="00" />/<fmt:formatNumber value="${inquiry.sentAt.dayOfMonth}" pattern="00" /></td>
                <td><c:out value="${inquiry.name}" /></td>
                <td><c:out value="${inquiry.subject}" /></td>
                <td><c:out value="${inquiry.status}" /></td>
                <td>
                  <%-- 回答済みのお問い合わせには、「回答する」を出さない --%>
                  <c:choose>
                    <c:when test="${inquiry.status == '回答済み'}">—</c:when>
                    <c:otherwise>
                      <a href="${pageContext.request.contextPath}/admin/contacts/reply?id=${inquiry.inquiryId}">回答する →</a>
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>

            <c:if test="${empty inquiries}">
              <tr>
                <td colspan="6">お問い合わせはありません。</td>
              </tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>