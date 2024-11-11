package org.example;

public class User {
    private int id;
    private String username;

    // Конструктор
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    // Сеттеры (опционально, если нужны)
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
