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

    public class OtpRequest {
        private String email;
        private String otp;

        public OtpRequest(String email, String otp) {
            this.email = email;
            this.otp = otp;
        }
    }

    public class ResendOtpRequest {
        private String email;

        public ResendOtpRequest(String email) {
            this.email = email;
        }
    }

    public class OtpResponse {
        private boolean success;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    // Forgot Password
    public class ForgotPasswordRequest {
        private String email;

        public ForgotPasswordRequest(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    // Reset Password
    public class ResetPasswordRequest {
        private String email;
        private String newPassword;
        private String confirmPassword;
        private String otp;

        public ResetPasswordRequest(String email, String newPassword, String confirmPassword, String otp) {
            this.email = email;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
            this.otp = otp;
        }

        public String getEmail() {
            return email;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public String getOtp() {
            return otp;
        }
    }

    @GET("api/notifications")
    Call<List<Notification>> getNotifications();

    // Login
    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    Call<User> login(@Body UserLoginRequest request);

    @POST("/api/auth/login/google")
    Call<User> loginWithGoogle(@Body UserLoginRequest request);

    // Verify Email for Registration
    @Headers("Content-Type: application/json")
    @POST("/api/verify_email/verify")
    Call<OtpResponse> verifyOtp(@Body OtpRequest request);

    // Resend OTP
    @POST("/api/resend-otp")
    Call<OtpResponse> resendOtp(@Body ResendOtpRequest request);

    // Forgot Password
    @Headers("Content-Type: application/json")
    @POST("/api/forgot_password/forgot-password")
    Call<OtpResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @Headers("Content-Type: application/json")
    @POST("/api/forgot_password/reset-password")
    Call<OtpResponse> resetPassword(@Body ResetPasswordRequest request);


}