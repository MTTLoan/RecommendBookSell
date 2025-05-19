package com.example.app.models;

import android.os.Parcelable;
import android.os.Parcel;

public class Review implements Parcelable {
    private int id;
    private int userId;
    private int bookId;
    private int rating;
    private String comment;
    private String createdAt; // Đổi sang String để khớp với MongoDB

    // Constructor
    public Review(int id, int userId, int bookId, int rating, String comment, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    protected Review(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        bookId = in.readInt();
        rating = in.readInt();
        comment = in.readString();
        createdAt = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(bookId);
        dest.writeInt(rating);
        dest.writeString(comment);
        dest.writeString(createdAt);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}