<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">

<section class="requests">
    <h2>Заявки на отпуск</h2>

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
                    <td>${request.id}</td>
                    <td>${request.username}</td>
                    <td>${request.type}</td>
                    <td>${request.description}</td>
                    <td>${request.details}</td>
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

<#include "footer.ftl">
