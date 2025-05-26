package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItem implements Parcelable {
    private int bookId;
    private int quantity;
    private double unitPrice;
    private boolean recommend; // Thêm trường recommend
    private Book book;

    public OrderItem(int bookId, int quantity, double unitPrice, Book book, boolean recommend) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.book = book;
        this.recommend = recommend;
    }

    protected OrderItem(Parcel in) {
        bookId = in.readInt();
        quantity = in.readInt();
        unitPrice = in.readDouble();
        recommend = in.readByte() != 0; // Đọc recommend
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
        dest.writeByte((byte) (recommend ? 1 : 0)); // Ghi recommend
        dest.writeParcelable(book, flags);
    }

    public int getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public boolean isRecommend() { return recommend; } // Getter
    public void setRecommend(boolean recommend) { this.recommend = recommend; } // Setter
    public Book getBook() { return book; }
}