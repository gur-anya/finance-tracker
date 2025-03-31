<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Обновить транзакцию</title>
</head>
<body>

<p>Выберите транзакцию, которую хотите обновить</p>
<div id="transactionList"></div>
<form>
    <label> Номер транзакции, которую обновить
        <input type="number" oninput="transNumChanged()" id="transNumberInp" name="transNumber" min="1" step="1" required>
    </label>
</form>

<form id="updateForm">
    <div>
        <label> Тип:
            <select id="formType" name="type">
                <option value="1">Доход</option>
                <option value="2">Расход</option>
            </select>
            <input type="hidden" id="originalType" name="originalType">
        </label>
    </div>
    <div>
        <label> Сумма:
            <input type="number" id="formSum" name="sum" min="0" step="0.01">
            <input type="hidden" id="originalSum" name="originalSum">
        </label>
    </div>
    <div>
        <label> Новая категория:
            <input type="text" id="formCategory" name="category">
            <input type="hidden" id="originalCategory" name="originalName">
        </label>
    </div>
    <div>
        <label> Новое описание:
            <input id="formDescription" name="description">
            <input type="hidden" id="originalDescription" name="originalDescription">
        </label>
    </div>

</form>
<div>
    <button onclick="window.location.href='/transactions_management_page'" id="goBack">Назад</button>
</div>
<div>
    <button id="doUpdate">Обновить транзакцию под выбранным номером</button>
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
        return year + "-" + month + "-" + day + " " + hours + ":" + minutes;
    }

    window.onload = function () {
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


            li.textContent = index + ". " + typeText + ": " + sum + ", " + category + ", " + description + ", " + timestamp;
            ul.appendChild(li);
            index = index + 1;
            container.appendChild(ul);

            const hr = document.createElement("hr");
            container.appendChild(hr);
        });

        if (numberInput.value !== "" && !isNaN(parseInt(numberInput.value))) {
            transNumChanged();
        }
    }
    
    
    function transNumChanged(){
        const input = document.getElementById("transNumberInp");
        const deleteIndex = parseInt(input.value) - 1;

        const toChange = transactions[deleteIndex];


        document.getElementById("formType").value = toChange.type;
        document.getElementById("originalType").value = toChange.type;

        document.getElementById("formSum").value = toChange.sum;
        document.getElementById("originalSum").value = toChange.sum;

        document.getElementById("formCategory").value = toChange.category;
        document.getElementById("originalCategory").value = toChange.category;

        document.getElementById("formDescription").value = toChange.description;
        document.getElementById("originalDescription").value = toChange.description;
    }

    function sendJSON() {
        const input = document.getElementById("transNumberInp");
        const result = {};

        const deleteIndex = parseInt(input.value) - 1;

        const toUpdate = transactions[deleteIndex];

        result["type"] = document.getElementById("formType").value;
        result["sum"] =   document.getElementById("formSum").value;
        result["category"] =  document.getElementById("formCategory").value;
        result["description"] = document.getElementById("formDescription").value

        result["originalType"] = document.getElementById("originalType").value;
        result["originalSum"] =  document.getElementById("originalSum").value;
        result["originalCategory"] = document.getElementById("originalCategory").value;
        result["originalDescription"] = document.getElementById("originalDescription").value;
        result["originalTimestamp"] = toUpdate.timestamp;


        
        let updatedValues = "";
        if( result["type"] !== result["originalType"]){
            updatedValues += "type";
        }
        if( result["sum"] !==  result["originalSum"] ){
            updatedValues += "sum";
        }
        if( result["category"] !==  result["originalCategory"]){
            updatedValues += "category";
        }
        if( result["description"] !==  result["originalDescription"]){
            updatedValues += "description";
        }

        result["updatedValues"] = updatedValues;

            const jsonString = JSON.stringify(result);

        fetch('/update_transaction', {
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

    document.getElementById("goBack").addEventListener("click", function (event) {
        event.preventDefault();
    })
    document.getElementById("doUpdate").addEventListener("click", function (event) {
        event.preventDefault();
        sendJSON();
    });
</script>
</body>
</html>