<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Удалить аккаунт</title>
</head>
<body>
<p>Вы действительно хотите удалить аккаунт? Это действие невозможно отменить!</p>
<div>
    <button onclick="deleteAccount()">Да, удалить аккаунт</button>
</div>
<div>
    <button onclick="window.location.href='/personal_account'" id="goBack">Нет, вернуться назад</button>
</div>



<script>
    function deleteAccount() {
        fetch('/delete_account', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/';
                }
            })
            .catch(error => {
                console.error('Ошибка запроса:', error);
            });
    }
</script>
</body>
</html>
