<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>
       Расходы и доходы за период
    </title>
</head>
<body>
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
<button type="submit" id="sendDataButton"  name="sendData">Показать расходы и доходы за выбранный период</button>
<div id="container"></div>
<div id="stateMessage"></div>
<button onclick="window.location.href='/general_stats_page'" id="goBack">Назад</button>
<script>

    document.getElementById("sendDataButton").addEventListener("click", function (event) {
        event.preventDefault();
        const startTime = document.getElementById("startDateForm").value;
        const endTime = document.getElementById("endDateForm").value;
        const startDate = new Date(startTime);
        const endDate = new Date(endTime);


        if (!startTime || !endTime || isNaN(startDate) || isNaN(endDate)) {
            document.getElementById("container").innerHTML = "";
            document.getElementById("stateMessage").innerText = "Пожалуйста, введите обе даты!";
        } else if (startDate>endDate){
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
        result["start"] = startTime;
        result["end"] = endTime;

        const jsonString = JSON.stringify(result);
        fetch('/summary_income_expense', {
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
            })
            .catch(error => {
                document.getElementById("stateMessage").textContent = error.message;
                document.getElementById("container").innerHTML = "";
            });
    }

    function catchJSON(data) {
        const container = document.getElementById("container");
        const stateMessage = document.getElementById("stateMessage");
        container.innerHTML = "";
        stateMessage.textContent = "";

        Object.entries(data).forEach(([type, sum]) => {
            const ul = document.createElement("ul");
            const li = document.createElement("li");
            let formattedType = "";

            if (type === "income") {
                formattedType = "Ваш доход за период: "
            } else if (type === "expense") {
                formattedType = "Ваш расход за период: "
            } else {
                formattedType = "Ваш баланс за период: "
            }

            li.textContent = formattedType + sum + " руб.";
            ul.appendChild(li);
            container.appendChild(ul);

            const hr = document.createElement("hr");
            container.appendChild(hr);
        });
    }
</script>
</body>
</html>
