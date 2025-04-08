<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Проверить месячный бюджет</title>
</head>
<body>
<div id="stateContainer"></div>
<button onclick="window.location.href='/monthly_budget_management'" id="goBack">Назад</button>
<script>
    window.onload = function () {
        fetch('/get_check_budget')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                const budgetState = data.state;
                const monthlyBudget = data.param;
                if (monthlyBudget === 0){
                    document.getElementById("stateContainer").textContent = "Вы еще не установили месячный бюджет!";
                } else {
                    if (budgetState === 0) {
                        document.getElementById("stateContainer").textContent = "Внимание! Ваш остаток на месяц - 0 руб.";
                    } else if (budgetState > 0){
                        document.getElementById("stateContainer").textContent = "Ваш остаток на месяц - " + budgetState + " руб.";
                    } else {
                        document.getElementById("stateContainer").textContent = "Вы превысили месячный бюджет на " +  budgetState + " руб.!";
                    }
                }
            })
            .catch(error => {
                document.getElementById("stateContainer").textContent = "Ошибка загрузки";
            });
    };
</script>
</body>
</html>