<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Изменить цель</title>
</head>
<body>
<div>На этой странице вы можете изменить месячный бюджет.</div>
<div>
    <form>
        <label> Новый бюджет:
            <input type="number" id="formSum" name="sum" min="0" step="0.01">
        </label>
    </form>
</div>
<div id = "stateMessage"></div>
<button type="submit" id="sendData">Обновить месячный бюджет</button>
<button onclick="window.location.href='/monthly_budget_management'" id="goBack">Назад</button>

<script>
    document.querySelector("form").addEventListener("submit", function (event) {
        event.preventDefault();
    });

    document.getElementById("sendData").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON();
    });

    function sendJSON() {
        const newSumForm = document.getElementById("formSum");

        const result = {};

        result["newValue"] = newSumForm.value;

        const jsonString = JSON.stringify(result);
        fetch('/update_budget', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            body: jsonString
        }).then(response => {
            return response.json();
        }).then(data => {
            document.getElementById("stateMessage").innerText = data.message;
        });
    }
</script>
</body>
</html>
