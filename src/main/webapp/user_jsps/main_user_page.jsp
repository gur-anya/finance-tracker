<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Здравствуйте!</title>
</head>
<body>
<p>
    Здравствуйте, <c:out value="${username}"/>! Выберите, что хотите сделать:
</p>
<div>
<button onclick="window.location.href='/personal_account'" id = "personalAccount">Перейти в личный кабинет</button>
</div>
<div>
<button onclick="window.location.href='/main_transaction_page'" id = "finances">Перейти к управлению финансами</button>
</div>
<div>
<button onclick="window.location.href='/logout'" id = "logout">Выйти</button>
</div>
</body>
</html>
