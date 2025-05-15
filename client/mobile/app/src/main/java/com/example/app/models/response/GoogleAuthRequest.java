package com.example.app.models.response;

import com.google.gson.annotations.SerializedName;

public class GoogleAuthRequest {
    @SerializedName("googleId")
    private String googleId;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("photoUrl")
    private String photoUrl;

    public GoogleAuthRequest(String googleId, String email, String fullName, String photoUrl) {
        this.googleId = googleId;
        this.email = email;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
    }
}