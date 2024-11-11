<#include "header.ftl">
<main class="requests-container">
    <section id="leave-request-section">
        <h2>Запросы на отпуск</h2>
        <form id="leave-request-form" action="/requests/leave" method="post">
            <label for="leave-start">Начало отпуска:</label>
            <input type="date" id="leave-start" name="leaveStart" required>

            <label for="leave-end">Окончание отпуска:</label>
            <input type="date" id="leave-end" name="leaveEnd" required>

            <button type="submit">Отправить запрос</button>
        </form>
    </section>

    <section id="schedule-change-section">
        <h2>Запросы на изменение графика</h2>
        <form id="schedule-change-form" action="/requests/schedule" method="post">
            <div class="day-container">
                <label for="monday-start">Понедельник:</label>
                <input type="time" id="monday-start" name="mondayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="monday-end" name="mondayEnd" onchange="calculateTotalHours()">
            </div>
            <div class="day-container">
                <label for="tuesday-start">Вторник:</label>
                <input type="time" id="tuesday-start" name="tuesdayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="tuesday-end" name="tuesdayEnd" onchange="calculateTotalHours()">
            </div>
            <div class="day-container">
                <label for="wednesday-start">Среда:</label>
                <input type="time" id="wednesday-start" name="wednesdayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="wednesday-end" name="wednesdayEnd" onchange="calculateTotalHours()">
            </div>
            <div class="day-container">
                <label for="thursday-start">Четверг:</label>
                <input type="time" id="thursday-start" name="thursdayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="thursday-end" name="thursdayEnd" onchange="calculateTotalHours()">
            </div>
            <div class="day-container">
                <label for="friday-start">Пятница:</label>
                <input type="time" id="friday-start" name="fridayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="friday-end" name="fridayEnd" onchange="calculateTotalHours()">
            </div>
            <div class="day-container">
                <label for="saturday-start">Суббота:</label>
                <input type="time" id="saturday-start" name="saturdayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="saturday-end" name="saturdayEnd" onchange="calculateTotalHours()">
            </div>
            <div class="day-container">
                <label for="sunday-start">Воскресенье:</label>
                <input type="time" id="sunday-start" name="sundayStart" onchange="calculateTotalHours()">
                <span>до</span>
                <input type="time" id="sunday-end" name="sundayEnd" onchange="calculateTotalHours()">
            </div>

            <p id="total-hours">Общее количество часов: 0 / 40</p>
            <button type="submit" id="submit-button" disabled>Отправить запрос</button>
        </form>
    </section>
</main>

<script>
    function calculateTotalHours() {
        let totalHours = 0;
        const days = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];

        days.forEach(day => {
            const start = document.getElementById(`${day}-start`).value;
            const end = document.getElementById(`${day}-end`).value;

            if (start && end) {
                const startDate = new Date(`1970-01-01T${start}:00`);
                const endDate = new Date(`1970-01-01T${end}:00`);
                const hoursWorked = (endDate - startDate) / (1000 * 60 * 60); // Convert milliseconds to hours
                totalHours += hoursWorked;
            }
        });

        document.getElementById('total-hours').textContent = `Общее количество часов: ${totalHours} / 40`;

        const submitButton = document.getElementById('submit-button');
        if (totalHours === 40) {
            submitButton.disabled = false;
            document.getElementById('total-hours').style.color = 'green';
        } else {
            submitButton.disabled = true;
            document.getElementById('total-hours').style.color = 'red';
        }
    }
</script>
<#include "footer.ftl">
