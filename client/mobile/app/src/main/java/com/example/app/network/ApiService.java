package com.example.app.network;

import com.example.app.models.Notification;
import com.example.app.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    public class UserLoginRequest {
        private final String email;
        private final String password;

        public UserLoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getter (nếu cần)
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }


    @GET("api/notifications")
    Call<List<Notification>> getNotifications();

    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    Call<User> login(@Body UserLoginRequest request);


    @POST("google-login")
    Call<User> loginWithGoogle(@Body UserLoginRequest request);
}