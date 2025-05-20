package com.example.app.network;

import com.example.app.models.Cart;
import com.example.app.models.HasReviewsResponse;
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
import com.example.app.models.response.BookDetailResponse;
import com.example.app.models.response.ForgotPasswordResponse;
import com.example.app.models.request.GoogleAuthRequest;
import com.example.app.models.response.LogoutResponse;
import com.example.app.models.response.OrderHistoryResponse;
import com.example.app.models.response.OtpResponse;
import com.example.app.models.response.ResetPasswordResponse;
import com.example.app.models.response.ChangePasswordResponse;
import com.example.app.models.request.ChangePasswordRequest;
import com.example.app.models.response.UserResponse;
import com.example.app.models.response.ReviewResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.models.response.BookResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @POST("/api/auth/change-password")
    Call<ChangePasswordResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
    @GET("/api/auth/profile")
    Call<UserResponse> getUserProfile(@Header("Authorization") String token);

    @PUT("/api/auth/update-profile")
    Call<UserResponse> updateUserProfile(@Header("Authorization") String token, @Body User user);

    @GET("notifications")
    Call<List<Notification>> getNotifications(@Header("Authorization") String token);

    @PUT("notifications/{id}")
    Call<Notification> markAsRead(@Header("Authorization") String token, @Path("id") int id);

    @GET("orders/history")
    Call<OrderHistoryResponse> getOrderHistory(@Header("Authorization") String authorization);

    @GET("orders/{orderId}")
    Call<Order> getOrderById(@Header("Authorization") String authorization, @Path("orderId") int orderId);

    @PUT("orders/{id}/status")
    Call<Order> updateOrderStatus(@Header("Authorization") String authorization, @Path("id") int orderId, @Body StatusUpdateRequest request);

    @POST("reviews")
    Call<Review> submitReview(@Header("Authorization") String authorization, @Body Review review);

    @GET("/api/books/all-book")
    Call<BookResponse> getBooks(@Query("categoryId") Integer categoryId);
    @GET("/api/books/book-detail/{id}")
    Call<BookDetailResponse> getBookDetail(@Path("id") int bookId);
    @GET("/api/books/book-detail/{bookId}/reviews")
    Call<ReviewResponse> getBookReviews(@Path("bookId") int bookId);

    @GET("/api/categories/all-categories")
    Call<CategoryResponse> getCategories();

    @GET("reviews/{orderId}/reviews")
    Call<HasReviewsResponse> getReviewsForOrder(@Header("Authorization") String token, @Path("orderId") int orderId);

    @POST("/api/carts")
    Call<Cart> addToCart(@Header("Authorization") String authorization, @Body Cart cart);

    @GET("/api/carts")
    Call<Cart> getCart(@Header("Authorization") String authorization);

    @PUT("/api/carts")
    Call<Cart> updateCart(@Header("Authorization") String authorization, @Body Cart cart);

    @DELETE("/api/carts/items/{bookId}")
    Call<Cart> deleteCartItem(@Header("Authorization") String authorization, @Path("bookId") int bookId);

}