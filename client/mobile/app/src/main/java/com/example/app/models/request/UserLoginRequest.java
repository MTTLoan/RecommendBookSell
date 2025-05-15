package com.example.app.models.request;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequest {
    @SerializedName("identifier")
    private String identifier;

    @SerializedName("password")
    private String password;

    @SerializedName("idToken")
    private String idToken;

    public UserLoginRequest(String email, String password, boolean isNormalLogin) {
        this.identifier = email;
        this.password = password;
        this.idToken = null;
    }

    public UserLoginRequest(String email, String idToken) {
        this.identifier = email;
        this.idToken = idToken;
        this.password = null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public String getIdToken() {
        return idToken;
    }
}
