<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Вход</title>
</head>
<body>
<h1>
    Вход
</h1>
<form action="/login" method="POST" id="loginForm">
    <div>
        <label>Адрес электронной почты:
            <input type="email" name="email">
        </label>
    </div>
    <div>
        <label>Пароль:
            <input type="password" name="password">
        </label>
    </div>
    <div>
        <button type="submit" id="sendData">Войти</button>
        <button onclick="window.location.href='..'" id="goBack">Назад</button>
    </div>
    <div>
        <p id="stateMessage"></p>
    </div>
    <script>
        document.getElementById("sendData").addEventListener("click", function (event) {
            event.preventDefault();
            sendJSON();
        });

        function sendJSON() {
            const formDataArr = new FormData(document.getElementById("loginForm"));
            const result = {};


            formDataArr.forEach((value, key) => {
                result[key] = value;
            });


            const jsonString = JSON.stringify(result);


            fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                },
                body: jsonString
            }).then(response => {
                if (response.ok) {
                    window.location.href = '/main_user_page';
                } else {
                    return response.json().then(data => {
                        document.getElementById("stateMessage").innerText = data.message;
                    });
                }
            });
        }

        document.getElementById("goBack").addEventListener("click", function (event) {
            event.preventDefault();
        });
    </script>
</form>
</body>
</html>
