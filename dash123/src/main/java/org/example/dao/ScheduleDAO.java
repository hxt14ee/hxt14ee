package org.example.dao;

import org.example.models.Schedule;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleDAO {

    // Получение расписания пользователя
    public static List<Schedule> getUserSchedule(int userId) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(rs.getInt("id"));
                schedule.setUserId(rs.getInt("user_id"));
                schedule.setDayOfWeek(rs.getString("day_of_week"));
                schedule.setHours(rs.getDouble("hours"));

                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    // Обновление расписания пользователя
    public static boolean updateUserSchedule(int userId, Map<String, Double> scheduleMap) {
        String deleteSql = "DELETE FROM schedules WHERE user_id = ?";
        String insertSql = "INSERT INTO schedules (user_id, day_of_week, hours) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<String, Double> entry : scheduleMap.entrySet()) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, entry.getKey());
                    insertStmt.setDouble(3, entry.getValue());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Восстанавливаем состояние автокоммита
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
