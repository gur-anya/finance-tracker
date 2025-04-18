<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Анализ расходов по категориям</title>
</head>
<body>
<div id="expensesList"></div>
<div>
    <button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>
</div>
<script>


    window.onload = function() {
        fetch('/get_expenses_by_category')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                fillTable(data);
            })
            .catch(error => {
                document.getElementById("transactionList").textContent = "Ошибка загрузки статистики";
            });
    };

    function fillTable(data) {
        const container = document.getElementById("expensesList");
        container.innerHTML = "";

        if (Object.keys(data).length === 0) {
            container.textContent = "Расходы не найдены";
            return;
        }

        let index = 1;

        Object.entries(data).forEach(([category, sum]) => {
            const ul = document.createElement("ul");
            const li = document.createElement("li");
            const formattedCategory = category.toLowerCase().charAt(0).toUpperCase() + category.toLowerCase().slice(1);

            li.textContent = index +". " + formattedCategory +": " + sum + " руб.";
            ul.appendChild(li);
            container.appendChild(ul);

            const hr = document.createElement("hr");
            container.appendChild(hr);

            index++;
        });
    }
</script>
</body>
</html>
