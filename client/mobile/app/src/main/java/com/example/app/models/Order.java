package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {
    private int id;
    private int userId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;
    private Integer shippingProvince;
    private Integer shippingDistrict;
    private Integer shippingWard;
    private String shippingDetail;
    @SerializedName("items")
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userFullName;
    private String userPhoneNumber;

    public Order(int id, int userId, LocalDateTime orderDate, double totalAmount, String status,
                 Integer shippingProvince, Integer shippingDistrict, Integer shippingWard,
                 String shippingDetail, List<OrderItem> items, LocalDateTime createdAt,
                 LocalDateTime updatedAt, String userFullName, String userPhoneNumber) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingProvince = shippingProvince;
        this.shippingDistrict = shippingDistrict;
        this.shippingWard = shippingWard;
        this.shippingDetail = shippingDetail;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
    }

    protected Order(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        String orderDateStr = in.readString();
        orderDate = orderDateStr != null ? LocalDateTime.parse(orderDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        totalAmount = in.readDouble();
        status = in.readString();
        shippingProvince = in.readInt() == 0 ? null : in.readInt();
        shippingDistrict = in.readInt() == 0 ? null : in.readInt();
        shippingWard = in.readInt() == 0 ? null : in.readInt();
        shippingDetail = in.readString();
        items = new ArrayList<>();
        in.readList(items, OrderItem.class.getClassLoader());
        String createdAtStr = in.readString();
        createdAt = createdAtStr != null ? LocalDateTime.parse(createdAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        String updatedAtStr = in.readString();
        updatedAt = updatedAtStr != null ? LocalDateTime.parse(updatedAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
        userFullName = in.readString();
        userPhoneNumber = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
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
        dest.writeString(orderDate != null ? orderDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        dest.writeDouble(totalAmount);
        dest.writeString(status);
        dest.writeInt(shippingProvince != null ? shippingProvince : 0);
        dest.writeInt(shippingDistrict != null ? shippingDistrict : 0);
        dest.writeInt(shippingWard != null ? shippingWard : 0);
        dest.writeString(shippingDetail);
        dest.writeList(items);
        dest.writeString(createdAt != null ? createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        dest.writeString(updatedAt != null ? updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        dest.writeString(userFullName);
        dest.writeString(userPhoneNumber);
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Integer getShippingProvince() { return shippingProvince; }
    public Integer getShippingDistrict() { return shippingDistrict; }
    public Integer getShippingWard() { return shippingWard; }
    public String getShippingDetail() { return shippingDetail; }
    public List<OrderItem> getItems() { return items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getUserFullName() { return userFullName; }
    public String getUserPhoneNumber() { return userPhoneNumber; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setShippingProvince(Integer shippingProvince) { this.shippingProvince = shippingProvince; }
    public void setShippingDistrict(Integer shippingDistrict) { this.shippingDistrict = shippingDistrict; }
    public void setShippingWard(Integer shippingWard) { this.shippingWard = shippingWard; }
    public void setShippingDetail(String shippingDetail) { this.shippingDetail = shippingDetail; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

    // Custom TypeAdapter for LocalDateTime
    public static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(value));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return LocalDateTime.parse(dateStr, formatter);
        }
    }
}