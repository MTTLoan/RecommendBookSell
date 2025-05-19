package com.example.app.network;

import com.example.app.models.Notification;
import com.example.app.models.Order;
import com.example.app.models.Review;
import com.example.app.models.User;
import com.example.app.models.request.ForgotPasswordRequest;
import com.example.app.models.request.OtpRequest;
import com.example.app.models.request.RegisterRequest;
import com.example.app.models.request.ResendOtpRequest;
import com.example.app.models.request.ResetPasswordRequest;
import com.example.app.models.request.StatusUpdateRequest;
import com.example.app.models.request.UserLoginRequest;
import com.example.app.models.response.ForgotPasswordResponse;
import com.example.app.models.response.GoogleAuthRequest;
import com.example.app.models.response.LogoutResponse;
import com.example.app.models.response.OrderHistoryResponse;
import com.example.app.models.response.OtpResponse;
import com.example.app.models.response.ResetPasswordResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    @POST("forgot_password/forgot-password")
    Call<ForgotPasswordResponse> sendPasswordResetOTP(@Body ForgotPasswordRequest request);

    @POST("forgot_password/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("/api/auth/logout")
    Call<LogoutResponse> logout(@Header("Authorization") String token);

    @POST("/api/auth/login")
    @Headers("Content-Type: application/json")
    Call<User> login(@Body UserLoginRequest request);

    @POST("/api/auth/googleauth")
    @Headers("Content-Type: application/json")
    Call<User> loginWithGoogle(@Body GoogleAuthRequest request);
    @Headers("Content-Type: application/json")
    @POST("/api/verify_email/verify")
    Call<OtpResponse> verifyOtp(@Body OtpRequest request);

    @POST("/api/resend-otp")
    Call<OtpResponse> resendOtp(@Body ResendOtpRequest request);

    @GET("notifications")
    Call<List<Notification>> getNotifications(@Query("userId") int userId);

    @PUT("notifications/{id}")
    Call<Notification> markAsRead(@Path("id") int id);

    @GET("orders/history")
    Call<OrderHistoryResponse> getOrderHistory(@Header("Authorization") String authorization);

    @PUT("orders/{id}/status")
    Call<Order> updateOrderStatus(@Header("Authorization") String authorization, @Path("id") int orderId, @Body StatusUpdateRequest request);

    @POST("reviews")
    Call<Review> submitReview(@Header("Authorization") String authorization, @Body Review review);
}