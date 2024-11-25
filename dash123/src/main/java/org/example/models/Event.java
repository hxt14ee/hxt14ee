package org.example.models;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private int userId; // Связь с пользователем

    // Конструкторы
    public Event() {}

    public Event(int id, String title, LocalDateTime start, LocalDateTime end, int userId) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.userId = userId;
    }

    // Геттеры и Сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
