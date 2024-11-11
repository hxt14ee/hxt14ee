<#include "header.ftl">
<section>
    <h2>Ошибка</h2>
    <#if errorMessage??>
        <div class="error">${errorMessage}</div>
    <#else>
        <div class="error">Произошла ошибка.</div>
    </#if>
    <p><a href="/">Вернуться на главную</a></p>
</section>
<#include "footer.ftl">
