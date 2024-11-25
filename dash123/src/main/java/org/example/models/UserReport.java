package org.example.models;

public class UserReport {
    private String username;
    private double totalHours;
    private int completedTasks;
    private int pendingTasks;

    // Конструктор
    public UserReport(String username, double totalHours, int completedTasks, int pendingTasks) {
        this.username = username;
        this.totalHours = totalHours;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public double getTotalHours() { return totalHours; }
    public int getCompletedTasks() { return completedTasks; }
    public int getPendingTasks() { return pendingTasks; }

    public void setUsername(String username) { this.username = username; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    public void setPendingTasks(int pendingTasks) { this.pendingTasks = pendingTasks; }
}
