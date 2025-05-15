package com.example.app.models.request;

public class RegisterRequest {
    String username, fullName, email, phoneNumber, password, confirm_password;

    public RegisterRequest(String username, String fullName, String email, String phoneNumber, String password, String confirm_password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirm_password = confirm_password;
    }
}