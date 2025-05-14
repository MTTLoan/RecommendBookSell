package com.example.app.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int bookId;
    private int quantity;
    private double unitPrice;

    public OrderItem(int bookId, int quantity, double unitPrice) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;

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
}