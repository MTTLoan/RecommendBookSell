package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
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
        createdAt = in.readString(); // Read as String directly
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
        dest.writeString(createdAt); // Write as String directly
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

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImages(List<Image> images) { this.images = images; }
    public void setPrice(double price) { this.price = price; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setAuthor(String author) { this.author = author; }

    // Custom TypeAdapter for String to handle MongoDB's ISO 8601 string
    public static class StringTypeAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value); // Write the ISO 8601 string directly
            }
        }

        @Override
        public String read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return in.nextString(); // Read the ISO 8601 string directly
        }
    }
}