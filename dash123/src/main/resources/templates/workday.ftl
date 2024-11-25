<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Рабочий день</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">

<section class="workday">
    <h2>Рабочий день</h2>

    <#if success??>
        <p class="success">${success}</p>
    </#if>
    <#if error??>
        <p class="error">${error}</p>
    </#if>

    <#if workdayStartTime??>
        <p>Рабочий день активен. Начало: ${workdayStartTime?string("yyyy-MM-dd HH:mm:ss")}</p>

        <!-- Кнопка для остановки рабочего дня -->
        <form action="/workday/pause" method="post">
            <button type="submit">Поставить на паузу</button>
        </form>
        <form action="/workday/stop" method="post">
            <button type="submit">Завершить рабочий день</button>
        </form>
    <#else>
        <p>Рабочий день не начат.</p>

        <!-- Кнопка для начала рабочего дня -->
        <form action="/workday/start" method="post">
            <button type="submit">Начать рабочий день</button>
        </form>
    </#if>
</section>

<#include "footer.ftl">
