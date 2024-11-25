<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">
<section class="tasks">
    <h2>Ваши задачи</h2>

    <#if success??>
        <p class="success">${success}</p>
    </#if>
    <#if error??>
        <p class="error">${error}</p>
    </#if>

    <table>
        <tr>
            <th>Название задачи</th>
            <th>Дедлайн</th>
            <th>Статус</th>
        </tr>
        <#list tasks as task>
            <tr>
                <td>${task.title}</td>
                <td>${task.deadline}</td>

                <td>
                    <#if task.completed>
                        Завершено
                    <#else>
                        В процессе
                    </#if>
                </td>

            </tr>
        </#list>
    </table>
</section>
<#include "footer.ftl">
