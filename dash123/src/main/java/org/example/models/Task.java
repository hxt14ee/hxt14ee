package org.example.models;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private LocalDateTime deadline;
    private LocalDateTime startDate; // Новое поле
    private int assignedToUserId;
    private boolean isCompleted;

    // Конструкторы
    public Task() {}

    public Task(int id, String title, LocalDateTime deadline, int assignedToUserId, boolean isCompleted, LocalDateTime startDate) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.assignedToUserId = assignedToUserId;
        this.isCompleted = isCompleted;
        this.startDate = startDate;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getDeadline() { return deadline; }
    public int getAssignedToUserId() { return assignedToUserId; }
    public boolean isCompleted() { return isCompleted; }
    public LocalDateTime getStartDate() { return startDate; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public void setAssignedToUserId(int assignedToUserId) { this.assignedToUserId = assignedToUserId; }
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    // Дополнительный метод для FreeMarker
    public boolean getCompleted() {
        return isCompleted;
    }
}
