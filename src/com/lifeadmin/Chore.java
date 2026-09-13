package com.lifeadmin;

import java.time.LocalDate;

public class Chore {
    private int id;
    private String title;
    private LocalDate dueDate;
    private String category;
    private boolean isDone;
    private int userId;
    private String recurrence;

    public Chore(int id, String title, LocalDate dueDate, String category, boolean isDone, int userId, String recurrence) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.category = category;
        this.isDone = isDone;
        this.userId = userId;
        this.recurrence = recurrence;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public String getCategory() { return category; }
    public boolean isDone() { return isDone; }
    public int getUserId() { return userId; }
    public String getRecurrence() { return recurrence; }
    public void setDone(boolean done) { isDone = done; }

    @Override
    public String toString() {
        return title + " | due " + dueDate + " | " + category + " | done=" + isDone;
    }
}