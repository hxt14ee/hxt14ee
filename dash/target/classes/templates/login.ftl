<#include "header.ftl">
<section>
    <h2>Вход в систему</h2>
    <#if error??>
        <div class="error">${error}</div>
    </#if>
    <form action="/login" method="post">
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Войти</button>
    </form>
    <p>Еще нет аккаунта? <a href="/register">Зарегистрироваться</a></p>
</section>
<#include "footer.ftl">
