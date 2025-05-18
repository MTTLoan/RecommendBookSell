package com.example.app.models.request;

public class ChangePasswordRequest {
    private String identifier;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordRequest(String identifier, String oldPassword, String newPassword, String confirmPassword) {
        this.identifier = identifier;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
