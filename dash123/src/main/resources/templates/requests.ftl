<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">

<section class="requests">
    <h2>Запрос на отпуск</h2>

    <#-- Сообщение об успехе или ошибке -->
    <#if successMessage??>
        <p class="success">${successMessage}</p>
    </#if>
    <#if errorMessage??>
        <p class="error">${errorMessage}</p>
    </#if>

    <form method="POST" action="/requests">
        <label for="type">Тип отпуска:</label>
        <select id="type" name="type" required>
            <option value="paid">Оплачиваемый отпуск</option>
            <option value="unpaid">Неоплачиваемый отпуск</option>
            <option value="sick">Больничный</option>
        </select>

        <label for="description">Описание:</label>
        <textarea id="description" name="description" rows="4" required></textarea>

        <label for="start">Дата начала:</label>
        <input type="date" id="start" name="start" required>

        <label for="end">Дата окончания:</label>
        <input type="date" id="end" name="end" required>

        <button type="submit" class="submit-btn">Отправить запрос</button>
    </form>
</section>

<#include "footer.ftl">
