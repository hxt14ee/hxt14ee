package org.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Не удалось найти application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка при загрузке конфигурации: " + ex.getMessage(), ex);
        }
    }

    public static String getProperty(String key) {
        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        return envValue != null ? envValue : properties.getProperty(key);
    }
}
