<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Личный кабинет</title>
</head>
<body>
<p>
    <c:out value="${username}"/>, что хотите сделать с транзакциями?
</p>
<div>
    <button onclick="window.location.href='/show_transactions'" id="showTransactions">Просмотреть транзакции</button>
</div>
<div>
    <button onclick="window.location.href='/create_transaction'" id="createTransaction">Создать транзакцию</button>
</div>
<div>
    <button onclick="window.location.href='/update_transaction'" id="updateTransaction">Изменить транзакцию</button>
</div>
<div>
    <button onclick="window.location.href='/delete_transaction'" id="deleteTransaction">Удалить транзакцию</button>
</div>
<div>
    <button onclick="window.location.href='/main_transaction_page'" id="goBack">Назад</button>
</div>
</body>
</html>