package org.example.models;

public class Request {
    private int id;
    private String username;
    private String type;
    private String description;
    private String details;

    // Конструктор без параметров
    public Request() {}

    // Конструктор с параметрами
    public Request(int id, String username, String type, String description, String details) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.description = description;
        this.details = details;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
