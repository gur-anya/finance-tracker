<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Регистрация</title>
</head>
<body>
<h1>
    Регистрация
</h1>
<form action="/registration" id="registrationForm" method="POST">
    <div>
        <label> Имя пользователя:
            <input type="text" id="formName" name="name">
        </label>
    </div>
    <div>
        <label> Адрес электронной почты:
            <input type="email" id="formEmail" name="email">
        </label>
    </div>
    <div>
        <label> Пароль:
            <input type="password" id="formPassword" name="password">
        </label>
        <input type="hidden" id="formRole" name="role" value="1"/>
    </div>
    <div>
        <label> Повторите пароль:
            <input type="password" id="formRepeatPassword" name="repeatedPassword">
        </label>
    </div>

    <button type="submit" id="sendData">Зарегистрироваться</button>
    <button onclick="window.location.href='..'" id="goBack">Назад</button>
</form>
<div>
    <p id="stateMessage"></p>
</div>

<script>
    document.getElementById("sendData").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON();
    });

    function sendJSON() {
        const formDataArr = new FormData(document.getElementById("registrationForm"));
        const result = {};


        formDataArr.forEach((value, key) => {
            result[key] = value;
        });


        const jsonString = JSON.stringify(result);

        fetch('/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            body: jsonString
        }).then(response => {
            return response.json();
        }).then(data => {
            document.getElementById("stateMessage").innerText = data.message;
        })
    }

    document.getElementById("goBack").addEventListener("click", function (event){
        event.preventDefault();
    })
</script>
</body>
</html>
