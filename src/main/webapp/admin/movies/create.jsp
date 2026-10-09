<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>上映映画登録 | O-HARAFILM</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー(管理者) -->
  <jsp:include page="/views/common/admin-header.jsp" />

  <main class="container" id="main">
    <p class="breadcrumb">
      <a href="${pageContext.request.contextPath}/admin/adminMenu.action">管理者メインメニュー</a> / 上映映画登録
    </p>
    <div class="page-title"><div><h1>上映映画登録</h1></div></div>

    <section class="panel narrow">
      <h2>新しい作品を登録</h2>

      <%-- 入力に問題があったとき、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <form action="${pageContext.request.contextPath}/admin/movieCreateExecute.action" method="post">
        <label for="title">作品名</label>
        <input id="title" name="title" type="text" required maxlength="100"
               value="<c:out value='${param.title}' />">

        <label for="poster-url">ポスター画像のURL</label>
        <input id="poster-url" name="posterUrl" type="text" required maxlength="500"
               aria-describedby="poster-url-help"
               value="<c:out value='${param.posterUrl}' />">
        <p id="poster-url-help" class="muted">ポスター画像のURL(または画像のパス)を入力してください。</p>

        <label for="duration">上映時間(分)</label>
        <input id="duration" name="duration" type="number" required min="1" max="600"
               value="<c:out value='${param.duration}' />">

        <label for="genre">ジャンル</label>
        <select id="genre" name="genre">
          <option${param.genre == 'ドラマ' ? ' selected' : ''}>ドラマ</option>
          <option${param.genre == '青春' ? ' selected' : ''}>青春</option>
          <option${param.genre == 'SF' ? ' selected' : ''}>SF</option>
          <option${param.genre == 'アニメ' ? ' selected' : ''}>アニメ</option>
          <option${param.genre == 'その他' ? ' selected' : ''}>その他</option>
        </select>

        <label for="release-start">公開開始日</label>
        <input id="release-start" name="releaseStartDate" type="date" required
               value="<c:out value='${param.releaseStartDate}' />">

        <label for="release-end">公開終了日</label>
        <input id="release-end" name="releaseEndDate" type="date" required
               value="<c:out value='${param.releaseEndDate}' />">

        <label for="age-limit">年齢制限</label>
        <select id="age-limit" name="ageLimit">
          <option${param.ageLimit == '全年齢' ? ' selected' : ''}>全年齢</option>
          <option${param.ageLimit == 'PG12' ? ' selected' : ''}>PG12</option>
          <option${param.ageLimit == 'R15+' ? ' selected' : ''}>R15+</option>
          <option${param.ageLimit == 'R18+' ? ' selected' : ''}>R18+</option>
        </select>

        <label for="director">監督</label>
        <input id="director" name="director" type="text" required maxlength="100"
               value="<c:out value='${param.director}' />">

        <label for="cast">主要キャスト</label>
        <input id="cast" name="cast" type="text" required maxlength="500"
               value="<c:out value='${param.cast}' />">

        <label for="description">作品紹介</label>
        <textarea id="description" name="description" rows="5" required><c:out value="${param.description}" /></textarea>

        <div class="actions">
          <button class="button" type="submit">登録</button>
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/movieList.action">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>