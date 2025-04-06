<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Личный кабинет</title>
</head>
<body>
<p>
    <c:out value="${username}"/>, что хотите сделать?
</p>
<div>
    <button onclick="window.location.href='/update_account'" id="accountUpdate">Изменить личную информацию</button>
</div>
<div>
    <button onclick="window.location.href='/delete_account'" id="deleteAccount">Удалить аккаунт</button>
</div>
<div>
    <button onclick="window.location.href='/main_user_page'" id="goBack">Назад</button>
</div>
</body>
</html>
