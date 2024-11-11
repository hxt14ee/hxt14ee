<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<header>
    <nav class="navbar">
        <h1 class="navbar-title">Dashboard</h1>
        <ul class="navbar-links">
            <li><a href="/">Главная</a></li>
            <li><a href="/schedules">График</a></li>
            <li><a href="/requests">Запросы</a></li>
            <!-- Если маршруты администратора и отчетов отсутствуют, закомментируйте их -->
            <!-- <li><a href="/admin">Администратор</a></li>
            <li><a href="/reports">Отчеты</a></li> -->
            <#if username??>
                <li><a href="/logout">Выход</a></li>
            <#else>
                <li><a href="/login">Вход</a></li>
                <li><a href="/register">Регистрация</a></li>
            </#if>
        </ul>
    </nav>
</header>
<main>
