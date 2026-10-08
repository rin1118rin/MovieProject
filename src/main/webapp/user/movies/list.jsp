<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>作品一覧 | O-HARAFILM</title>
  <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>
<body>
  <a class="skip" href="#main">本文へ移動</a>

  <!-- 共通ヘッダー-->
  <jsp:include page="/views/common/header.jsp" />

  <main id="main" class="container">
    <p class="breadcrumb"><a href="index.html">ホーム</a> / 作品一覧</p>
    <div class="page-title"><div><h1>作品一覧</h1></div></div>
    <p>気になる作品を選んで、作品情報や上映時間をご覧ください。</p><div class="movie-grid"><article class="movie-card"><div class="poster blue" aria-label="星降る駅での文字ポスター"><span>CINEMA ORIGINAL</span><strong>星降る駅で</strong><small>夜空の下、もう一度あなたに会いたい。</small></div><div class="card-body"><p class="eyebrow">NOW SHOWING / ドラマ</p><h2>星降る駅で</h2><p>106分・日本語・2D</p><a class="text-link" href="movie-detail.html">作品詳細・チケット予約 →</a></div></article><article class="movie-card"><div class="poster sea" aria-label="海辺の約束の文字ポスター"><span>CINEMA ORIGINAL</span><strong>海辺の約束</strong><small>あの夏の続きを、探しに行こう。</small></div><div class="card-body"><p class="eyebrow">NOW SHOWING / 青春</p><h2>海辺の約束</h2><p>112分・日本語・2D</p><a class="text-link" href="movie-detail.html#film-2">作品詳細・チケット予約 →</a></div></article><article class="movie-card"><div class="poster space" aria-label="LAST ORBITの文字ポスター"><span>CINEMA ORIGINAL</span><strong>LAST ORBIT</strong><small>最後の軌道。その先にある希望。</small></div><div class="card-body"><p class="eyebrow">NOW SHOWING / SF</p><h2>LAST ORBIT</h2><p>124分・日本語・2D</p><a class="text-link" href="movie-detail.html#film-3">作品詳細・チケット予約 →</a></div></article></div>
  </main>

  <!-- 共通フッター -->
<jsp:include page="/views/common/footer.jsp" />

</body>
</html>
