package org.example.dao;

import org.example.models.Event;
import org.example.utils.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private static final Logger logger = LoggerFactory.getLogger(EventDAO.class);

    /**
     * Добавляет новое событие в базу данных.
     *
     * @param event Объект события для добавления.
     * @return true, если событие успешно добавлено, иначе false.
     */
    public static boolean addEvent(Event event) {
        String sql = "INSERT INTO events (title, start, \"end\", user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setTimestamp(2, Timestamp.valueOf(event.getStart()));

            if (event.getEnd() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(event.getEnd()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setInt(4, event.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Ошибка при добавлении события: ", e);
            return false;
        }
    }

    /**
     * Получает список событий для конкретного пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список событий пользователя.
     */
    public static List<Event> getEventsByUserId(int userId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, title, start, \"end\", user_id FROM events WHERE user_id = ? ORDER BY start ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setId(rs.getInt("id"));
                    event.setTitle(rs.getString("title"));
                    event.setStart(rs.getTimestamp("start").toLocalDateTime());

                    Timestamp endTimestamp = rs.getTimestamp("end");
                    if (endTimestamp != null) {
                        event.setEnd(endTimestamp.toLocalDateTime());
                    }

                    event.setUserId(rs.getInt("user_id"));
                    events.add(event);
                }
            }

        } catch (SQLException e) {
            logger.error("Ошибка при получении событий пользователя: ", e);
        }

        return events;
    }

    /**
     * Удаляет событие, принадлежащее конкретному пользователю.
     *
     * @param eventId Идентификатор события.
     * @param userId  Идентификатор пользователя.
     * @return true, если событие успешно удалено, иначе false.
     */
    public static boolean deleteEventIfOwned(int eventId, int userId) {
        String sql = "DELETE FROM events WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Ошибка при удалении события: ", e);
            return false;
        }
    }

    /**
     * Обновляет существующее событие.
     *
     * @param event Объект события с обновленными данными.
     * @return true, если событие успешно обновлено, иначе false.
     */
    public static boolean updateEvent(Event event) {
        String sql = "UPDATE events SET title = ?, start = ?, \"end\" = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setTimestamp(2, Timestamp.valueOf(event.getStart()));

            if (event.getEnd() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(event.getEnd()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setInt(4, event.getId());
            stmt.setInt(5, event.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении события: ", e);
            return false;
        }
    }

    /**
     * Проверяет, принадлежит ли событие конкретному пользователю.
     *
     * @param eventId Идентификатор события.
     * @param userId  Идентификатор пользователя.
     * @return true, если событие принадлежит пользователю, иначе false.
     */
    public static boolean isEventOwnedByUser(int eventId, int userId) {
        String sql = "SELECT COUNT(*) AS count FROM events WHERE id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Ошибка при проверке владения событием: ", e);
        }

        return false;
    }
}
