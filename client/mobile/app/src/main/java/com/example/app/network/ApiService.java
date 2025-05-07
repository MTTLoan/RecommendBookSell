package com.example.app.network;

import com.example.app.models.Notification;
import com.example.app.models.User;
import com.google.gson.annotations.SerializedName;

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
        @SerializedName("email")
        private String email;

        @SerializedName("password")
        private String password; // Dùng cho đăng nhập thông thường

        @SerializedName("idToken")
        private String idToken;  // Dùng cho đăng nhập Google

        // Constructor cho đăng nhập thông thường (email, password)
        public UserLoginRequest(String email, String password, boolean isNormalLogin) {
            this.email = email;
            this.password = password;
            this.idToken = null; // Đảm bảo idToken không được gửi
        }

        // Constructor cho đăng nhập Google (email, idToken)
        public UserLoginRequest(String email, String idToken) {
            this.email = email;
            this.idToken = idToken;
            this.password = null; // Đảm bảo password không được gửi
        }

        // Getters
        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getIdToken() {
            return idToken;
        }
    }

    @GET("api/notifications")
    Call<List<Notification>> getNotifications();

    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    Call<User> login(@Body UserLoginRequest request);


    @POST("/api/auth/login/google")
    Call<User> loginWithGoogle(@Body UserLoginRequest request);
}