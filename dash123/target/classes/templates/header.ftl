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
            <li><a href="/">Главная</a></li>
            <li><a href="/calendar">Календарь</a></li>
            <li><a href="/tasks">Задачи</a></li>
            <li><a href="/schedule">Расписание</a></li>
            <#if userRole??>
                <#if userRole == "admin">
                    <li><a href="/admin">Админ</a></li>
                    <li><a href="/reports">Запросы</a></li>
                </#if>
                <li><a href="/logout">Выход</a></li>
            <#else>
                <li><a href="/login">Вход</a></li>
            </#if>
        </ul>
    </nav>
</header>


