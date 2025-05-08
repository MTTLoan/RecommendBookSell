package com.example.app.models;

import java.util.List;
import java.io.Serializable;

public class Order implements Serializable {
    private int id;
    private int userId;
    private String orderDate;
    private double totalAmount;
    private String status;
    private Integer shippingAddressWardCode;
    private Integer shippingAddressDistrictCode;
    private Integer shippingAddressProvinceCode;
    private String shippingDetailedAddress;
    private List<OrderItem> items;
    private String userFullName;
    private String userPhoneNumber;

    public Order(int id, int userId, String orderDate, double totalAmount, String status, Integer shippingAddressWardCode, Integer shippingAddressDistrictCode, Integer shippingAddressProvinceCode, String shippingDetailedAddress, List<OrderItem> items, String userFullName, String userPhoneNumber) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddressWardCode = shippingAddressWardCode;
        this.shippingAddressDistrictCode = shippingAddressDistrictCode;
        this.shippingAddressProvinceCode = shippingAddressProvinceCode;
        this.shippingDetailedAddress = shippingDetailedAddress;
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

    public Integer getShippingAddressWardCode() {
        return shippingAddressWardCode;
    }

    public Integer getShippingAddressDistrictCode() {
        return shippingAddressDistrictCode;
    }

    public Integer getShippingAddressProvinceCode() {
        return shippingAddressProvinceCode;
    }

    public String getShippingDetailedAddress() {
        return shippingDetailedAddress;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setShippingAddressWardCode(Integer shippingAddressWardCode) {
        this.shippingAddressWardCode = shippingAddressWardCode;
    }

    public void setShippingAddressDistrictCode(Integer shippingAddressDistrictCode) {
        this.shippingAddressDistrictCode = shippingAddressDistrictCode;
    }

    public void setShippingAddressProvinceCode(Integer shippingAddressProvinceCode) {
        this.shippingAddressProvinceCode = shippingAddressProvinceCode;
    }

    public void setShippingDetailedAddress(String shippingDetailedAddress) {
        this.shippingDetailedAddress = shippingDetailedAddress;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}