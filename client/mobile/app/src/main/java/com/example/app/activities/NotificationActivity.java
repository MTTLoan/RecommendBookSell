package com.example.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.NotificationAdapter;
import com.example.app.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.OnItemClickListener {
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

        // Dữ liệu mẫu (thay bằng dữ liệu từ API)
        notifications = new ArrayList<>();
        notifications.add(new Notification(1, 1, "Đặt hàng thành công",
                "Đơn hàng SPXVN05578654567 đã được đặt thành công. Ngày giao hàng dự kiến là 10-04-2025. Nhấn vào đây để xem chi tiết đơn hàng.",
                false, "11:23 09-04-2025"));
        notifications.add(new Notification(2, 1, "Giao hàng thành công",
                "Đơn hàng SPXVN0578654567 đã giao thành công đến bạn. Nếu bạn chưa nhận hàng hay gặp vấn đề gì, hãy nhấn Trả hàng/Hoàn tiền trước ngày 10-03-2025.",
                true, "11:23 09-03-2025"));
        notifications.add(new Notification(3, 1, "Đã gửi cho đơn vị vận chuyển",
                "Đơn hàng SPXVN05578654567 đã được gửi cho đơn vị vận chuyển. Ngày giao hàng dự kiến là 10-03-2025. Nhấn vào đây để xem chi tiết đơn hàng.",
                true, "11:23 09-03-2025"));

        adapter = new NotificationAdapter(this, notifications, this);
        recyclerView.setAdapter(adapter);

        // Sự kiện nhấn nút quay lại
        // Back button
        ivReturn.setOnClickListener(v -> finish());
    }

    @Override
    public void onItemClick(Notification notification) {
        Toast.makeText(this, "Đã nhấn: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        // Có thể mở Activity chi tiết hoặc xử lý thêm
    }
}