package com.example.app.models.response;

import com.example.app.models.Category;

import java.util.List;

public class CategoryResponse {
    private boolean success;
    private String msg;
    private List<Category> categories; // Danh sách các danh mục trả về

    public boolean isSuccess() {
        return success;
    }
    public String getMsg() {
        return msg;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}