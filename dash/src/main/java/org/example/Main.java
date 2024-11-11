package org.example;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        // Настройка порта (опционально, по умолчанию 4567)
        // port(4567);

        // Настройка статических файлов
        staticFiles.location("/public"); // Папка public в resources

        // Настройка FreeMarker
        Configuration freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
        freeMarkerConfiguration.setClassForTemplateLoading(Main.class, "/templates");
        freeMarkerConfiguration.setDefaultEncoding("UTF-8");
        freeMarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freeMarkerConfiguration.setLogTemplateExceptions(false);
        freeMarkerConfiguration.setWrapUncheckedExceptions(true);

        FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(freeMarkerConfiguration);

        // Маршрут для главной страницы
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Главная страница");
            model.put("message", "Добро пожаловать на наш сайт!");

            String username = request.session().attribute("currentUser");
            if (username != null) {
                model.put("username", username);
            }

            return freeMarkerEngine.render(new ModelAndView(model, "index.ftl"));
        });

        // Маршрут для страницы регистрации
        get("/register", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Регистрация");

            String error = request.queryParams("error");
            if (error != null) {
                model.put("error", "Пользователь уже существует или введены некорректные данные.");
            }

            return freeMarkerEngine.render(new ModelAndView(model, "register.ftl"));
        });

        // Обработка регистрации пользователя
        post("/register", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            logger.info("Получены данные регистрации: username={}, password=******", username);

            // Простая валидация
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                logger.warn("Пустые поля при регистрации");
                response.redirect("/register?error=1");
                return null;
            }

            boolean success = UserDAO.registerUser(username, password);
            if (success) {
                logger.info("Пользователь {} успешно зарегистрирован", username);
                response.redirect("/success?message=Регистрация прошла успешно. Теперь вы можете войти.");
            } else {
                logger.warn("Регистрация пользователя {} не удалась (возможно, пользователь уже существует)", username);
                response.redirect("/register?error=1");
            }
            return null;
        });

        // Маршрут для страницы входа
        get("/login", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Вход");

            String error = request.queryParams("error");
            if (error != null) {
                model.put("error", "Неверное имя пользователя или пароль.");
            }

            return freeMarkerEngine.render(new ModelAndView(model, "login.ftl"));
        });

        // Обработка входа пользователя
        post("/login", (request, response) -> {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            logger.info("Попытка входа пользователя: username={}", username);

            User user = UserDAO.authenticateUser(username, password);
            if (user != null) {
                request.session().attribute("currentUser", username);
                logger.info("Пользователь {} успешно вошел в систему", username);
                response.redirect("/dashboard");
            } else {
                logger.warn("Неудачная попытка входа пользователя: username={}", username);
                response.redirect("/login?error=1");
            }
            return null;
        });

        // Маршрут для выхода пользователя
        get("/logout", (request, response) -> {
            String username = request.session().attribute("currentUser");
            if (username != null) {
                logger.info("Пользователь {} вышел из системы", username);
                request.session().removeAttribute("currentUser");
            }
            response.redirect("/");
            return null;
        });

        // Маршрут для личного кабинета
        get("/dashboard", (request, response) -> {
            String username = request.session().attribute("currentUser");
            if (username == null) {
                logger.warn("Неавторизованный доступ к /dashboard");
                response.redirect("/login");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Личный кабинет");
            model.put("username", username);

            return freeMarkerEngine.render(new ModelAndView(model, "dashboard.ftl"));
        });

        // Маршрут для страницы календаря
        get("/calendar", (request, response) -> {
            String username = request.session().attribute("currentUser");
            if (username == null) {
                logger.warn("Неавторизованный доступ к /calendar");
                response.redirect("/login");
                return null;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("title", "Календарь");
            model.put("username", username);

            return freeMarkerEngine.render(new ModelAndView(model, "calendar.ftl"));
        });

        // API для получения всех событий (GET /events)
        get("/events", (request, response) -> {
            response.type("application/json");
            List<Event> events = EventDAO.getAllEvents();
            return gson.toJson(events);
        });

        // API для добавления нового события (POST /events)
        post("/events", (request, response) -> {
            response.type("application/json");
            Event event = gson.fromJson(request.body(), Event.class);
            EventDAO.addEvent(event);
            return gson.toJson(event);
        });

        // API для удаления события по ID (DELETE /events/:id)
        delete("/events/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            EventDAO.deleteEvent(id);
            return "OK";
        });

        // Маршрут для страницы успеха
        get("/success", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Успех");

            String message = request.queryParams("message");
            if (message != null) {
                model.put("successMessage", message);
            }

            return freeMarkerEngine.render(new ModelAndView(model, "success.ftl"));
        });

        // Маршрут для страницы ошибки
        get("/error", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Ошибка");

            String errorMessage = request.queryParams("message");
            if (errorMessage != null) {
                model.put("errorMessage", errorMessage);
            }

            return freeMarkerEngine.render(new ModelAndView(model, "error.ftl"));
        });

        // Обработка несуществующих маршрутов (404)
        notFound((req, res) -> {
            res.type("text/html");
            return "<html><body><h1>404 - Страница не найдена</h1></body></html>";
        });

        // Обработка внутренних ошибок сервера (500)
        internalServerError((req, res) -> {
            res.type("text/html");
            return "<html><body><h1>500 - Внутренняя ошибка сервера</h1></body></html>";
        });

        logger.info("Приложение запущено и доступно по адресу http://localhost:4567/");
    }
}
