<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Удалить аккаунт</title>
</head>
<body>
<form method="post" action="/delete_account">
    <p>Вы действительно хотите удалить аккаунт? Это действие невозможно отменить!</p>
    <div>
    <button type="submit" id="delete">Да, удалить аккаунт</button>
    </div>
</form>
    <div>
        <button onclick="window.location.href='/personal_account'" id = "goBack">Нет, вернуться назад</button>
    </div>
</body>
</html>
