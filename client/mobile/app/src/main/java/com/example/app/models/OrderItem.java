package com.example.app.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int bookId;
    private int quantity;
    private double unitPrice;
    private String bookName;
    private String imageUrl;

    public OrderItem(int bookId, int quantity, double unitPrice, String bookName, String imageUrl) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.bookName = bookName;
        this.imageUrl = imageUrl;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getBookName() {
        return bookName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}