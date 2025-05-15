package com.example.app.activities;

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
import com.example.app.network.RetrofitClient;

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

        // Sự kiện nhấn nút quay lại
        ivReturn.setOnClickListener(v -> finish());

        // Lấy dữ liệu từ API với userId ảo là 2
        fetchNotifications(2);

        Log.d(TAG, "onCreate: Initialized NotificationActivity with userId=2 at " + java.time.LocalDateTime.now());
    }

    private void fetchNotifications(int userId) {
        Call<List<Notification>> call = RetrofitClient.getApiService().getNotifications(userId);
        Log.d(TAG, "fetchNotifications: Request URL - " + call.request().url().toString());
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                Log.d(TAG, "onResponse: Response Code - " + response.code());
                Log.d(TAG, "onResponse: Raw Response - " + response.raw().toString());
                if (response.isSuccessful()) {
                    List<Notification> fetchedNotifications = response.body();
                    Log.d(TAG, "onResponse: Fetched Notifications - " + (fetchedNotifications != null ? fetchedNotifications.toString() : "null"));
                    if (fetchedNotifications != null && !fetchedNotifications.isEmpty()) {
                        notifications.clear();
                        notifications.addAll(fetchedNotifications);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onResponse: Updated notifications list with " + notifications.size() + " items");
                        Toast.makeText(NotificationActivity.this, "Đã tải " + notifications.size() + " thông báo cho userId: " + userId, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onResponse: No notifications found for userId: " + userId);
                        Toast.makeText(NotificationActivity.this, "Không có thông báo cho userId: " + userId, Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "onItemClick: Marking notification as read - ID: " + notification.getId());
        RetrofitClient.getApiService().markAsRead(notification.getId()).enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Marked as read - " + response.body().getTitle());
                    Toast.makeText(NotificationActivity.this, "Đã đánh dấu: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
                    fetchNotifications(notification.getUserId()); // Làm mới danh sách
                } else {
                    Log.e(TAG, "onResponse: Error marking as read - " + response.message());
                    Toast.makeText(NotificationActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e(TAG, "onFailure: Error marking as read - " + t.getMessage(), t);
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}