<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Личный кабинет</title>
</head>
<body>
<p>
    <c:out value="${username}"/>, какую статистику хотите просмотреть?
</p>
<div>
    <button onclick="window.location.href='/monthly_budget'" id="monthlyBudget">Месячный бюджет</button>
</div>
<div>
    <button onclick="window.location.href='/goal'" id="goal">Цель</button>
</div>
<div>
    <button onclick="window.location.href='/current_balance'" id="currentBalance">Текущий баланс с учетом всех транзакций</button>
</div>
<div>
    <button onclick="window.location.href='/summary_income_expense'" id="summaryIncomeExpense">Суммарный доход и расход за период</button>
</div>
<div>
    <button onclick="window.location.href='/summary_expenses_by_categories'" id="expensesByCategories">Общие расходы по категориям</button>
</div>
<div>
    <button onclick="window.location.href='/general_report'" id="generalReport">Отчет по финансовому состоянию</button>
</div>
<div>
    <button onclick="window.location.href='/main_transaction_page'" id="goBack">Назад</button>
</div>
</body>
</html>
