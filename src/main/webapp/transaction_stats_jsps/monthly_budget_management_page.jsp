<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Управление месячным бюджетом</title>
</head>
<body>
<div id = "budgetContainer"></div>
<button onclick="window.location.href='/check_budget'" id="checkGoal">Проверить остаток бюджета на месяц</button>
<button onclick="window.location.href='/update_budget'" id="changeGoal">Изменить месячный бюджет</button>
<button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>
<script>
  window.onload = function () {
    fetch('/get_monthly_budget_management')
            .then(response => {
              if (!response.ok) {
                throw new Error('Ошибка при загрузке транзакций');
              }
              return response.json();
            })
            .then(data => {
              const budget = data.param;
              if (budget === 0.0){
                document.getElementById("budgetContainer").textContent = "Вы еще не установили месячный бюджет!";
              } else {
                document.getElementById("budgetContainer").textContent = "Установленный месячный бюджет: " + budget + " руб.";
              }
            })
            .catch(error => {
              document.getElementById("goalContainer").textContent = "Ошибка загрузки";
            });
  };
</script>
</body>
</html>
