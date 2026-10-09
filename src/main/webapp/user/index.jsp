<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>トップページ | O-HARAFILM</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user.css">
</head>
<body>

    <!-- 共通ヘッダー -->
    <jsp:include page="/views/common/header.jsp" />

    <main class="user-main">

        <!-- 宣伝画像：横にスクロールできます -->
        <section class="promotion" aria-label="上映作品の紹介">

            <!-- 左矢印 -->
            <button class="slider-arrow slider-prev" type="button" aria-label="前へ">
                &#10094;
            </button>

            <div class="promotion-list">

                <!-- 修正済み：予約リンクは scheduleList.action に飛ばす -->
                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/scheduleList.action">
                    <img src="${pageContext.request.contextPath}/images/banners/banner5.jpg"
                        alt="予約">

                    <div class="promotion-text">
                        <h2>次の感動を予約しよう。</h2>
                        <p>観たい作品と上映日時を選んで、チケット予約。</p>
                        <span>チケット予約はこちら →</span>
                    </div>
                </a>

                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/movieList.action?id=1">
                    <img src="${pageContext.request.contextPath}/images/banners/banner1.jpg"
                        alt="ますお">

                    <div class="promotion-text">
                        <h2>ますお</h2>
                        <p>ー その名前を呼んではいけない ー</p>
                        <span>作品詳細を見る →</span>
                    </div>
                </a>

                <a class="promotion-item"
                   href="${pageContext.request.contextPath}/user/movieList.action?id=2">
                    <img src="${pageContext.request.contextPath}/images/banners/banner2.jpg"
                        alt="エイリアン">

                    <div class="promotion-text">
                        <h2>エイリアン</h2>
