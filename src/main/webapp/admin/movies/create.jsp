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
      <a href="${pageContext.request.contextPath}/admin/index.jsp">管理者メインメニュー</a> / 上映映画登録
    </p>
    <div class="page-title"><div><h1>上映映画登録</h1></div></div>

    <section class="panel narrow">
      <h2>新しい作品を登録</h2>

      <%-- 入力に問題があったとき、Actionが "errorMessage" に入れた文章を表示する --%>
      <c:if test="${not empty errorMessage}">
        <p class="error-message" role="alert"><c:out value="${errorMessage}" /></p>
      </c:if>

      <%-- 画像を送るので enctype="multipart/form-data" が必要 --%>
      <form action="${pageContext.request.contextPath}/admin/movies/create/execute"
            method="post" enctype="multipart/form-data">
        <label for="title">作品名</label>
        <input id="title" name="title" type="text" required maxlength="100"
               value="<c:out value='${param.title}' />">

        <label for="movie-image">映画の画像</label>
        <input id="movie-image" name="image" type="file"
               accept="image/jpeg,image/png,image/webp" aria-describedby="movie-image-help">
        <p id="movie-image-help" class="muted">ポスター画像を選択してください(JPG・PNG・WebP)。</p>

        <label for="duration">上映時間(分)</label>
        <input id="duration" name="duration" type="number" required min="1" max="600"
               value="<c:out value='${param.duration}' />">

        <label for="genre">ジャンル</label>
        <select id="genre" name="genre">
          <option>ドラマ</option>
          <option>青春</option>
          <option>SF</option>
          <option>アニメ</option>
          <option>その他</option>
        </select>

        <label for="release-start">公開開始日</label>
        <input id="release-start" name="releaseStartDate" type="date" required
               value="<c:out value='${param.releaseStartDate}' />">

        <label for="release-end">公開終了日</label>
        <input id="release-end" name="releaseEndDate" type="date" required
               value="<c:out value='${param.releaseEndDate}' />">

        <label for="age-limit">年齢制限</label>
        <select id="age-limit" name="ageLimit">
          <option>全年齢</option>
          <option>PG12</option>
          <option>R15+</option>
          <option>R18+</option>
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
          <a class="button secondary" href="${pageContext.request.contextPath}/admin/movies/list.jsp">戻る</a>
        </div>
      </form>
    </section>
  </main>

  <!-- 共通フッター(管理者) -->
  <jsp:include page="/views/common/admin-footer.jsp" />
</body>
</html>