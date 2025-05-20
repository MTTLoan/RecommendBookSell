// models/CartItem.java
package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private int bookId;
    private int quantity;
    private boolean isSelected;
    private Book book;

    public CartItem() {
    }

    public CartItem(int bookId, int quantity, Book book) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.book = book;
        this.isSelected = false;
    }

    protected CartItem(Parcel in) {
        bookId = in.readInt();
        quantity = in.readInt();
        isSelected = in.readByte() != 0;
        book = in.readParcelable(Book.class.getClassLoader());
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public CartItem(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeInt(quantity);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeParcelable(book, flags);
    }

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}