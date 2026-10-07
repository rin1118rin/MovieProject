<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>上映映画一覧</title>
<style>

body{
    font-family: sans-serif;
    margin:40px;
}

.title-area{
    display:flex;
    justify-content:space-between;
    align-items:center;
}

.register-btn{
    background:#d8234a;
    color:white;
    border:none;
    padding:10px 20px;
    cursor:pointer;
}

table{
    width:100%;
    border-collapse:collapse;
    margin-top:20px;
}

th{
    background:#f0f0f0;
}

th,td{
    border:1px solid #ddd;
    padding:10px;
    text-align:center;
}

.button-area{
    text-align:right;
    margin-bottom:20px;
}

.register-btn{
    background-color:#d62852;
    color:white;
    border:none;
    padding:10px 20px;
}
</style>

</head>
<body>

<h1>上映映画一覧</h1>

<div class="button-area">
    <button class="register-btn">
        上映映画登録
    </button>
</div>

<table border="1">
    <tr>
        <th>映画ID</th>
        <th>作品名</th>
        <th>上映時間</th>
        <th>状態</th>
        <th>操作</th>
        
    </tr>

    <tr>
        <td>M001</td>
        <td>星降る駅で</td>
        <td>105分</td>
        <td>上映中</td>
        <td>編集</td>
    </tr>
    
</table>

</body>
</html>