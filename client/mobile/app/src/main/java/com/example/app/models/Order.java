package com.example.app.models;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private String orderDate;
    private double totalAmount;
    private String status;
    private String shippingAddressWard;
    private String shippingAddressDistrict;
    private String shippingAddressProvince;
    private String shippingAddress;
    private List<OrderItem> items;
    private String userFullName; // Lấy từ bảng User
    private String userPhoneNumber; // Lấy từ bảng User

    public Order(int id, int userId, String orderDate, double totalAmount, String status,
                 String shippingAddressWard, String shippingAddressDistrict, String shippingAddressProvince,
                 String shippingAddress, List<OrderItem> items, String userFullName, String userPhoneNumber) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddressWard = shippingAddressWard;
        this.shippingAddressDistrict = shippingAddressDistrict;
        this.shippingAddressProvince = shippingAddressProvince;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getShippingAddressWard() {
        return shippingAddressWard;
    }

    public String getShippingAddressDistrict() {
        return shippingAddressDistrict;
    }

    public String getShippingAddressProvince() {
        return shippingAddressProvince;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }
}