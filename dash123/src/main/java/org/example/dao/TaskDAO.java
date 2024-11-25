package org.example.dao;

import org.example.models.Task;
import org.example.utils.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);

    public static List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE assigned_to_user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));

                Timestamp startDateTimestamp = rs.getTimestamp("start_date"); // Исправлено имя столбца
                if (startDateTimestamp != null) {
                    task.setStartDate(startDateTimestamp.toLocalDateTime());
                }

                Timestamp deadlineTimestamp = rs.getTimestamp("deadline");
                if (deadlineTimestamp != null) {
                    task.setDeadline(deadlineTimestamp.toLocalDateTime());
                } else {
                    task.setDeadline(null);
                }

                task.setAssignedToUserId(rs.getInt("assigned_to_user_id")); // Исправлено имя столбца
                task.setCompleted(rs.getBoolean("is_completed"));
                tasks.add(task);
            }
            logger.info("Получено {} задач для пользователя с ID {}", tasks.size(), userId);
        } catch (SQLException e) {
            logger.error("Ошибка при получении задач пользователя: ", e);
        }
        return tasks;
    }

    // Добавление новой задачи
    public static boolean addTask(Task task) {
        // Исправлено количество плейсхолдеров и имена столбцов
        String sql = "INSERT INTO tasks (title, start_date, deadline, assigned_to_user_id, is_completed) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());

            if (task.getStartDate() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(task.getStartDate()));
            } else {
                stmt.setTimestamp(2, null);
            }

            if (task.getDeadline() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(task.getDeadline()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setInt(4, task.getAssignedToUserId());
            stmt.setBoolean(5, task.isCompleted());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Задача добавлена: {}", task.getTitle());
                return true;
            } else {
                logger.warn("Не удалось добавить задачу: {}", task.getTitle());
                return false;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при добавлении задачи: ", e);
            return false;
        }
    }

    // Обновление существующей задачи
    public static boolean updateTask(Task task) {
        // Исправлено имя столбца deadline и количество плейсхолдеров
        String sql = "UPDATE tasks SET title = ?, deadline = ?, assigned_to_user_id = ?, is_completed = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());

            if (task.getDeadline() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(task.getDeadline()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }

            stmt.setInt(3, task.getAssignedToUserId());
            stmt.setBoolean(4, task.isCompleted());
            stmt.setInt(5, task.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Задача обновлена: ID={}", task.getId());
                return true;
            } else {
                logger.warn("Не удалось обновить задачу: ID={}", task.getId());
                return false;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении задачи: ", e);
            return false;
        }
    }

    // Получение всех задач (для админа)
    public static List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));

                // Установка startDate, если необходимо
                Timestamp startDateTimestamp = rs.getTimestamp("start_date");
                if (startDateTimestamp != null) {
                    task.setStartDate(startDateTimestamp.toLocalDateTime());
                }

                // Проверка на null для поля deadline
                Timestamp deadlineTimestamp = rs.getTimestamp("deadline");
                if (deadlineTimestamp != null) {
                    task.setDeadline(deadlineTimestamp.toLocalDateTime());
                } else {
                    task.setDeadline(null);
                }

                task.setAssignedToUserId(rs.getInt("assigned_to_user_id"));
                task.setCompleted(rs.getBoolean("is_completed"));

                tasks.add(task);
            }
            logger.info("Получено {} задач", tasks.size());
        } catch (SQLException e) {
            logger.error("Ошибка при получении задач: ", e);
        }

        return tasks;
    }
}
