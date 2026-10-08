<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>分析一覧 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / 分析一覧
    </p>
    <div class="page-title"><div><h1>分析一覧</h1></div></div>

    <%--
      Actionが request に入れておく値(座席の予約状況を、集計した結果):
        analysisDate  : 分析する日(LocalDate)
        scheduleCount : その日の上映回の数
        totalSeats    : 総座席数(その日の全上映回の合計)
        reservedSeats : 予約済みの座席数(同じく合計)
        areaStats     : エリア別の集計のリスト(前方・中央・後方の順)
                        1件が持つ値: name(前方など) / rowsLabel(A列、B・C列など) / total / reserved
        rowStats      : 列別の集計のリスト(A列・B列…の順)
                        1件が持つ値: name(A、B…) / areaName(前方など) / total / reserved
        topArea       : 予約率がいちばん高いエリア(areaStats の中の1件)
        topRow        : 予約率がいちばん高い列(rowStats の中の1件)
    --%>
    <c:set var="wd" value="${analysisDate.dayOfWeek.value}" />

    <c:choose>
      <c:when test="${scheduleCount > 0 and totalSeats > 0}">
        <c:set var="vacantSeats" value="${totalSeats - reservedSeats}" />
        <c:set var="totalRate" value="${reservedSeats * 100 / totalSeats}" />

        <%-- ① 全体の予約割合 --%>
        <section class="panel">
          <h2>座席の予約割合分析</h2>
          <p>${analysisDate.year}年${analysisDate.monthValue}月${analysisDate.dayOfMonth}日（${fn:substring('月火水木金土日', wd - 1, wd)}）／全作品・${scheduleCount}上映回の合計</p>

          <div class="admin-stats seat-totals">
            <div>
              <small>全体の予約率</small>
              <strong><fmt:formatNumber value="${totalRate}" pattern="0.0" /><span class="stat-unit">%</span></strong>
            </div>
            <div>
              <small>予約済み／総座席数</small>
              <strong>${reservedSeats}<span class="stat-unit">／${totalSeats}席</span></strong>
            </div>
            <div>
              <small>空席数</small>
              <strong>${vacantSeats}<span class="stat-unit">席</span></strong>
            </div>
            <div>
              <small>予約率が高いエリア</small>
              <strong><c:out value="${topArea.name}" /><span class="stat-unit"><fmt:formatNumber value="${topArea.reserved * 100 / topArea.total}" pattern="0.0" />%</span></strong>
            </div>
          </div>

          <div class="occupancy-meter" role="img"
               aria-label="全${totalSeats}席のうち${reservedSeats}席が予約済み。予約率<fmt:formatNumber value='${totalRate}' pattern='0.0' />パーセント。">
            <span style="width:${totalRate}%"></span>
          </div>
          <p class="muted">予約済み ${reservedSeats}席 ／ 空席 ${vacantSeats}席</p>
        </section>

        <div class="analysis-charts">
          <%-- ② エリア別の空席割合(円グラフ) --%>
          <section class="panel">
            <h2>エリア別の空席割合</h2>
            <p class="muted">全空席${vacantSeats}席のうち、各エリアが占める割合</p>

            <c:choose>
              <c:when test="${vacantSeats > 0}">
                <%-- 円グラフの色の境目(%)を、各エリアの空席数から計算する --%>
                <c:set var="acc" value="0" />
                <c:set var="gradient" value="" />
                <c:forEach var="a" items="${areaStats}" varStatus="st">
                  <c:set var="share" value="${(a.total - a.reserved) * 100 / vacantSeats}" />
                  <c:set var="to" value="${acc + share}" />
                  <c:set var="gradient" value="${gradient}${st.first ? '' : ', '}var(--area-${st.index}) ${acc}% ${to}%" />
                  <c:set var="acc" value="${to}" />
                </c:forEach>

                <div class="vacancy-pie" style="background:conic-gradient(${gradient})" role="img"
                     aria-label="空席の内訳。<c:forEach var='a' items='${areaStats}' varStatus='st'><c:out value='${a.name}' />${a.total - a.reserved}席<fmt:formatNumber value='${(a.total - a.reserved) * 100 / vacantSeats}' pattern='0.0' />%<c:if test='${not st.last}'>、</c:if></c:forEach>。">
                  <%-- 円の中の数字(各エリアの真ん中の角度に置く) --%>
                  <c:set var="acc" value="0" />
                  <c:forEach var="a" items="${areaStats}" varStatus="st">
                    <c:set var="share" value="${(a.total - a.reserved) * 100 / vacantSeats}" />
                    <c:if test="${share >= 5}">
                      <span class="pie-label area-${st.index}" style="--mid:${(acc + share / 2) * 3.6}deg"><fmt:formatNumber value="${share}" pattern="0.0" />%</span>
                    </c:if>
                    <c:set var="acc" value="${acc + share}" />
                  </c:forEach>
                </div>

                <ul class="chart-legend">
                  <c:forEach var="a" items="${areaStats}" varStatus="st">
                    <li>
                      <span class="swatch area-${st.index}"></span><c:out value="${a.name}" />（<c:out value="${a.rowsLabel}" />）
                      <strong>${a.total - a.reserved}席・<fmt:formatNumber value="${(a.total - a.reserved) * 100 / vacantSeats}" pattern="0.0" />%</strong>
                    </li>
                  </c:forEach>
                </ul>
              </c:when>
              <c:otherwise>
                <p>空席はありません。</p>
              </c:otherwise>
            </c:choose>
          </section>

          <%-- ③ 列ごとの予約率(棒グラフ) --%>
          <section class="panel">
            <h2>列ごとの予約率</h2>
            <p class="muted">各列の総座席数に対する予約済み座席の割合</p>

            <div class="row-chart" role="img"
                 aria-label="<c:forEach var='r' items='${rowStats}' varStatus='st'><c:out value='${r.name}' />列<fmt:formatNumber value='${r.reserved * 100 / r.total}' pattern='0.0' />%<c:if test='${not st.last}'>、</c:if></c:forEach>。">
              <div class="chart-scale"><span>100%</span><span>75%</span><span>50%</span><span>25%</span><span>0%</span></div>
              <div class="bar-plot">
                <c:forEach var="r" items="${rowStats}">
                  <div class="bar-column">
                    <span class="bar-value"><fmt:formatNumber value="${r.reserved * 100 / r.total}" pattern="0.0" />%</span>
                    <div class="bar-area"><div class="row-bar" style="height:${r.reserved * 100 / r.total}%"></div></div>
                    <span class="bar-name"><c:out value="${r.name}" />列</span>
                  </div>
                </c:forEach>
              </div>
            </div>

            <p class="analysis-note"><c:out value="${topRow.name}" />列の予約率が最も高く、<fmt:formatNumber value="${topRow.reserved * 100 / topRow.total}" pattern="0.0" />%です。</p>
          </section>
        </div>

        <%-- ④ エリア別の予約状況(表) --%>
        <section class="panel">
          <h2>エリア別の予約状況</h2>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th scope="col">エリア</th>
                  <th scope="col">対象列</th>
                  <th scope="col">総座席数</th>
                  <th scope="col">予約済み</th>
                  <th scope="col">空席</th>
                  <th scope="col">予約率</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="a" items="${areaStats}">
                  <tr>
                    <td><c:out value="${a.name}" /></td>
                    <td><c:out value="${a.rowsLabel}" /></td>
                    <td>${a.total}席</td>
                    <td>${a.reserved}席</td>
                    <td>${a.total - a.reserved}席</td>
                    <td><fmt:formatNumber value="${a.reserved * 100 / a.total}" pattern="0.0" />%</td>
                  </tr>
                </c:forEach>
                <tr class="total-row">
                  <th scope="row">合計</th>
                  <td>全列</td>
                  <td>${totalSeats}席</td>
                  <td>${reservedSeats}席</td>
                  <td>${vacantSeats}席</td>
                  <td><fmt:formatNumber value="${totalRate}" pattern="0.0" />%</td>
                </tr>
              </tbody>
            </table>
          </div>
          <p class="muted">予約率＝予約済み座席数÷総座席数×100。</p>
        </section>

        <%-- ⑤ 列ごとの座席数(開閉できる表) --%>
        <details class="panel">
          <summary>列ごとの座席数を確認</summary>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th scope="col">列</th>
                  <th scope="col">エリア</th>
                  <th scope="col">総座席数</th>
                  <th scope="col">予約済み</th>
                  <th scope="col">空席</th>
                  <th scope="col">予約率</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="r" items="${rowStats}">
                  <tr>
                    <td><c:out value="${r.name}" />列</td>
                    <td><c:out value="${r.areaName}" /></td>
                    <td>${r.total}席</td>
                    <td>${r.reserved}席</td>
                    <td>${r.total - r.reserved}席</td>
                    <td><fmt:formatNumber value="${r.reserved * 100 / r.total}" pattern="0.0" />%</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </details>
      </c:when>

      <c:otherwise>
        <%-- その日の上映スケジュールがないときは、集計できない --%>
        <section class="panel">
          <h2>座席の予約割合分析</h2>
          <p>${analysisDate.year}年${analysisDate.monthValue}月${analysisDate.dayOfMonth}日（${fn:substring('月火水木金土日', wd - 1, wd)}）の上映スケジュールがないため、分析できません。</p>
        </section>
      </c:otherwise>
    </c:choose>

  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>