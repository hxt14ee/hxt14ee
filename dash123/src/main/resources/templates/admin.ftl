<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">
<section class="admin">
    <h2>Админ Панель</h2>

    <#if success??>
        <p class="success">${success}</p>
    </#if>
    <#if error??>
        <p class="error">${error}</p>
    </#if>

    <!-- Форма для добавления новой задачи -->
    <!-- Форма для добавления новой задачи -->
    <h3>Добавить новую задачу</h3>
    <form action="/admin/createTask" method="post"> <!-- Убедитесь, что action соответствует маршруту -->
        <label for="title">Название задачи:</label>
        <input type="text" id="title" name="title" required>

        <label for="startDate">Дата начала:</label>
        <input type="datetime-local" id="startDate" name="startDate" required>

        <label for="deadline">Дедлайн:</label>
        <input type="datetime-local" id="deadline" name="deadline" required>

        <label for="assignedTo">Назначить пользователю:</label>
        <select id="assignedTo" name="assignedTo" required>
            <option value="" disabled selected>Выберите пользователя</option>
            <#list users as user>
                <option value="${user.id}">${user.username}</option>
            </#list>
        </select>

        <button type="submit">Создать задачу</button>
    </form>


    <!-- Список задач -->
    <h3>Список задач</h3>
    <table>
        <tr>
            <th>Название задачи</th>
            <th>Дата начала</th>
            <th>Дедлайн</th>
            <th>Назначено пользователю</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
        <#list tasks as task>
            <tr>
                <form action="/admin/updateTask" method="post">
                    <input type="hidden" name="id" value="${task.id}">

                    <td>
                        <input type="text" name="title" value="${task.title}" required>
                    </td>
                    <td>
                        <input type="datetime-local" name="startDate" value="${task.startDate}" readonly>
                    </td>
                    <td>
                        <input type="datetime-local" name="deadline" value="${task.deadline}" required>
                    </td>

                    <td>
                        <select name="assignedTo" required>
                            <option value="" disabled>Выберите <пользовате></пользовате>ля</option>
                            <#list users as user>
                                <option value="${user.id}" <#if user.id == task.assignedToUserId>selected</#if>>${user.username}</option>
                            </#list>
                        </select>
                    </td>
                    <td>
                        <select name="isCompleted">
                            <option value="false" <#if !task.completed>selected</#if>>В процессе</option>
                            <option value="true" <#if task.completed>selected</#if>>Завершено</option>
                        </select>
                    </td>
                    <td>
                        <button type="submit">Обновить</button>
                    </td>
                </form>
            </tr>
        </#list>
    </table>

</section>
<#include "footer.ftl">
