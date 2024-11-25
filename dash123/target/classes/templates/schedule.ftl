<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">
<section class="schedule">
    <h2>Ваше расписание</h2>

    <#if success??>
        <p class="success">${success}</p>
    </#if>
    <#if error??>
        <p class="error">${error}</p>
    </#if>

    <form action="/schedule" method="post">
        <table>
            <tr>
                <th>День недели</th>
                <th>Часы</th>
            </tr>
            <#list ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"] as day>
                <tr>
                    <td>${day}</td>
                    <td>
                        <#assign hours = 0>
                        <#list schedules as schedule>
                            <#if schedule.dayOfWeek == day>
                                <#assign hours = schedule.hours>
                            </#if>
                        </#list>
                        <input type="number" step="0.5" name="${day?lowerCase}Hours" min="0" max="24"
                               value="${hours}">
                    </td>
                </tr>
            </#list>
        </table>
        <button type="submit">Сохранить расписание</button>
    </form>
</section>
<#include "footer.ftl">
