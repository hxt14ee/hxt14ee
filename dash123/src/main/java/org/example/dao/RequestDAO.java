package org.example.dao;

import org.example.models.Request;
import org.example.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    // Добавление новой заявки
    public static boolean createRequest(String username, String type, String description, String details) {
        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO requests (username, type, description, details) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, type);
                stmt.setString(3, description);
                stmt.setString(4, details);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получение всех заявок
    public static List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT id, username, type, description, details FROM requests";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Request request = new Request();
                request.setId(rs.getInt("id"));
                request.setUsername(rs.getString("username"));
                request.setType(rs.getString("type"));
                request.setDescription(rs.getString("description"));
                request.setDetails(rs.getString("details"));
                requests.add(request);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requests;
    }


    // Получение заявки по ID
    public static Request getRequestById(int id) {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM requests WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Request(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("type"),
                            rs.getString("description"),
                            rs.getString("details")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<Request> getRequestsByUsername(String username) {
        String sql = "SELECT * FROM requests WHERE username = ?";
        List<Request> requests = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Request request = new Request();
                    request.setId(rs.getInt("id"));
                    request.setUsername(rs.getString("username"));
                    request.setType(rs.getString("type"));
                    request.setDescription(rs.getString("description"));
                    request.setDetails(rs.getString("details"));
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requests;
    }

    // Удаление заявки
    public static boolean deleteRequest(int id) {
        try (Connection conn = Database.getConnection()) {
            String query = "DELETE FROM requests WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
