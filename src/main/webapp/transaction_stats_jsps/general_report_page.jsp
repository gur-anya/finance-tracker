
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Общий финансовый отчет</title>
</head>

<body>
<div id="reportContainer">

</div>
<button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>

<script>
    window.onload = function () {
        fetch('/get_general_report')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                document.getElementById("reportContainer").textContent = data.report;
            })
            .catch(error => {
                document.getElementById("reportContainer").textContent = "Ошибка загрузки";
            });
    };
</script>
</body>
</html>
