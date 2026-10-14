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
  
  <style>
    /* 座席予約マップ（ヒートマップ）用スタイル */
    .seat-map-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin: 20px 0;
    }
    .seat-map-screen {
      width: 80%;
      max-width: 400px;
      background: #4a5568;
      color: #fff;
      text-align: center;
      padding: 8px;
      border-radius: 4px;
      margin-bottom: 25px;
      font-weight: bold;
      letter-spacing: 3px;
      font-size: 0.9rem;
    }
    .seat-map-grid {
      display: flex;
      flex-direction: column;
      gap: 10px;
      width: 100%;
      max-width: 450px;
    }
    .seat-map-row {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
    }
    .seat-map-row-label {
      width: 20px;
      font-weight: bold;
      text-align: center;
      color: #4a5568;
    }
    .seat-map-seats {
      display: flex;
      gap: 8px;
    }
    .seat-chip {
      width: 48px;
      height: 42px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      font-size: 0.8rem;
      font-weight: bold;
      color: #fff;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }
    .seat-chip.available {
      background-color: #38a169; /* 空席：緑 */
    }
    .seat-chip.reserved {
      background-color: #e53e3e; /* 予約あり：赤基調（インラインで濃淡を制御） */
    }
    .seat-count-badge {
      font-size: 0.65rem;
      font-weight: normal;
      opacity: 0.9;
    }
    .seat-map-legend {
      display: flex;
      gap: 25px;
      margin-top: 25px;
      font-size: 0.9rem;
      color: #4a5568;
      align-items: center;
    }
    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
    }
    .legend-color {
      display: inline-block;
      width: 16px;
      height: 16px;
      border-radius: 3px;
    }
    .legend-color.available { background-color: #38a169; }
    .legend-color.reserved { background-color: #e53e3e; }
  </style>

  <script>
    function toggleFilterMode() {
      const mode = document.getElementById('analysisMode').value;
      const dateDiv = document.getElementById('filterDaily');
      const movieDiv = document.getElementById('filterMovie');

      if (mode === 'daily') {
        dateDiv.style.display = 'inline-block';
        movieDiv.style.display = 'none';
      } else if (mode === 'movie') {
        dateDiv.style.display = 'none';
        movieDiv.style.display = 'inline-block';
      } else if (mode === 'date_movie') {
        dateDiv.style.display = 'inline-block';
        movieDiv.style.display = 'inline-block';
      }
    }
    window.onload = function() {
      toggleFilterMode();
    };
  </script>
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/adminMenu.action">管理者メインメニュー</a> / 分析一覧
    </p>
    <div class="page-title"><div><h1>分析一覧</h1></div></div>

    <%-- 分析切り替えフォーム --%>
    <section class="panel" style="margin-bottom: 20px;">
      <form method="get" action="${pageContext.request.contextPath}/admin/analyticsList.action" style="display: flex; gap: 15px; align-items: center; flex-wrap: wrap;">
        <div>
          <label for="analysisMode"><strong>分析種別：</strong></label>
          <select name="mode" id="analysisMode" onchange="toggleFilterMode()" class="form-control">
            <option value="daily" ${param.mode == 'daily' or empty param.mode ? 'selected' : ''}>日別分析</option>
            <option value="movie" ${param.mode == 'movie' ? 'selected' : ''}>映画別分析（全期間）</option>
            <option value="date_movie" ${param.mode == 'date_movie' ? 'selected' : ''}>映画別分析(指定日のみ)</option>
          </select>
        </div>

        <%-- 日付選択 --%>
        <div id="filterDaily" style="display: none;">
          <label for="date"><strong>対象日付：</strong></label>
          <input type="date" id="date" name="date" value="${not empty param.date ? param.date : analysisDate}">
        </div>

        <%-- 映画選択 --%>
        <div id="filterMovie" style="display: none;">
          <label for="movieId"><strong>対象映画：</strong></label>
          <select name="movieId" id="movieId">
            <option value="">映画を選択してください</option>
            <c:forEach var="m" items="${movieList}">
              <option value="${m.movieId}" ${param.movieId == m.movieId ? 'selected' : ''}>
                <c:out value="${m.title}" />
              </option>
            </c:forEach>
          </select>
        </div>

        <div>
          <button class="button" type="submit">更新</button>
        </div>
      </form>
    </section>

    <c:choose>
      <%-- 映画別(全期間)または映画別（指定日のみ）で、映画未選択の場合 --%>
      <c:when test="${(param.mode == 'movie' or param.mode == 'date_movie') and empty selectedMovie}">
        <section class="panel">
          <h2>分析</h2>
          <p>対象の映画を選択してください。</p>
        </section>
      </c:when>

      <%-- データが存在する場合 --%>
      <c:when test="${scheduleCount > 0 and totalSeats > 0}">
        <c:set var="vacantSeats" value="${totalSeats - reservedSeats}" />
        <c:set var="totalRate" value="${reservedSeats * 100 / totalSeats}" />

        <%-- 全体の予約割合 --%>
        <section class="panel">
          <h2>座席の予約割合分析</h2>
          <c:choose>
            <c:when test="${param.mode == 'date_movie'}">
              <c:set var="wd" value="${analysisDate.dayOfWeek.value}" />
              <p>${analysisDate.year}年${analysisDate.monthValue}月${analysisDate.dayOfMonth}日（${fn:substring('月火水木金土日', wd - 1, wd)}） ／ 映画：<strong><c:out value="${selectedMovie.title}" /></strong> ／ ${scheduleCount}上映回</p>
            </c:when>
            <c:when test="${param.mode == 'movie'}">
              <p>映画：<strong><c:out value="${selectedMovie.title}" /></strong> ／ 全期間・全${scheduleCount}上映回の合計</p>
            </c:when>
            <c:otherwise>
              <c:set var="wd" value="${analysisDate.dayOfWeek.value}" />
              <p>${analysisDate.year}年${analysisDate.monthValue}月${analysisDate.dayOfMonth}日（${fn:substring('月火水木金土日', wd - 1, wd)}）／全作品・${scheduleCount}上映回の合計</p>
            </c:otherwise>
          </c:choose>

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

          <div class="occupancy-meter" role="img" aria-label="予約率<fmt:formatNumber value='${totalRate}' pattern='0.0' />パーセント。">
            <span style="width:${totalRate}%"></span>
          </div>
          <p class="muted">予約済み ${reservedSeats}席 ／ 空席 ${vacantSeats}席</p>
        </section>

        <div class="analysis-charts">
          <%-- エリア別の空席割合(円グラフ) --%>
          <section class="panel">
            <h2>エリア別の空席割合</h2>
            <p class="muted">全空席${vacantSeats}席のうち、各エリアが占める割合</p>

            <c:choose>
              <c:when test="${vacantSeats > 0}">
                <c:set var="acc" value="0" />
                <c:set var="gradient" value="" />
                <c:forEach var="a" items="${areaStats}" varStatus="st">
                  <c:set var="share" value="${(a.total - a.reserved) * 100 / vacantSeats}" />
                  <c:set var="to" value="${acc + share}" />
                  <c:set var="gradient" value="${gradient}${st.first ? '' : ', '}var(--area-${st.index}) ${acc}% ${to}%" />
                  <c:set var="acc" value="${to}" />
                </c:forEach>

                <div class="vacancy-pie" style="background:conic-gradient(${gradient})" role="img">
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

          <%-- 列ごとの予約率(棒グラフ) --%>
          <section class="panel">
            <h2>列ごとの予約率</h2>
            <p class="muted">各列の総座席数に対する予約済み座席の割合</p>

            <div class="row-chart" role="img">
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

        <%-- エリア別の予約状況(表) --%>
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
        </section>

        <%-- 座席予約状況マップ（ヒートマップ） --%>
        <section class="panel">
          <h2>座席予約状況マップ</h2>
          <p class="muted">対象期間内における各座席の人気度（赤の濃さと数値：予約された回数）</p>
          
          <div class="seat-map-container">
            <div class="seat-map-screen">
              SCREEN
            </div>

            <div class="seat-map-grid">
              <c:forEach var="rowChar" items="${fn:split('A,B,C,D,E', ',')}">
                <div class="seat-map-row">
                  <span class="seat-map-row-label"><c:out value="${rowChar}" /></span>
                  <div class="seat-map-seats">
                    <c:forEach begin="1" end="6" var="seatNum">
                      <c:set var="currentSeatNo" value="${rowChar}${seatNum}" />
                      
                      <%-- 座席ごとの予約回数を取得（存在しない場合は0） --%>
                      <c:set var="resCount" value="${seatReservedCountMap[currentSeatNo] != null ? seatReservedCountMap[currentSeatNo] : 0}" />

                      <c:choose>
                        <c:when test="${resCount > 0}">
                          <%-- 予約回数に応じて赤色の透明度を変化させる --%>
                          <c:set var="opacity" value="${0.3 + (resCount / scheduleCount) * 0.7}" />
                          <c:if test="${opacity > 1.0}"><c:set var="opacity" value="1.0" /></c:if>
                          
                          <div class="seat-chip reserved" style="background-color: rgba(229, 62, 62, ${opacity});" title="${currentSeatNo}: 予約回数 ${resCount}回 / 全 ${scheduleCount}回">
                            <span>${currentSeatNo}</span>
                            <span class="seat-count-badge">${resCount}回</span>
                          </div>
                        </c:when>
                        <c:otherwise>
                          <div class="seat-chip available" title="${currentSeatNo}: 空席（予約0回）">
                            <span>${currentSeatNo}</span>
                            <span class="seat-count-badge">0回</span>
                          </div>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>
                  </div>
                </div>
              </c:forEach>
            </div>

            <div class="seat-map-legend">
              <div class="legend-item">
                <span class="legend-color reserved"></span> 予約あり（濃いほど人気）
              </div>
              <div class="legend-item">
                <span class="legend-color available"></span> 空席（予約なし）
              </div>
            </div>
          </div>
        </section>
      </c:when>

      <c:otherwise>
        <section class="panel">
          <h2>座席の予約割合分析</h2>
          <p>該当する条件の上映スケジュールがないため、分析できません。</p>
        </section>
      </c:otherwise>
    </c:choose>

  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>