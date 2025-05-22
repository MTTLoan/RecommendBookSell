package com.example.app.models.response;

import com.example.app.models.Book;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookResponse {
    private boolean success;
    private String msg;
    @SerializedName("book")
    private List<Book> books; // Change to List<Book> to match the array

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public List<Book> getBooks() {
        return books; // Return the same list for compatibility
    }
}