package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<String> author;
    private int totalQuantitySold;

    public Book(int id, String name, String description, List<Image> images, double price,
                double averageRating, int ratingCount, int stockQuantity, int categoryId,
                String createdAt, List<String> author, int totalQuantitySold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        this.price = price;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.author = author != null ? new ArrayList<>(author) : new ArrayList<>();
        this.totalQuantitySold = totalQuantitySold;
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
        author = new ArrayList<>();
        in.readStringList(author);
        totalQuantitySold = in.readInt();
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
        dest.writeList(images != null ? images : new ArrayList<>());
        dest.writeDouble(price);
        dest.writeDouble(averageRating);
        dest.writeInt(ratingCount);
        dest.writeInt(stockQuantity);
        dest.writeInt(categoryId);
        dest.writeString(createdAt);
        dest.writeStringList(author != null ? author : new ArrayList<>());
        dest.writeInt(totalQuantitySold);
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name != null ? name : "Unknown Title"; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Image> getImages() { return images != null ? new ArrayList<>(images) : new ArrayList<>(); }
    public void setImages(List<Image> images) { this.images = images != null ? new ArrayList<>(images) : new ArrayList<>(); }
    public void setImages(Image image) { this.images = image != null ? Collections.singletonList(image) : new ArrayList<>(); }
    public Image getFirstImage() { return images != null && !images.isEmpty() ? images.get(0) : null; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public List<String> getAuthor() { return author != null ? new ArrayList<>(author) : new ArrayList<>(); }
    public void setAuthor(List<String> author) { this.author = author != null ? new ArrayList<>(author) : new ArrayList<>(); }
    public String getAuthorAsString() { return author != null && !author.isEmpty() ? String.join(", ", author) : "Unknown Author"; }
    public int getTotalQuantitySold() { return totalQuantitySold; }
    public void setTotalQuantitySold(int totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }

    public static class StringTypeAdapter extends TypeAdapter<String> {
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            if (value == null) out.nullValue(); else out.value(value);
        }
        @Override
        public String read(JsonReader in) throws IOException {
            return in.peek() == com.google.gson.stream.JsonToken.NULL ? null : in.nextString();
        }
    }
}