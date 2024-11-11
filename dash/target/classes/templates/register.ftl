<#include "header.ftl">
<section>
    <h2>Регистрация</h2>
    <#if error??>
        <div class="error">${error}</div>
    </#if>
    <form action="/register" method="post">
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" required>

        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Зарегистрироваться</button>
    </form>
    <p>Уже есть аккаунт? <a href="/login">Войти</a></p>
</section>
<#include "footer.ftl">
