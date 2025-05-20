package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.NotificationAdapter;
import com.example.app.models.Notification;
import com.example.app.models.Order;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnItemClickListener {
    private static final String TAG = "NotificationActivity";
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ivReturn = findViewById(R.id.ivReturn);

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(this, notifications, this);
        recyclerView.setAdapter(adapter);

        ivReturn.setOnClickListener(v -> finish());

        String token = AuthUtils.getToken(this);
        if (token != null) {
            fetchNotifications("Bearer " + token);
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)); // Chuyển đến màn hình đăng nhập
            finish();
        }
    }

    private void fetchNotifications(String token) {
        Call<List<Notification>> call = RetrofitClient.getApiService().getNotifications(token);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> fetchedNotifications = response.body();
                    if (fetchedNotifications != null && !fetchedNotifications.isEmpty()) {
                        notifications.clear();
                        notifications.addAll(fetchedNotifications);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Không có thông báo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "onResponse: Error - " + response.message());
                    try {
                        Log.e(TAG, "onResponse: Error Body - " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: Failed to read error body - " + e.getMessage());
                    }
                    Toast.makeText(NotificationActivity.this, "Lỗi khi tải thông báo: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.e(TAG, "onFailure: Connection Error - " + t.getMessage(), t);
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemClick(Notification notification) {
        String token = AuthUtils.getToken(NotificationActivity.this);
        if (token == null) {
            Toast.makeText(NotificationActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NotificationActivity.this, LoginActivity.class));
            finish();
            return;
        }

        Log.d(TAG, "Marking notification as read - ID: " + notification.getId());
        RetrofitClient.getApiService().markAsRead("Bearer " + token, notification.getId()).enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notification marked as read successfully - ID: " + notification.getId());
                    fetchNotifications("Bearer " + token);
                } else {
                    Log.e(TAG, "Mark as read error - Code: " + response.code());
                    try {
                        Log.e(TAG, "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read error body: " + e.getMessage());
                    }
                    if (response.code() == 401) {
                        Toast.makeText(NotificationActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                        AuthUtils.clearToken(NotificationActivity.this);
                        startActivity(new Intent(NotificationActivity.this, LoginActivity.class));
                        finish();
                    } else if (response.code() == 404) {
                        Toast.makeText(NotificationActivity.this, "Thông báo không tồn tại hoặc bạn không có quyền", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NotificationActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e(TAG, "Mark as read failure: " + t.getMessage(), t);
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Kiểm tra orderId và chuyển đến OrderActivity nếu có
        if (notification.getOrderId() > 0) {
            Log.d(TAG, "Fetching order for orderId: " + notification.getOrderId());
            RetrofitClient.getApiService().getOrderById("Bearer " + token, notification.getOrderId()).enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Order order = response.body();
                        Log.d(TAG, "Order fetched successfully - ID: " + order.getId());
                        Intent intent = new Intent(NotificationActivity.this, OrderActivity.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                    } else {
                        Log.e(TAG, "Fetch order error - Code: " + response.code());
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to read error body: " + e.getMessage());
                        }
                        if (response.code() == 401) {
                            Toast.makeText(NotificationActivity.this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                            AuthUtils.clearToken(NotificationActivity.this);
                            startActivity(new Intent(NotificationActivity.this, LoginActivity.class));
                            finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(NotificationActivity.this, "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NotificationActivity.this, "Lỗi khi tải đơn hàng: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Log.e(TAG, "Fetch order failure: " + t.getMessage(), t);
                    Toast.makeText(NotificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}