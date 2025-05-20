package com.example.app.models.response;

public class ForgotPasswordResponse {
    private String message;
    private Object otpRecord;

    public String getMessage() {
        return message;
    }

    public Object getOtpRecord() {
        return otpRecord;
    }
}
