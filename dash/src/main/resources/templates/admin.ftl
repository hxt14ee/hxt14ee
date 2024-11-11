<#include "header.ftl">
<section>
    <h2>Управление графиками сотрудников</h2>
    <p>Редактирование графиков, добавление/удаление смен.</p>

    <h2>Просмотр отчетов</h2>
    <p>Доступ к отчетам по часам и эффективности.</p>

    <h2>Заявки на отпуск и изменение графика</h2>
    <div id="requests-view">
        <!-- Здесь будет список заявок -->
        <ul id="requests-list">
            <#if requests?? && requests?size > 0>
                <#list requests as request>
                    <li>${request.description} — ${request.details}</li>
                </#list>
            <#else>
                <li>Нет доступных заявок.</li>
            </#if>
        </ul>
    </div>
</section>
<#include "footer.ftl">
