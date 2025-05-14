package com.example.app.models;

public class CartItem {
    private int bookId;

    private int quantity;

    // Constructor
    public CartItem() {
    }

    public CartItem(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}