package com.example.app.network;

import com.example.app.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    class RegisterRequest {
        String username, fullName, email, phoneNumber, password, confirm_password;

        public RegisterRequest(String username, String fullName, String email, String phoneNumber, String password, String confirm_password) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
            this.confirm_password = confirm_password;
        }
    }
}