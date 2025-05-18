package com.example.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class  User {
    private Integer id;
    private String username;
    private String fullName;
    @SerializedName("email")
    private String email;
    private String phoneNumber;
    private Integer addressProvince;
    private Integer addressDistrict;
    private Integer addressWard;
    private String addressDetail;
    private String password;
    private String role;
    private Date birthday;
    private Date createdAt;
    private Date updatedAt;
    private String avatar;
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(Integer addressProvince) {
        this.addressProvince = addressProvince;
    }

    public Integer getAddressDistrict() {
        return addressDistrict;
    }

    public void setAddressDistrict(Integer addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public Integer getAddressWard() {
        return addressWard;
    }

    public void setAddressWard(Integer addressWard) {
        this.addressWard = addressWard;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}