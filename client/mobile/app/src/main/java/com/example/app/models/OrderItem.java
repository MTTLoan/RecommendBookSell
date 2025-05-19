package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItem implements Parcelable {
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

    protected OrderItem(Parcel in) {
        bookId = in.readInt();
        quantity = in.readInt();
        unitPrice = in.readDouble();
        book = in.readParcelable(Book.class.getClassLoader());
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeInt(quantity);
        dest.writeDouble(unitPrice);
        dest.writeParcelable(book, flags);
    }

    // Getters
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