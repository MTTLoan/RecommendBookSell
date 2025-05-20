package com.example.app.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    private int id;
    private int userId;
    @SerializedName("orderId")
    private int orderId;
    private String title;
    private String message;
    private boolean isRead;
    private String createdAt;
    private String imageUrl;

    // Constructor
    public Notification(int id, int userId, int orderId, String title, String message, boolean isRead, String createdAt, String imageUrl) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt='" + createdAt + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}