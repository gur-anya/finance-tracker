<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Просмотр транзакций</title>
</head>
<body>
<p>
    Выберите фильтр для транзакций.
</p>
<form>
    <div>
        <label> До даты:
            <input type="datetime-local" id="beforeDateForm" name="beforeDate" >
        </label>
        <button type="submit" id="beforeDateButton" name="filter" value="1">Отфильтровать до даты</button>
    </div>

    <div>
        <label> После даты:
            <input type="datetime-local" id="afterDateForm" name="afterDate">
        </label>
        <button type="submit" id="afterDateButton"  name="filter" value="2">Отфильтровать после даты</button>
    </div>
    <div>
        <label> По категории:
            <input type="text" id="categoryForm" name="category">
        </label>
        <button type="submit" id="categoryButton" name="filter" value="3">Отфильтровать по категории</button>
    </div>
    <div>
        <label> По типу:

            <button type="submit"  name="filter" value="41" id="typeIncomeButton">Доходы</button>
            <input type="hidden" id="incomesForm" name="type" value="1"/>
            <button type="submit" name="filter"  value="42" id="typeExpenseButton">Расходы</button>
            <input type="hidden" id="expensesForm" name="type" value="2"/>
        </label>
    </div>
    <div>
        <label> Все, без фильтра:
            <button type="submit" name="filter" value="5" id="allButton">Показать все</button>
            <input type="hidden" id="getAllForm" name="getAll" value=""/>
        </label>
    </div>
</form>
<button onclick="window.location.href='/transactions_management_page'" id="goBack">Назад</button>
<div id="transactionList"></div>
<p id="stateMessage"></p>
<script>
    function formatTimestamp(timestamp) {
        const date = new Date(timestamp);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return year+"-"+month+"-"+day+" "+hours+":"+minutes;
    }
    document.querySelector("form").addEventListener("submit", function (event) {
        event.preventDefault();
    });

    document.getElementById("beforeDateButton").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON("beforeDateForm", "beforeDateButton");
    });
    document.getElementById("afterDateButton").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON("afterDateForm", "afterDateButton");
    });
    document.getElementById("categoryButton").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON("categoryForm", "categoryButton");
    });
    document.getElementById("typeIncomeButton").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON("incomesForm", "typeIncomeButton");
    });
    document.getElementById("typeExpenseButton").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON("expensesForm", "typeExpenseButton");
    });
    document.getElementById("allButton").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON("getAllForm","allButton");
    });

    function sendJSON(inputId, buttonId) {
        const input = document.getElementById(inputId);
        const button = document.getElementById(buttonId);
        const result = {};
        result[input.name] = input.value;
        result["filter"] = button.value;

        const jsonString = JSON.stringify(result);

        fetch('/show_transactions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            body: jsonString
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || 'Ошибка сервера');
                    });
                }
                return response.json();
            })
            .then(transactions => {
                catchJson(transactions);
            })
            .catch(error => {
                document.getElementById("stateMessage").textContent = error.message;
                document.getElementById("transactionList").innerHTML = "";
            });
    }
    document.getElementById("goBack").addEventListener("click", function (event){
        event.preventDefault();
    })


    function catchJson(data) {
        const container = document.getElementById("transactionList");
        const stateMessage = document.getElementById("stateMessage");
        container.innerHTML = "";
        stateMessage.textContent = "";

        if (data.transactions) {
            const transactions = data.transactions;
            if (transactions.length === 0) {
                stateMessage.textContent = "Транзакции не найдены";
            } else {
                let index = 1;
                transactions.forEach((transaction) => {
                    const ul = document.createElement("ul");
                    const li = document.createElement("li");
                    const typeText = transaction.type === 1 ? "Доход" : "Расход";
                    const sum = transaction.sum;
                    const category = transaction.category;
                    const description = transaction.description;
                    const timestamp = formatTimestamp(transaction.timestamp);
                    li.textContent = index+". " + typeText +": " + sum +", " + category +", " + description+", "+timestamp;
                    ul.appendChild(li);
                    container.appendChild(ul);
                    container.appendChild(document.createElement("hr"));
                    index++;
                });
            }
        } else if (data.message) {
            stateMessage.textContent = data.message;
        } else {
            stateMessage.textContent = "Ошибка: неверный формат ответа от сервера";
        }
    }
</script>
</body>
</html>

