package com.example.app.models.response;

import com.example.app.models.Order;

import java.util.List;
import java.util.Map;

public class OrderHistoryResponse {
    private boolean success;
    private Map<String, List<Order>> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, List<Order>> getData() {
        return data;
    }

    public void setData(Map<String, List<Order>> data) {
        this.data = data;
    }
}