<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Удалить транзакцию</title>
</head>
<body>

<p>Выберите транзакцию, которую хотите удалить. Это действие невозможно отменить!</p>
<div id="transactionList"></div>
<form>
    <label> Номер транзакции, которую нужно удалить:
        <input type="number" id="transNumberInp" name="transNumber" min="1" step="1">
    </label>
</form>
<div>
    <button onclick="window.location.href='/transactions_management_page'" id="goBack">Назад</button>
</div>
<div>
    <button id="doDelete">Удалить транзакцию под выбранным номером</button>
</div>
<div>
    <p id="stateMessage"></p>
</div>
<script>

    let transactions;

    function formatTimestamp(timestamp) {
        const date = new Date(timestamp);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return year+"-"+month+"-"+day+" "+hours+":"+minutes;
    }

    window.onload = function() {
        fetch('/get_all')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке транзакций');
                }
                return response.json();
            })
            .then(data => {
                fillTable(data);
            })
            .catch(error => {
                document.getElementById("transactionList").textContent = "Ошибка загрузки транзакций";
            });
    };
    function fillTable(data) {
        const container = document.getElementById("transactionList");
        const numberInput = document.getElementById("transNumberInp");
        container.innerHTML = "";

        transactions = data.transactions;



        if (transactions.length === 0) {
            container.textContent = "Транзакции не найдены";
            return;
        } else {
            numberInput.max = transactions.length;
        }

        let index = 1;

        transactions.forEach((transaction) => {

            const ul = document.createElement("ul");
            const li = document.createElement("li");


            const typeText = transaction.type === 1 ? "Доход" : "Расход";
            const sum = transaction.sum;
            const category = transaction.category;
            const description = transaction.description;
            const timestamp = formatTimestamp(transaction.timestamp);


            li.textContent = index+". " + typeText+": " + sum +", " + category +", " + description+", "+timestamp;
            ul.appendChild(li);
            index = index + 1;
            container.appendChild(ul);

            const hr = document.createElement("hr");
            container.appendChild(hr);
        });
    }

    function sendJSON(){
        const input = document.getElementById("transNumberInp");
        const result = {};

        const deleteIndex = parseInt(input.value) - 1;

        const toDelete = transactions[deleteIndex];

        result["type"] = toDelete.type;
        result["sum"] = toDelete.sum;
        result["category"] = toDelete.category;
        result["description"] = toDelete.description;
        result["timestamp"] = toDelete.timestamp;
        const jsonString = JSON.stringify(result);

        fetch('/delete_transaction', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            body: jsonString
        }).then(response => {
            return response.json();
        }).then(data => {
            document.getElementById("stateMessage").innerText = data.message;
            fetch('/get_all')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Ошибка при загрузке транзакций');
                    }
                    return response.json();
                })
                .then(data => {
                    fillTable(data);
                })
                .catch(error => {
                    document.getElementById("transactionList").textContent = "Ошибка загрузки транзакций";
                });
        });
    }
    document.getElementById("goBack").addEventListener("click", function (event){
        event.preventDefault();
    })
    document.getElementById("doDelete").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON();
    });
</script>
</body>
</html>
