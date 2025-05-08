package com.example.app.network;

import com.example.app.models.Notification;
import com.example.app.models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    @POST("forgot_password/forgot-password")
    Call<ForgotPasswordResponse> sendPasswordResetOTP(@Body ForgotPasswordRequest request);

    @POST("forgot_password/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

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

    class UserLoginRequest {
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

    class OtpRequest {
        private String email;
        private String otp;

        public OtpRequest(String email, String otp) {
            this.email = email;
            this.otp = otp;
        }
    }

    class ResendOtpRequest {
        private String email;

        public ResendOtpRequest(String email) {
            this.email = email;
        }
    }

    class ForgotPasswordRequest {
        private String email;

        public ForgotPasswordRequest(String email) {
            this.email = email;
        }
    }

    class ResetPasswordRequest {
        private String email;
        private String otp;
        private String newPassword;
        private String confirmPassword;

        public ResetPasswordRequest(String email, String otp, String newPassword, String confirmPassword) {
            this.email = email;
            this.otp = otp;
            this.newPassword = newPassword;
            this.confirmPassword = confirmPassword;
        }
    }

    class OtpResponse {
        private boolean success;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    class ForgotPasswordResponse {
        private String message;
        private Object otpRecord;

        public String getMessage() {
            return message;
        }

        public Object getOtpRecord() {
            return otpRecord;
        }
    }

    class ResetPasswordResponse {
        private String message;

        public String getMessage() {
            return message;
        }
    }

    // Logout Response Class
    class LogoutResponse {
        private String message;
        private String msg;

        public String getMessage() {
            return message != null ? message : msg;
        }

        public boolean isSuccess() {
            return (message != null && message.contains("thành công")) || (msg != null && msg.contains("thành công"));
        }
    }

    @POST("/api/auth/logout")
    Call<LogoutResponse> logout(@Header("Authorization") String token);

    @GET("api/notifications")
    Call<List<Notification>> getNotifications();

    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    Call<User> login(@Body UserLoginRequest request);

    @POST("/api/auth/login/google")
    Call<User> loginWithGoogle(@Body UserLoginRequest request);

    @Headers("Content-Type: application/json")
    @POST("/api/verify_email/verify")
    Call<OtpResponse> verifyOtp(@Body OtpRequest request);

    @POST("/api/resend-otp")
    Call<OtpResponse> resendOtp(@Body ResendOtpRequest request);

}