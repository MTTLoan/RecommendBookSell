package com.example.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.OrderItemAdapter;
import com.example.app.models.Order;
import com.example.app.models.OrderItem;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {
    private TextView tvTitle, tvStatus, tvOrderCode, tvOrderDate, tvShippingAddress, tvTotalGoods, tvShippingFee, tvTotal;
    private RecyclerView rvOrderItems;
    private Button btnCancelOrder;
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ view
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvTotalGoods = findViewById(R.id.tvTotalGoods);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotal = findViewById(R.id.tvTotal);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        ivReturn = findViewById(R.id.ivReturn);

        // Tạo dữ liệu giả theo ví dụ đơn hàng
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(106, 3, 156000));
        items.add(new OrderItem(9, 2, 299000));
        items.add(new OrderItem(240, 2, 235000));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime orderDate = LocalDateTime.parse("2025-05-08 12:00:00", formatter);
        LocalDateTime createdAt = LocalDateTime.parse("2025-05-08 12:00:00", formatter);
        LocalDateTime updatedAt = LocalDateTime.parse("2025-05-08 12:00:00", formatter);

        Order order = new Order();

        // Gán dữ liệu vào view
        tvTitle.setText("THÔNG TIN ĐƠN HÀNG");
        tvStatus.setText("Trạng thái: " + (order.getStatus().equals("completed") ? "Hoàn thành" : order.getStatus()));
        tvOrderCode.setText(String.valueOf(order.getId()));
//        tvOrderDate.setText(order.getOrderDate().format(formatter));
        tvShippingAddress.setText(
                order.getShippingDetail() + ", " +
                order.getShippingWard() + ", " +
                order.getShippingDistrict() + ", " +
                order.getShippingProvince());

        // Tính tổng tiền hàng, phí vận chuyển, thành tiền
        double totalGoods = order.getTotalAmount();
        double shippingFee = 20000; // Giả định phí vận chuyển
        double total = totalGoods + shippingFee;

        tvTotalGoods.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", totalGoods));
        tvShippingFee.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", shippingFee));
        tvTotal.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", total));

        // Thiết lập RecyclerView
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderItemAdapter adapter = new OrderItemAdapter(order.getItems());
        rvOrderItems.setAdapter(adapter);

        // Sự kiện nút Hủy đơn hàng
        btnCancelOrder.setOnClickListener(v -> {
            // Xử lý hủy đơn hàng
        });

        // Sự kiện nút quay lại
        ivReturn.setOnClickListener(v -> finish());
    }
}