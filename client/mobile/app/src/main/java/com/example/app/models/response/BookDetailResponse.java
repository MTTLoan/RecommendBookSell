package com.example.app.models.response;

import com.example.app.models.Book;

public class BookDetailResponse {
    private boolean success;
    private String msg;
    private Book book;

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public Book getBook() {
        return book;
    }

}