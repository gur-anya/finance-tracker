<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Проверить результат по цели</title>
</head>
<body>
<div id="stateContainer"></div>
<button onclick="window.location.href='/goal_management'" id="goBack">Назад</button>
<script>
    window.onload = function () {
        fetch('/get_check_goal')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                const goalState = data.state;
                const goal = data.param;
                if (goal === 0){
                    document.getElementById("stateContainer").textContent = "Вы еще не установили цель!";
                } else {
                    if (goalState === 0) {
                        document.getElementById("stateContainer").textContent = "Поздравляем! Вы достигли цели! Может, пора поставить новую? ;)";
                    } else if (goalState > 0){
                        document.getElementById("stateContainer").textContent = "До цели осталось накопить " + goalState + " руб. Отличный результат!";
                    } else {
                        document.getElementById("stateContainer").textContent = "Вы превысили цель на " + goalState + " руб.! Может, пора поставить новую цель? ;)";
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