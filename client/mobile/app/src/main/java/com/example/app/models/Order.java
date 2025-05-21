package com.example.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order implements Parcelable {
    private int id;
    private int userId;
    private String orderDate;
    private double totalAmount;
    private int shippingCost;
    private String status;
    private int shippingProvince;
    private int shippingDistrict;
    private int shippingWard;
    private String shippingDetail;
    private List<OrderItem> items;
    public Order(){}

    public Order(int id, int userId, String orderDate, double totalAmount, int shippingCost, String status,
                 int shippingProvince, int shippingDistrict, int shippingWard,
                 String shippingDetail, List<OrderItem> items) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingCost = shippingCost;
        this.status = status;
        this.shippingProvince = shippingProvince;
        this.shippingDistrict = shippingDistrict;
        this.shippingWard = shippingWard;
        this.shippingDetail = shippingDetail;
        this.items = items;
    }

    protected Order(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        orderDate = in.readString();
        totalAmount = in.readDouble();
        shippingCost = in.readInt();
        status = in.readString();
        shippingProvince = in.readInt();
        shippingDistrict = in.readInt();
        shippingWard = in.readInt();
        shippingDetail = in.readString();
        items = new ArrayList<>();
        in.readList(items, OrderItem.class.getClassLoader());
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
        dest.writeString(orderDate);
        dest.writeDouble(totalAmount);
        dest.writeDouble(shippingCost);
        dest.writeString(status);
        dest.writeInt(shippingProvince);
        dest.writeInt(shippingDistrict);
        dest.writeInt(shippingWard);
        dest.writeString(shippingDetail);
        dest.writeList(items);
    }

    public int getShippingCost() {
        return shippingCost;
    }

    // Getters
    public int getId() { return id; }

    public void setShippingCost(int shippingCost) {
        this.shippingCost = shippingCost;
    }

    public int getUserId() { return userId; }
    public String getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public int getShippingProvince() { return shippingProvince; }
    public int getShippingDistrict() { return shippingDistrict; }
    public int getShippingWard() { return shippingWard; }
    public String getShippingDetail() { return shippingDetail; }
    public List<OrderItem> getItems() { return items; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setShippingProvince(int shippingProvince) { this.shippingProvince = shippingProvince; }
    public void setShippingDistrict(int shippingDistrict) { this.shippingDistrict = shippingDistrict; }
    public void setShippingWard(int shippingWard) { this.shippingWard = shippingWard; }
    public void setShippingDetail(String shippingDetail) { this.shippingDetail = shippingDetail; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Phương thức định dạng ngày
    public String getFormattedOrderDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(orderDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return orderDate; // Trả về nguyên bản nếu lỗi
        }
    }
}