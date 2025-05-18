package com.example.app.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private int userId;
    private String orderDate; // Thay đổi từ LocalDateTime sang String
    private double totalAmount;
    private String status;
    private int shippingProvince;
    private int shippingDistrict;
    private int shippingWard;
    private String shippingDetail;
    private List<OrderItem> items;

    public Order() {}

    public Order(int id, int userId, String orderDate, double totalAmount, String status,
                 int shippingProvince, int shippingDistrict, int shippingWard, String shippingDetail,
                 List<OrderItem> items) {
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
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getShippingProvince() {
        return shippingProvince;
    }

    public void setShippingProvince(int shippingProvince) {
        this.shippingProvince = shippingProvince;
    }

    public int getShippingDistrict() {
        return shippingDistrict;
    }

    public void setShippingDistrict(int shippingDistrict) {
        this.shippingDistrict = shippingDistrict;
    }

    public int getShippingWard() {
        return shippingWard;
    }

    public void setShippingWard(int shippingWard) {
        this.shippingWard = shippingWard;
    }

    public String getShippingDetail() {
        return shippingDetail;
    }

    public void setShippingDetail(String shippingDetail) {
        this.shippingDetail = shippingDetail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}