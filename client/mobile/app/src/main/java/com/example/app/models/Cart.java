package com.example.app.models;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;


public class Cart {
    private int id;

    private int userId;

    private List<CartItem> items = new ArrayList<>();

    private String createdAt;

    private String updatedAt;

    // Constructor
    public Cart() {
    }

    public Cart(int id, int userId, List<CartItem> items, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}