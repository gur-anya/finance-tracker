<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Изменить личную информацию</title>
</head>
<body>
<form id="updateForm">
    <div>
        <label> Имя пользователя:
            <input type="text" id="formName" name="name"
                   oninput="nameChanged()" value=${username}>
            <input type="hidden" id="originalName" name="originalName" value=${username}>
        </label>
    </div>
    <div>
        <label> Адрес электронной почты:
            <input type="email" id="formEmail" name="email"
                   oninput="emailChanged()" value=${useremail}>
            <input type="hidden" id="originalEmail" name="originalEmail" value=${useremail}>
        </label>
    </div>
    <div>
        <label> Новый пароль:
            <input type="password" id="formNewPassword"
                   oninput="passwordChanged()" name="password">
        </label>
    </div>
    <div style="display: none" id="oldPasswordDiv">
        <label> Старый пароль:
            <input type="password" id="formOldPassword" name="oldPassword">
        </label>
    </div>
    <div style="display: none" id="newPasswordRepeatDiv">
        <label> Повторите новый пароль:
            <input type="password" id="formRepeatPassword" name="newPassword">
        </label>
    </div>
    <button type="submit" id="sendData">Изменить данные</button>
    <button onclick="window.location.href='/personal_account'" id="goBack">Назад</button>
</form>
<div>
    <p id="stateMessage"></p>
</div>
<script>
    document.getElementById("sendData").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON();
    });

    let isNameChanged = false;
    let isEmailChanged = false;
    let isPasswordChanged = false;

    function sendJSON() {
        document.getElementById("stateMessage").innerText = ""

        if (isPasswordChanged && !(document.getElementById("formNewPassword").value ===
            document.getElementById("formRepeatPassword").value)) {
            document.getElementById("stateMessage").innerText = "Повторенный пароль не совпадает с новым! Пожалуйста, повторите попытку!"
        } else {
            let result = {};


            result["name"] = document.getElementById("formName").value;
            result["email"] = document.getElementById("formEmail").value;
            if (isPasswordChanged) {
                result ["password"] = document.getElementById("formNewPassword").value;
            }
            result['updatedValues'] = "";

            if (isNameChanged) {
                result['updatedValues'] = result['updatedValues'] + "name";
            }
            if (isEmailChanged) {
                result['updatedValues'] = result['updatedValues'] + "email";
            }
            if (isPasswordChanged) {
                result['updatedValues'] = result['updatedValues'] + "password";
            }
            const jsonString = JSON.stringify(result);

            fetch('/update_account', {
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
    }

    function passwordChanged() {
        const newPasswordForm = document.getElementById("formNewPassword");
        const oldPasswordDiv = document.getElementById("oldPasswordDiv");
        const newPasswordRepeatDiv = document.getElementById("newPasswordRepeatDiv");
        if (newPasswordForm.value.length > 0) {
            oldPasswordDiv.style.display = 'block';
            newPasswordRepeatDiv.style.display = 'block';
            isPasswordChanged = true;
        } else {
            oldPasswordDiv.style.display = 'none';
            newPasswordRepeatDiv.style.display = 'none';
            isPasswordChanged = false;
        }
    }

    function nameChanged() {
        const newNameForm = document.getElementById("formName");
        const oldNameForm = document.getElementById("originalName");
        isNameChanged = !(newNameForm.value === oldNameForm.value);
    }

    function emailChanged() {
        const newEmailForm = document.getElementById("formEmail");
        const oldEmailForm = document.getElementById("originalEmail");

        isEmailChanged = !(newEmailForm.value === oldEmailForm.value);
    }

    document.getElementById("goBack").addEventListener("click", function (event) {
        event.preventDefault();
    })
</script>
</body>
</html>
