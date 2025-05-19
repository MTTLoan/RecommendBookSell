package com.example.app.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int bookId;
    private int quantity;
    private double unitPrice;
    private Book book; // Thông tin sách

    public OrderItem(int bookId, int quantity, double unitPrice, Book book) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.book = book;
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

    public Book getBook() {
        return book;
    }
}