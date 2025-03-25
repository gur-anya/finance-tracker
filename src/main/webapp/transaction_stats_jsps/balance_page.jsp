<%--
  Created by IntelliJ IDEA.
  User: anya ananasss
  Date: 24.03.2025
  Time: 23:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Баланс</title>
</head>
<body>
<div id="balanceContainer">
</div>
<button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>

<script>
    window.onload = function () {
        fetch('/get_balance')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                document.getElementById("balanceContainer").textContent = data.balance;
            })
            .catch(error => {
                document.getElementById("balanceContainer").textContent = "Ошибка загрузки";
            });
    };
</script>
</body>
</html>
