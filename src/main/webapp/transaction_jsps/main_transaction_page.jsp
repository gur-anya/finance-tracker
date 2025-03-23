<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Меню управления транзакциями</title>
</head>
<body>
<p>
    Выберите, что хотите сделать:
</p>
<div>
    <button onclick="window.location.href='/transactions_management_page'" id = "transactions">Перейти к транзакциям</button>
</div>
<div>
    <button onclick="window.location.href='/general_stats_page'" id = "stats">Перейти к статистике</button>
</div>
<div>
    <button onclick="window.location.href='/main_user_page'" id = "goback">Выйти</button>
</div>
</body>
</html>
