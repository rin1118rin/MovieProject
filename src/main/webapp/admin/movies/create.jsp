<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>上映映画登録</title>
</head>
    
<h1>上映映画登録</h1>

<form>

<p>
作品名
<br>
<input type="text">
</p>

<p>
作品画像
<br>
<input type="file">
</p>

<p>
上映時間
<br>
<input type="number">
</p>

<p>
ジャンル
<br>
<select>
    <option>アクション</option>
    <option>アニメ</option>
    <option>SF</option>
</select>
</p>

<p>
公開日
<br>
<input type="date">
</p>

<p>
公開状態
<br>
<select>
    <option>上映中</option>
    <option>公開予定</option>
    <option>終了</option>
</select>
</p>

<p>
作品紹介
<br>
<textarea rows="5" cols="40"></textarea>
</p>

<button>登録</button>
<button type="button">戻る</button>

</form>