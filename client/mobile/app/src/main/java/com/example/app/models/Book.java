package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Book implements Parcelable {
    private int id;
    private String name;
    private String description;
    private List<Image> images;
    private double price;
    private double averageRating;
    private int ratingCount;
    private int stockQuantity;
    private int categoryId;
    private String createdAt;
    private String author;

    public Book(int id, String name, String description, List<Image> images, double price,
                double averageRating, int ratingCount, int stockQuantity, int categoryId,
                String createdAt, String author) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.images = images;
        this.price = price;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.author = author;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        images = new ArrayList<>();
        in.readList(images, Image.class.getClassLoader());
        price = in.readDouble();
        averageRating = in.readDouble();
        ratingCount = in.readInt();
        stockQuantity = in.readInt();
        categoryId = in.readInt();
        createdAt = in.readString();
        author = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeList(images);
        dest.writeDouble(price);
        dest.writeDouble(averageRating);
        dest.writeInt(ratingCount);
        dest.writeInt(stockQuantity);
        dest.writeInt(categoryId);
        dest.writeString(createdAt);
        dest.writeString(author);
    }

    // Getters
    public int getId() { return id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public List<Image> getImages() { return images; }

    public double getPrice() { return price; }

    public double getAverageRating() { return averageRating; }

    public int getRatingCount() { return ratingCount; }

    public int getStockQuantity() { return stockQuantity; }

    public int getCategoryId() { return categoryId; }

    public String getCreatedAt() { return createdAt; }

    public String getAuthor() { return author; }

    // Inner class Image implements Parcelable
    public static class Image implements Parcelable {
        private String url;
        private String alt;

        public Image(String url, String alt) {
            this.url = url;
            this.alt = alt;
        }

        public Image(Parcel in) {
            url = in.readString();
            alt = in.readString();
        }

        public static final Creator<Image> CREATOR = new Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel in) {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(url);
            dest.writeString(alt);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getUrl() { return url; }

        public String getAlt() { return alt; }
    }
}
