package org.example.dao;

import org.example.models.UserReport;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    // Генерация отчётов по пользователям
    public static List<UserReport> generateUserReports() {
        List<UserReport> reports = new ArrayList<>();
        String sql = "SELECT u.username, COALESCE(SUM(s.hours), 0) AS total_hours, " +
                "COUNT(CASE WHEN t.is_completed = true THEN 1 END) AS completed_tasks, " +
                "COUNT(CASE WHEN t.is_completed = false THEN 1 END) AS pending_tasks " +
                "FROM users u " +
                "LEFT JOIN schedules s ON u.id = s.user_id " +
                "LEFT JOIN tasks t ON u.id = t.assigned_to_user_id " +
                "GROUP BY u.username";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserReport report = new UserReport(
                        rs.getString("username"),
                        rs.getDouble("total_hours"),
                        rs.getInt("completed_tasks"),
                        rs.getInt("pending_tasks")
                );
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }
}
