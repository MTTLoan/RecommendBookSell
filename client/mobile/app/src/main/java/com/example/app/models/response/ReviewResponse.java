package com.example.app.models.response;

import com.example.app.models.Review;
import java.util.List;

public class ReviewResponse {
    private boolean success;
    private String msg;
    private List<Review> reviews;

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}