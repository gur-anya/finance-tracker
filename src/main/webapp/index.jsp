<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Трекер финансов</title>

</head>
<body>
<p>
    Добро пожаловать в трекер финансов! Выберите, что хотите сделать:
</p>
<button onclick="window.location.href='/registration'" id = "registration">Зарегистрировать нового пользователя</button>

<button onclick="window.location.href='/login'" id = "login">Войти в существующий аккаунт</button>

</body>
</html>