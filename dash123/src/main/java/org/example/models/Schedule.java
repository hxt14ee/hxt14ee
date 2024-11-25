package org.example.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class Schedule {
    private int id;
    private int userId;
    private String dayOfWeek;
    private double hours;

    // Конструкторы
    public Schedule() {}

    public Schedule(int id, int userId, String dayOfWeek, double hours) {
        this.id = id;
        this.userId = userId;
        this.dayOfWeek = dayOfWeek;
        this.hours = hours;
    }

    // Геттеры и Сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    // Методы для расчёта начала и конца дня
    public String getStartDateTime() {
        // Преобразование строки дня недели в DayOfWeek
        DayOfWeek day = DayOfWeek.valueOf(this.dayOfWeek.toUpperCase());
        LocalDate today = LocalDate.now();
        // Определяем ближайший день недели
        LocalDate targetDate = today.with(TemporalAdjusters.nextOrSame(day));
        // Указываем начало дня
        return targetDate.atTime(0, 0).toString();
    }

    public String getEndDateTime() {
        // Преобразование строки дня недели в DayOfWeek
        DayOfWeek day = DayOfWeek.valueOf(this.dayOfWeek.toUpperCase());
        LocalDate today = LocalDate.now();
        // Определяем ближайший день недели
        LocalDate targetDate = today.with(TemporalAdjusters.nextOrSame(day));
        // Указываем конец дня
        return targetDate.atTime(23, 59).toString();
    }

    // Метод для отладки
    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", userId=" + userId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", hours=" + hours +
                '}';
    }
}
