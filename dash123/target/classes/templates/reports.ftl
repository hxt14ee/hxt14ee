<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отчёты</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">

<section class="reports">
    <h2>Отчёты</h2>

    <#if success??>
        <p class="success">${success}</p>
    </#if>
    <#if error??>
        <p class="error">${error}</p>
    </#if>

    <!-- Блок заявок -->
    <section class="requests">
        <h3>Заявки на отпуск</h3>

        <#if requests?? && (requests?size > 0)>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Пользователь</th>
                    <th>Тип</th>
                    <th>Описание</th>
                    <th>Детали</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                <#list requests as request>
                    <tr>
                        <td>${request.id!}</td>
                        <td>${request.username!"Неизвестный пользователь"}</td>
                        <td>${request.type!"Не указан"}</td>
                        <td>${request.description!"Нет описания"}</td>
                        <td>${request.details!"Нет деталей"}</td>
                        <td>
                            <form method="post" action="/requests/${request.id}/approve">
                                <button type="submit">Одобрить</button>
                            </form>
                            <form method="post" action="/requests/${request.id}/reject">
                                <button type="submit">Отклонить</button>
                            </form>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        <#else>
            <p>Нет заявок на отпуск.</p>
        </#if>
    </section>

    <!-- Блок отчётов -->
    <section class="user-reports">
        <h3>Отчёты пользователей</h3>

        <#if reports?? && (reports?size > 0)>
            <table>
                <thead>
                <tr>
                    <th>Имя пользователя</th>
                    <th>Общее рабочее время</th>
                    <th>Количество выполненных задач</th>
                    <th>Пропущенные дедлайны</th>
                </tr>
                </thead>
                <tbody>
                <#list reports as report>
                    <tr>
                        <td>${report.username!"Неизвестно"}</td>
                        <td>${report.totalWorkHours!"0"} ч.</td>
                        <td>${report.completedTasks!"0"}</td>
                        <td>${report.missedDeadlines!"0"}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        <#else>
            <p>Нет доступных отчётов.</p>
        </#if>
    </section>
</section>

<#include "footer.ftl">
