<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Общий финансовый отчет</title>
</head>
<div>Введите даты начала и окончания отчета. Можете оставить поле пустым, если не хотите задавать границу:</div>
<div>
    <label> Дата начала:
        <input type="datetime-local" id="startDateForm" name="startDate" required>
    </label>
</div>

<div>
    <label> Дата окончания:
        <input type="datetime-local" id="endDateForm" name="endDate" required>
    </label>
</div>
<body>
<div id="basicStats"></div>
<div id="categoryReport"></div>
<div id="goalData"></div>

<button type="submit" id="sendDataButton" name="sendData">Сформировать отчет</button>

<button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>

<script>

    document.getElementById("sendDataButton").addEventListener("click", function (event) {
        event.preventDefault();
        const startTime = document.getElementById("startDateForm").value;
        const endTime = document.getElementById("endDateForm").value;
        const startDate = new Date(startTime);
        const endDate = new Date(endTime);

        if (startDate > endDate) {
            document.getElementById("container").innerHTML = "";
            document.getElementById("stateMessage").innerText = "Дата начала должна быть раньше даты окончания!";
        } else {
            sendJSON();
        }
    });


    function sendJSON() {
        const startTime = document.getElementById("startDateForm").value;
        const endTime = document.getElementById("endDateForm").value;


        const result = {};
        result["start"] = startTime
        result["end"] = endTime

        const jsonString = JSON.stringify(result);


        fetch('/general_report', {
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
                catchJSON(transactions);
            });
    }

    function catchJSON(data) {
        const basicStatsCont = document.getElementById("basicStats");
        const categoryReportCont = document.getElementById("categoryReport");
        const goalDataCont = document.getElementById("goalData");
        const stateMessage = document.getElementById("stateMessage");

        basicStatsCont.innerHTML = "";
        categoryReportCont.innerHTML = "";
        goalDataCont.innerHTML = "";
        stateMessage.textContent = "";

        const basicStats = data.basicStats;
        const categoryReport = data.categoryReport;
        const goalData = data.goalData;


        Object.entries(basicStats).forEach(([type, sum]) => {
            const ul = document.createElement("ul");
            const li = document.createElement("li");
            let formattedType = "";

            if (type === "totalIncome") {
                formattedType = "Ваш доход за период: ";
            } else if (type === "totalExpense") {
                formattedType = "Ваш расход за период: ";
            } else if (type === "totalBalance") {
                formattedType = "Ваш баланс за период: ";
            }

            li.textContent = formattedType + sum + " руб.";
            ul.appendChild(li);
            basicStatsCont.appendChild(ul);

            const hr = document.createElement("hr");
            basicStatsCont.appendChild(hr);
        });

        Object.entries(categoryReport).forEach(([category, values]) => {
            const ul = document.createElement("ul");
            const li = document.createElement("li");


            const normalizedCategory = category.toLowerCase().charAt(0).toUpperCase() + category.toLowerCase().slice(1);

            let content = normalizedCategory + ": ";
            const income = values[0];
            const expense = values[1];
            if (income !== 0) {
                content += "Доходы - " + income + " руб.; ";
            }
            if (expense !== 0) {
                content += "Расходы - " + expense + "руб.; ";
            }

            li.textContent = content;
            ul.appendChild(li);
            categoryReportCont.appendChild(ul);
        });


        Object.entries(goalData).forEach(([type, sum]) => {
            const ul = document.createElement("ul");
            const li = document.createElement("li");
            let formattedType = "";

            if (type === "goalSum") {
                formattedType = "Сумма цели: ";
            } else if (type === "goalIncome") {
                formattedType = "Доход по цели: ";
            } else if (type === "goalExpense") {
                formattedType = "Расход по цели: ";
            } else if (type === "saved") {
                formattedType = "Накопления: ";
            } else if (type === "left") {
                formattedType = "Осталось накопить: ";
            }

            li.textContent = formattedType + sum + " руб.";
            ul.appendChild(li);
            goalDataCont.appendChild(ul);
        });
    }


</script>
</body>
</html>
