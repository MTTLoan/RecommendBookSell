package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private int bookId;
    private int quantity;
    private boolean selected;
    private boolean recommend; // Thêm trường recommend
    private Book book;

    public CartItem() {
    }

    public CartItem(int bookId, int quantity, Book book, boolean recommend) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.book = book;
        this.selected = false;
        this.recommend = recommend;
    }

    protected CartItem(Parcel in) {
        bookId = in.readInt();
        quantity = in.readInt();
        selected = in.readByte() != 0;
        recommend = in.readByte() != 0; // Đọc recommend
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeInt(quantity);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (recommend ? 1 : 0)); // Ghi recommend
        dest.writeParcelable(book, flags);
    }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean getSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public boolean isRecommend() { return recommend; } // Getter
    public void setRecommend(boolean recommend) { this.recommend = recommend; } // Setter
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
}