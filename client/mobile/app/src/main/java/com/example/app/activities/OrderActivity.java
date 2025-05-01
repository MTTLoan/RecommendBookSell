package com.example.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.OrderItemAdapter;
import com.example.app.models.OrderItem;
import com.example.app.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private TextView tvOrderCode, tvOrderDate, tvStatus, tvShippingAddress, tvTotalAmount;
    private RecyclerView rvOrderItems;
    private Button btnCancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ view
        tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        // Tạo dữ liệu giả theo ví dụ đơn hàng
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(106, 3, 156000, "Cùng con trưởng thành - Mình không thích bị cô lập - Bài học về sự dũng cảm - Dạy trẻ chống lại bạo lực tinh thần - Tránh xa tổn thương", "https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png"));
        items.add(new OrderItem(9, 2, 299000, "Chuyện Con Mèo Dạy Hải Âu Bay", "https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg"));
        items.add(new OrderItem(240, 2, 235000, "Những Con Mèo Sau Bức Tường Hoa", "https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg"));

        Order order = new Order(
                1, // id
                1, // user_id
                "2024-12-10 22:30:48", // order_date
                1536000, // total_amount
                "Chờ giao hàng", // status
                "Phường Linh Tây", // shipping_address_ward
                "Quận Thủ Đức", // shipping_address_district
                "TP.HCM", // shipping_address_province
                "291 Pasteur", // shipping_address
                items,
                "Mai Thị Thanh Loan", // user_full_name (Users)
                "0123 456 789" // user_phone_number (Users)
        );

        // Gán dữ liệu vào view
        tvOrderCode.setText(order.getId());
        tvOrderDate.setText(order.getOrderDate());
        tvStatus.setText(order.getStatus());
        tvShippingAddress.setText("Địa chỉ giao hàng\n" + order.getUserFullName() + "\n" +
                order.getUserPhoneNumber() + "\n" +
                order.getShippingAddress() + ", " +
                order.getShippingAddressWard() + ", " +
                order.getShippingAddressDistrict() + ", " +
                order.getShippingAddressProvince());
        tvTotalAmount.setText(String.format("%,.0fđ", order.getTotalAmount()));

        // Thiết lập RecyclerView
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderItemAdapter adapter = new OrderItemAdapter(order.getItems());
        rvOrderItems.setAdapter(adapter);

        // Sự kiện nút Hủy đơn hàng
        btnCancelOrder.setOnClickListener(v -> {

        });
    }
}