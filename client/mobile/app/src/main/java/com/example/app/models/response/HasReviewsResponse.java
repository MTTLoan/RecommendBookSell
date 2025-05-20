package com.example.app.models;

import com.google.gson.annotations.SerializedName;

public class HasReviewsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("hasReviews")
    private boolean hasReviews;

    @SerializedName("message")
    private String message; // Xử lý lỗi nếu có

    public boolean isSuccess() {
        return success;
    }

    public boolean hasReviews() {
        return hasReviews;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setHasReviews(boolean hasReviews) {
        this.hasReviews = hasReviews;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}