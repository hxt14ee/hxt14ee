<#include "header.ftl">
<section>
    <h2>Успех</h2>
    <#if successMessage??>
        <div class="success">${successMessage}</div>
    <#else>
        <div class="success">Операция выполнена успешно.</div>
    </#if>
    <p><a href="/">Вернуться на главную</a></p>
</section>
<#include "footer.ftl">
