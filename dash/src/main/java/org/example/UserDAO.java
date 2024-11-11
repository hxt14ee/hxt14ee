package org.example;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    // Регистрация нового пользователя
    public static boolean registerUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("Пользователь {} успешно зарегистрирован.", username);
                return true;
            } else {
                logger.warn("Регистрация пользователя {} не удалась.", username);
                return false;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", username, e.getMessage());
            e.printStackTrace(); // Дополнительный вывод стека для отладки
            return false;
        }
    }

    // Аутентификация пользователя
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    int id = rs.getInt("id");
                    logger.info("Пользователь {} успешно аутентифицирован.", username);
                    return new User(id, username);
                } else {
                    logger.warn("Неверный пароль для пользователя {}.", username);
                }
            } else {
                logger.warn("Пользователь {} не найден.", username);
            }

        } catch (SQLException e) {
            logger.error("Ошибка при аутентификации пользователя {}: {}", username, e.getMessage());
            e.printStackTrace(); // Дополнительный вывод стека для отладки
        }

        return null; // Аутентификация не удалась
    }
}
