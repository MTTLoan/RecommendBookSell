package com.example.app.models.response;

public class LogoutResponse {
    private String message;
    private String msg;

    public String getMessage() {
        return message != null ? message : msg;
    }

    public boolean isSuccess() {
        return (message != null && message.contains("thành công")) || (msg != null && msg.contains("thành công"));
    }
}
