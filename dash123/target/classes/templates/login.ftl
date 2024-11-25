<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<#include "header.ftl">
<section>
    <h2>Вход в систему</h2>
    <#if error??>
        <div class="error">${error}</div>
    </#if>
    <#if success??>
        <div class="success">${success}</div>
    </#if>
    <form action="/login" method="post">
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Войти</button>
    </form>
</section>
<#include "footer.ftl">
