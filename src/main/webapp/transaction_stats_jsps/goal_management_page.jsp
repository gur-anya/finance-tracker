<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Управление целью</title>
</head>
<body>
<div id = "goalContainer"></div>
<button onclick="window.location.href='/check_goal'" id="checkGoal">Проверить прогресс по цели</button>
<button onclick="window.location.href='/update_goal'" id="changeGoal">Изменить цель</button>
<button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>
<script>
    window.onload = function () {
        fetch('/get_goal_management')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                const goal = data.param;
                if (goal === 0.0){
                    document.getElementById("goalContainer").textContent = "Вы еще не установили цель. Сделайте это прямо сейчас!";
                } else {
                    document.getElementById("goalContainer").textContent = "Установленная цель: " + goal + " руб.";
                }
            })
            .catch(error => {
                document.getElementById("goalContainer").textContent = "Ошибка загрузки";
            });
    };
</script>
</body>
</html>
