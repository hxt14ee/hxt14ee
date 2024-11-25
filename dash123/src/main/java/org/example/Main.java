package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.dao.*;
import org.example.models.*;
import org.example.utils.LocalDateTimeAdapter;
import org.example.utils.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public static void main(String[] args) {
        port(8080); // Установите нужный порт

        // Настройка статических файлов
        staticFiles.location("/public"); // Директория для статических файлов

        // Настройка FreeMarker
        FreeMarkerEngine freeMarker = new FreeMarkerEngine();
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(Main.class, "/templates");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setNamingConvention(freemarker.template.Configuration.CAMEL_CASE_NAMING_CONVENTION); // Или SNAKE_CASE_NAMING_CONVENTION
        freeMarker.setConfiguration(configuration);


        // Фильтр для добавления общих атрибутов в модели
        before((req, res) -> {
            String path = req.pathInfo();

            // Список маршрутов, которые доступны без авторизации
            List<String> publicPaths = Arrays.asList("/login", "/register", "/css/", "/js/", "/images/");

            boolean isPublicPath = publicPaths.stream().anyMatch(path::startsWith);

            if (!isPublicPath) {
                String username = req.session().attribute("username");
                String role = req.session().attribute("role");

                if (username != null) {
                    req.attribute("username", username);
                    req.attribute("role", role);
                } else {
                    res.redirect("/login");
                    halt();
                }
            }
        });




        // Фильтр для защиты маршрутов админа
        before("/admin/*", (request, response) -> {
            String role = request.attribute("role");
            if (role == null || !role.equals("admin")) {
                halt(403, "Доступ запрещён");
            }
        });

        // Маршрут для главной страницы
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Главная");

            String username = req.attribute("username");
            String role = req.attribute("role"); // Предположительно, это роль пользователя

            if (username != null) {
                model.put("username", username);
                model.put("userRole", role); // Передаём роль как userRole для FreeMarker
            }

            return new ModelAndView(model, "index.ftl");
        }, freeMarker);


        // Маршрут для регистрации
        get("/register", (req, res) -> {
            // Проверяем роль пользователя
            String role = req.session().attribute("role");
            if (role == null || !role.equals("admin")) {
                res.redirect("/login?error=Доступ запрещён");
                halt();
            }

            // Создаём модель для передачи данных в шаблон
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Регистрация нового пользователя");
            model.put("role", role); // Передаём роль пользователя в шаблон

            // Передача возможной ошибки в шаблон
            String error = req.queryParams("error");
            if (error != null) {
                model.put("error", error);
            }

            return new ModelAndView(model, "register.ftl");
        }, freeMarker);


        post("/register", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                res.redirect("/register?error=Все+поля+обязательны");
                return null;
            }

            boolean success = UserDAO.registerUser(username, password);

            if (success) {
                res.redirect("/login?success=Регистрация+успешна");
            } else {
                res.redirect("/register?error=Регистрация+не+удалась");
            }

            return null;
        });

        // Маршрут для входа
        get("/login", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Вход в систему");

            String error = req.queryParams("error");
            String success = req.queryParams("success");
            if (error != null) {
                model.put("error", error);
            }
            if (success != null) {
                model.put("success", success);
            }

            // Если пользователь уже авторизован, перенаправляем на главную
            if (req.session().attribute("username") != null) {
                res.redirect("/");
                return null;
            }

            return new ModelAndView(model, "login.ftl");
        }, freeMarker);



        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                res.redirect("/login?error=Все+поля+обязательны");
                return null;
            }

            // Аутентификация пользователя
            User user = UserDAO.authenticateUser(username, password);

            if (user != null) {
                // Устанавливаем атрибуты сессии
                req.session().attribute("userId", user.getId());
                req.session().attribute("username", user.getUsername());
                req.session().attribute("role", user.getRole()); // Добавляем роль в сессию

                // Перенаправляем на соответствующую страницу
                if (user.getRole().equals("admin")) {
                    res.redirect("/admin");
                } else {
                    res.redirect("/workday");
                }
            } else {
                res.redirect("/login?error=Неверные+учетные+данные");
            }

            return null;
        });

        post("/workday/action", (req, res) -> {
            String action = req.queryParams("action");
            if (action == null) {
                res.redirect("/workday?error=Неизвестное+действие");
                return null;
            }

            // Получаем информацию из сессии
            LocalDateTime now = LocalDateTime.now();
            String workdayStatus = req.session().attribute("workdayStatus");
            LocalDateTime workdayStartTime = req.session().attribute("workdayStartTime");
            LocalDateTime workdayPauseTime = req.session().attribute("workdayPauseTime");

            switch (action) {
                case "start":
                    // Начинаем новый рабочий день
                    req.session().attribute("workdayStartTime", now);
                    req.session().attribute("workdayPauseTime", null);
                    req.session().attribute("workdayStatus", "active");
                    break;
                case "pause":
                    // Ставим на паузу (если не больше 1 часа)
                    if (workdayStatus != null && workdayStatus.equals("active")) {
                        req.session().attribute("workdayPauseTime", now);
                        req.session().attribute("workdayStatus", "paused");
                    } else {
                        res.redirect("/workday?error=Рабочий+день+не+активен");
                        return null;
                    }
                    break;
                case "resume":
                    // Возобновляем рабочий день (если пауза не больше 1 часа)
                    if (workdayPauseTime != null && workdayPauseTime.plusHours(1).isAfter(now)) {
                        req.session().attribute("workdayPauseTime", null);
                        req.session().attribute("workdayStatus", "active");
                    } else {
                        res.redirect("/workday?error=Пауза+превышает+1+час");
                        return null;
                    }
                    break;
                case "stop":
                    // Завершаем рабочий день
                    req.session().attribute("workdayStartTime", null);
                    req.session().attribute("workdayPauseTime", null);
                    req.session().attribute("workdayStatus", "stopped");
                    break;
                default:
                    res.redirect("/workday?error=Неизвестное+действие");
                    return null;
            }

            res.redirect("/workday");
            return null;
        });
        get("/workday", (req, res) -> {
            String username = req.session().attribute("username");
            String role = req.session().attribute("role");

            if (username == null) {
                res.redirect("/login");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Рабочий день");
            model.put("username", username);
            model.put("role", role);

            // Дополнительные данные для отображения
            model.put("workdayStartTime", req.session().attribute("workdayStartTime"));
            model.put("workdayEndTime", req.session().attribute("workdayEndTime"));

            return new ModelAndView(model, "workday.ftl");
        }, freeMarker);


        // Маршрут для выхода
        get("/logout", (req, res) -> {
            req.session().invalidate();
            res.redirect("/login?success=Вы+успешно+вышли+из+системы");
            return null;
        });

        // Маршрут для административной панели
        get("/admin", (req, res) -> {
            String username = req.session().attribute("username");
            String role = req.session().attribute("role");

            // Проверяем авторизацию
            if (username == null || !"admin".equals(role)) {
                res.redirect("/login");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Админка");
            model.put("username", username);
            model.put("role", role); // Передаем роль в шаблон

            // Добавляем дополнительные данные, если нужно
            model.put("tasks", TaskDAO.getAllTasks());
            model.put("users", UserDAO.getAllUsers());

            return new ModelAndView(model, "admin.ftl");
        }, freeMarker);

        post("/requests", (req, res) -> {
            // Проверяем, авторизован ли пользователь
            String username = req.session().attribute("username");
            if (username == null) {
                res.redirect("/login");
                return null;
            }

            // Считываем параметры запроса
            String type = req.queryParams("type");
            String description = req.queryParams("description");
            String start = req.queryParams("start");
            String end = req.queryParams("end");

            // Валидация входных данных
            if (type == null || type.isEmpty() || description == null || description.isEmpty()
                    || start == null || start.isEmpty() || end == null || end.isEmpty()) {
                res.redirect("/requests?errorMessage=Все+поля+обязательны");
                return null;
            }

            // Формируем детали заявки
            String details = "С " + start + " по " + end;

            // Создаем заявку
            boolean success = RequestDAO.createRequest(username, type, description, details);

            // Редирект в зависимости от результата
            if (success) {
                res.redirect("/requests?successMessage=Запрос%20отправлен%20успешно");
            } else {
                res.redirect("/requests?errorMessage=Ошибка%20при%20отправке%20запроса");
            }
            return null;
        });

        post("/requests/:id/approve", (req, res) -> {
            int requestId = Integer.parseInt(req.params(":id"));
            Request request = RequestDAO.getRequestById(requestId);

            if (request == null) {
                res.redirect("/admin/requests?errorMessage=Запрос%20не%20найден");
                return null;
            }

            // Разбираем строки из details для извлечения дат
            String[] details = request.getDetails().split(" ");
            LocalDate startDate = LocalDate.parse(details[1]); // Дата начала
            LocalDate endDate = LocalDate.parse(details[3]);   // Дата окончания

            Event event = new Event();
            event.setTitle("Отпуск: " + request.getType());
            event.setStart(startDate.atStartOfDay()); // Преобразуем LocalDate в LocalDateTime (начало дня)
            event.setEnd(endDate.atTime(23, 59));    // Преобразуем LocalDate в LocalDateTime (конец дня)

            // Получаем user_id по username
            Integer userId = UserDAO.getUserIdByUsername(request.getUsername());
            if (userId == null) {
                res.redirect("/admin/requests?errorMessage=Пользователь%20не%20найден");
                return null;
            }
            event.setUserId(userId);

            boolean success = EventDAO.addEvent(event);

            if (success) {
                RequestDAO.deleteRequest(requestId); // Удаляем заявку после одобрения
                res.redirect("/admin/requests?successMessage=Заявка%20одобрена");
            } else {
                res.redirect("/admin/requests?errorMessage=Ошибка%20при%20одобрении%20заявки");
            }
            return null;
        });


        post("/requests/:id/reject", (req, res) -> {
            int requestId = Integer.parseInt(req.params(":id"));
            boolean success = RequestDAO.deleteRequest(requestId);

            if (success) {
                res.redirect("/admin/requests?successMessage=Заявка%20отклонена");
            } else {
                res.redirect("/admin/requests?errorMessage=Ошибка%20при%20отклонении%20заявки");
            }
            return null;
        });
        get("/admin/requests", (req, res) -> {
            String username = req.session().attribute("username");
            String role = req.session().attribute("role");
            Map<String, Object> model = new HashMap<>();
            List<Request> requests = RequestDAO.getAllRequests();
            model.put("requests", requests);
            model.put("title", "Управление заявками");
            return new ModelAndView(model, "reports.ftl");
        }, freeMarker);

        // Маршрут для создания задачи (админ)
        post("/admin/createTask", (req, res) -> {
            logger.info("POST /admin/createTask вызван");
            String title = req.queryParams("title");
            String startDateStr = req.queryParams("startDate");
            String deadlineStr = req.queryParams("deadline");
            String assignedToStr = req.queryParams("assignedTo");

            // Валидация данных
            if (title == null || startDateStr == null || deadlineStr == null || assignedToStr == null ||
                    title.trim().isEmpty() || startDateStr.trim().isEmpty() || deadlineStr.trim().isEmpty() || assignedToStr.trim().isEmpty()) {
                res.redirect("/admin?error=Все+поля+обязательны");
                logger.warn("Валидация данных не пройдена");
                return null;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime startDate;
            try {
                // Парсинг startDate с использованием кастомного форматтера
                startDate = LocalDateTime.parse(startDateStr.replace("T", " "), formatter);
            } catch (DateTimeParseException e) {
                logger.error("Некорректный формат даты начала: {}", startDateStr, e);
                res.redirect("/admin?error=Некорректный+формат+даты+начала");
                return null;
            }

            LocalDateTime deadline;
            try {
                // Парсинг deadline с использованием кастомного форматтера
                deadline = LocalDateTime.parse(deadlineStr.replace("T", " "), formatter);
            } catch (DateTimeParseException e) {
                logger.error("Некорректный формат дедлайна: {}", deadlineStr, e);
                res.redirect("/admin?error=Некорректный+формат+дедлайна");
                return null;
            }

            int assignedTo;
            try {
                assignedTo = Integer.parseInt(assignedToStr);
            } catch (NumberFormatException e) {
                logger.error("Некорректный ID пользователя: {}", assignedToStr, e);
                res.redirect("/admin?error=Некорректный+ID+пользователя");
                return null;
            }

            // Проверка логики дат: startDate не позже deadline
            if (startDate.isAfter(deadline)) {
                logger.warn("Дата начала {} позже дедлайна {}", startDate, deadline);
                res.redirect("/admin?error=Дата+начала+не+может+быть+после+дедлайна");
                return null;
            }

            Task task = new Task();
            task.setTitle(title);
            task.setStartDate(startDate);
            task.setDeadline(deadline);
            task.setAssignedToUserId(assignedTo);
            task.setCompleted(false);

            logger.info("Создание задачи: Название={}, Дата начала={}, Дедлайн={}, Назначено пользователю ID={}",
                    title, startDate, deadline, assignedTo);

            boolean success = TaskDAO.addTask(task);

            if (success) {
                logger.info("Задача успешно создана: {}", title);
                res.redirect("/admin?success=Задача+создана+успешно");
            } else {
                logger.error("Не удалось создать задачу: {}", title);
                res.redirect("/admin?error=Не+удалось+создать+задачу");
            }

            return null;
        });
        // Маршрут для обновления задачи (админ)
        post("/admin/updateTask", (req, res) -> {
            String idStr = req.queryParams("id");
            String title = req.queryParams("title");
            String deadlineStr = req.queryParams("deadline");
            String assignedToStr = req.queryParams("assignedTo");
            String isCompletedStr = req.queryParams("isCompleted");

            if (idStr == null || title == null || deadlineStr == null || assignedToStr == null ||
                    idStr.trim().isEmpty() || title.trim().isEmpty() ||
                    deadlineStr.trim().isEmpty() || assignedToStr.trim().isEmpty()) {
                res.redirect("/admin?error=Все+поля+обязательны");
                return null;
            }

            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                res.redirect("/admin?error=Некорректный+ID+задачи");
                return null;
            }

            LocalDateTime deadline;
            try {
                deadline = LocalDateTime.parse(deadlineStr);
            } catch (Exception e) {
                res.redirect("/admin?error=Некорректный+формат+дедлайна");
                return null;
            }

            int assignedTo;
            try {
                assignedTo = Integer.parseInt(assignedToStr);
            } catch (NumberFormatException e) {
                res.redirect("/admin?error=Некорректный+ID+пользователя");
                return null;
            }

            boolean isCompleted = isCompletedStr != null && isCompletedStr.equals("on");

            Task task = new Task();
            task.setId(id);
            task.setTitle(title);
            task.setDeadline(deadline);
            task.setAssignedToUserId(assignedTo);
            task.setCompleted(isCompleted);

            boolean success = TaskDAO.updateTask(task);

            if (success) {
                res.redirect("/admin?success=Задача+обновлена+успешно");
            } else {
                res.redirect("/admin?error=Не+удалось+обновить+задачу");
            }

            return null;
        });

        // Маршрут для календаря
        get("/calendar", (req, res) -> {
            String username = req.attribute("username");
            Integer userId = req.session().attribute("userId");
            String role = req.attribute("role");

            if (username == null || userId == null) {
                res.redirect("/login?error=Unauthorized");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Календарь");
            model.put("username", username);
            model.put("role", role); // Передача роли в модель

            return new ModelAndView(model, "calendar.ftl");
        }, freeMarker);

        // API Endpoint для получения событий в формате JSON
        get("/api/events", (req, res) -> {
            Integer userId = req.session().attribute("userId");
            if (userId == null) {
                res.status(401);
                return "Unauthorized";
            }

            // Получение событий пользователя
            List<Event> userEvents = EventDAO.getEventsByUserId(userId);

            // Получение задач пользователя
            List<Task> userTasks = TaskDAO.getTasksByUserId(userId);

            // Получение расписания пользователя
            List<Schedule> userSchedules = ScheduleDAO.getUserSchedule(userId);

            // Преобразование расписания в события для календаря
            List<Map<String, Object>> scheduleEvents = userSchedules.stream().map(schedule -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", "schedule_" + schedule.getId());
                map.put("title", "Рабочее время: " + schedule.getHours() + " ч.");
                map.put("start", schedule.getStartDateTime()); // Используем метод из модели Schedule
                map.put("end", schedule.getEndDateTime()); // Используем метод из модели Schedule
                map.put("color", "green"); // Цвет для обозначения расписания
                map.put("groupId", "tasks"); // Для задач
                map.put("color", "rgba(220, 53, 69, 0.8)"); // Полупрозрачный красный для задач

                return map;
            }).collect(Collectors.toList());

            // Преобразование событий и задач в формат FullCalendar
            List<Map<String, Object>> events = userEvents.stream().map(event -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", event.getId());
                map.put("title", event.getTitle());
                map.put("start", event.getStart().toString());
                map.put("end", event.getEnd() != null ? event.getEnd().toString() : null);
                map.put("color", "blue");
                return map;
            }).collect(Collectors.toList());

            List<Map<String, Object>> taskEvents = userTasks.stream().map(task -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", "task_" + task.getId());
                map.put("title", task.getTitle() + " (Дедлайн)");
                map.put("start", task.getDeadline().toString());
                map.put("end", task.getDeadline().toString());
                map.put("color", "red");
                return map;
            }).collect(Collectors.toList());

            // Объединяем все типы событий
            events.addAll(scheduleEvents);
            events.addAll(taskEvents);

            res.type("application/json");
            return gson.toJson(events);
        });


        get("/reports", (req, res) -> {
            // Проверяем роль пользователя
            String role = req.session().attribute("role");
            if (role == null || !role.equals("admin")) {
                res.redirect("/login?error=Доступ запрещён");
                halt();
            }

            // Создаём модель для передачи данных в шаблон
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Отчёты");
            model.put("role", role); // Передаём роль пользователя в шаблон

            // Получение данных для отчётов и заявок
            List<UserReport> reports = ReportDAO.generateUserReports();
            List<Request> requests = RequestDAO.getAllRequests();

            // Добавляем отчёты и заявки в модель
            model.put("reports", reports);
            model.put("requests", requests);

            return new ModelAndView(model, "reports.ftl");
        }, freeMarker);





        // Маршрут для отображения задач пользователя
        get("/tasks", (req, res) -> {
            Integer userId = req.session().attribute("userId");
            String username = req.session().attribute("username");
            String role = req.session().attribute("role"); // Добавляем роль

            // Проверяем, что пользователь авторизован
            if (userId == null || username == null) {
                res.redirect("/login?error=Unauthorized");
                return null;
            }

            // Подготавливаем данные для шаблона
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Ваши задачи");
            model.put("username", username);
            model.put("role", role); // Передаем роль для header.ftl

            // Получение задач пользователя
            List<Task> tasks = TaskDAO.getTasksByUserId(userId);
            model.put("tasks", tasks);

            // Обработка сообщений об успехе/ошибке
            String success = req.queryParams("success");
            String error = req.queryParams("error");
            if (success != null) {
                model.put("success", success);
            }
            if (error != null) {
                model.put("error", error);
            }

            return new ModelAndView(model, "tasks.ftl");
        }, freeMarker);


        // Маршрут для отображения и редактирования расписания пользователя
        get("/schedule", (req, res) -> {
            Integer userId = req.session().attribute("userId");
            String username = req.attribute("username");
            String role = req.attribute("role");

            if (userId == null) {
                res.redirect("/login?error=Unauthorized");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Ваше расписание");
            model.put("username", username != null ? username : "");
            model.put("role", role != null ? role : ""); // Добавляем роль в модель

            List<Schedule> schedules = ScheduleDAO.getUserSchedule(userId);
            model.put("schedules", schedules);

            String success = req.queryParams("success");
            String error = req.queryParams("error");
            if (success != null) {
                model.put("success", success);
            }
            if (error != null) {
                model.put("error", error);
            }

            return new ModelAndView(model, "schedule.ftl");
        }, freeMarker);


        // Маршрут для обновления расписания пользователя
        post("/schedule", (req, res) -> {
            Integer userId = req.session().attribute("userId");
            String username = req.attribute("username");

            if (userId == null) {
                res.redirect("/login?error=Unauthorized");
                return null;
            }

            Map<String, Double> scheduleMap = new HashMap<>();
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            for (String day : days) {
                String hoursStr = req.queryParams(day.toLowerCase() + "Hours");
                if (hoursStr != null && !hoursStr.trim().isEmpty()) {
                    try {
                        double hours = Double.parseDouble(hoursStr);
                        scheduleMap.put(day, hours);
                    } catch (NumberFormatException e) {
                        logger.warn("Некорректное значение часов для дня '{}': {}", day, hoursStr);
                        res.redirect("/schedule?error=Некорректное+значение+часов+для+" + day);
                        return null;
                    }
                }
            }

            boolean success = ScheduleDAO.updateUserSchedule(userId, scheduleMap);
            if (success) {
                logger.info("Расписание пользователя '{}' успешно обновлено", username);
                res.redirect("/schedule?success=Расписание+успешно+обновлено");
            } else {
                logger.error("Не удалось обновить расписание пользователя '{}'", username);
                res.redirect("/schedule?error=Не+удалось+обновить+расписание");
            }

            return null;
        });
        // Маршрут для отображения запросов пользователя
        get("/requests", (req, res) -> {
            String username = req.session().attribute("username");
            if (username == null) {
                res.redirect("/login?error=Unauthorized");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Ваши запросы");

            List<Request> userRequests = RequestDAO.getRequestsByUsername(username);
            model.put("requests", userRequests);

            String successMessage = req.queryParams("successMessage");
            String errorMessage = req.queryParams("errorMessage");
            if (successMessage != null) {
                model.put("successMessage", successMessage);
            }
            if (errorMessage != null) {
                model.put("errorMessage", errorMessage);
            }

            return new ModelAndView(model, "requests.ftl");
        }, freeMarker);


        // Маршрут для добавления события
        post("/addEvent", (req, res) -> {
            // Получение параметров из формы
            String title = req.queryParams("title");
            String startStr = req.queryParams("start");
            String endStr = req.queryParams("end");

            // Валидация входных данных
            if (title == null || startStr == null || title.trim().isEmpty() || startStr.trim().isEmpty()) {
                res.status(400);
                return "Необходимы заголовок и время начала события.";
            }

            LocalDateTime start;
            LocalDateTime end = null;
            try {
                start = LocalDateTime.parse(startStr);
                if (endStr != null && !endStr.trim().isEmpty()) {
                    end = LocalDateTime.parse(endStr);
                }
            } catch (Exception e) {
                res.status(400);
                return "Некорректный формат даты и времени.";
            }

            // Получение userId из сессии
            Integer userId = req.session().attribute("userId");
            if (userId == null) {
                res.status(401);
                return "Необходима аутентификация.";
            }

            // Создание объекта события
            Event event = new Event();
            event.setTitle(title);
            event.setStart(start);
            event.setEnd(end);
            event.setUserId(userId);

            // Сохранение события в базе данных
            boolean success = EventDAO.addEvent(event);

            if (success) {
                // Перенаправление обратно в календарь
                res.redirect("/calendar");
            } else {
                res.status(500);
                return "Не удалось добавить событие.";
            }

            return null;
        });

        // Обработка ошибок
        exception(Exception.class, (e, req, res) -> {
            logger.error("Ошибка: ", e);
            res.status(500);
            res.body("Внутренняя ошибка сервера");
        });

        // Остановка приложения при завершении работы JVM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Закрытие пула соединений");
            Database.closeDataSource();
        }));

        logger.info("Приложение запущено на http://localhost:8080/");
    }

    /**
     * Вспомогательный метод для вычисления количества часов между двумя временами.
     * Время должно быть в формате "HH:mm".
     */
    private static double calculateHours(String start, String end) throws Exception {
        String[] startParts = start.split(":");
        String[] endParts = end.split(":");

        if (startParts.length != 2 || endParts.length != 2) {
            throw new Exception("Некорректный формат времени");
        }

        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);

        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);

        double startTotal = startHour + startMinute / 60.0;
        double endTotal = endHour + endMinute / 60.0;

        return endTotal - startTotal;
    }
}
