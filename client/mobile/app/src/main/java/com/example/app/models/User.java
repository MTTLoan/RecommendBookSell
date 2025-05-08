package com.example.app.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private String id;
    private String username;
    private String fullName;
    @SerializedName("email")
    private String email;
    private String phoneNumber;
    private String role;
    private String token;

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}