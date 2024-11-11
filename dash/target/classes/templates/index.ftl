<#include "header.ftl">
<section id="shift-info">
    <h2>Текущая смена</h2>
    <p id="shift-status">Смена не активна</p>
    <p id="time-passed">Прошло: 00:00:00</p>
    <p id="time-remaining">Осталось: 00:00:00</p>
    <p id="pause-remaining"></p>

    <button onclick="startShift()">Начать смену</button>
    <button onclick="endShift()">Завершить смену</button>
    <button onclick="pauseShift()">Пауза</button>
</section>
<#include "footer.ftl">
