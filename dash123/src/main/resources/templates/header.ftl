<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мой проект</title>
    <link rel="stylesheet" href="/css/style.css">
</head>

<header>
    <h1>Мой проект</h1>
    <nav>
        <ul>
            <!-- Проверка на наличие роли -->
            <#if role??>
                <!-- Элементы для администратора -->
                <#if role?string == "admin">
                    <li><a href="/admin">Админка</a></li>
                    <li><a href="/reports">Заявки</a></li>
                    <li><a href="/register">Регистрация нового пользователя</a></li>
                </#if>

                <!-- Элементы для авторизованного пользователя -->
                <#if role?string != "admin">
                    <li><a href="/workday">Таймер</a></li>
                    <li><a href="/calendar">Календарь</a></li>
                    <li><a href="/tasks">Задачи</a></li>
                    <li><a href="/schedule">Расписание</a></li>
                </#if>

                <!-- Общий элемент для всех авторизованных -->
                <li><a href="/logout">Выход</a></li>
            <#else>
                <!-- Элементы для неавторизованных пользователей -->
                <li><a href="/login">Вход</a></li>
            </#if>
        </ul>
    </nav>
</header>
