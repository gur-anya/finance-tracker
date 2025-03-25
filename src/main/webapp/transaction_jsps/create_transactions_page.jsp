
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Создать транзакцию</title>
</head>
<body>
<h1>
    Создание транзакции
</h1>
<form action="/create_transaction" id="transCreationForm" method="POST">
    <div>
        <label> Тип:
            <select id="formType" name="type">
                <option value="1">Доход</option>
                <option value="2">Расход</option>
            </select>
        </label>
    </div>
    <div>
        <label> Сумма:
            <input type="number" id="formSum" name="sum" min="0" step="0.01">
        </label>
    </div>
    <div>
        <label> Категория:
            <input type="text" id="formCategory" name="category">
        </label>
    </div>
    <div>
        <label> Описание:
            <textarea id="formDescription" name="description"></textarea>
        </label>
    </div>

    <button type="submit" id="sendData">Создать транзакцию</button>
    <button type="button" onclick="window.location.href='/transactions_management_page'" id="goBack">Назад</button>
</form>
<div>
    <p id="stateMessage"></p>
</div>

<script>
    document.querySelector("form").addEventListener("submit", function (event) {
        event.preventDefault();
    });

    document.getElementById("sendData").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON();
    });
    function sendJSON() {
        const formDataArr = new FormData(document.getElementById("transCreationForm"));
        const result = {};


        formDataArr.forEach((value, key) => {
            result[key] = value;
        });

        const jsonString = JSON.stringify(result);
        fetch('/create_transaction', {
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
