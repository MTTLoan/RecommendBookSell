package com.example.app.models.response;

import com.example.app.models.User;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("message")
    private String message;
    private boolean success;
    private String avatar; // URL của avatar

    @SerializedName("user")
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setUser(User user) {
        this.user = user;
    }
}