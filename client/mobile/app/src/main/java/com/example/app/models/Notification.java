package com.example.app.models;

public class Notification {
    private int id;
    private int userId;
    private String title;
    private String message;
    private boolean isRead;
    private String createdAt;

    public Notification(int id, int userId, String title, String message, boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setter for isRead (dùng khi đánh dấu đã đọc)
    public void setRead(boolean read) {
        isRead = read;
    }
}