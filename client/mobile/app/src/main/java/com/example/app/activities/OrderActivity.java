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
        items.add(new OrderItem(106, 3, 156000, "Cùng con trưởng thành - Mình không thích bị cô lập - Bài học về sự dũng cảm - Dạy trẻ chống lại bạo lực tinh thần - Tránh xa tổn thương", "https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png"));
        items.add(new OrderItem(9, 2, 299000, "Chuyện Con Mèo Dạy Hải Âu Bay", "https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg"));
        items.add(new OrderItem(240, 2, 235000, "Những Con Mèo Sau Bức Tường Hoa", "https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg"));

        Order order = new Order(
                1, // id
                1, // user_id
                "2024-12-10 22:30:48", // order_date
                1536000, // total_amount
                "Chờ giao hàng", // status
                28756, // shipping_address_ward
                829, // shipping_address_district
                83, // shipping_address_province
                "291 Pasteur", // shipping_address
                items,
                "Mai Thị Thanh Loan", // user_full_name (User)
                "0123 456 789" // user_phone_number (User)
        );

        // Định dạng ngày giờ
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = "";
        try {
            formattedDate = outputFormat.format(inputFormat.parse(order.getOrderDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gán dữ liệu vào view
        tvTitle.setText("THÔNG TIN ĐƠN HÀNG");
        tvStatus.setText("Trạng thái: " + (order.getStatus().equals("completed") ? "Hoàn thành" : order.getStatus()));
        tvOrderCode.setText(String.valueOf(order.getId()));
        tvOrderDate.setText(formattedDate);
        tvShippingAddress.setText(order.getUserFullName() + "\n" +
                order.getUserPhoneNumber() + "\n" +
                order.getShippingDetailedAddress() + ", " +
                order.getShippingAddressWardCode() + ", " +
                order.getShippingAddressDistrictCode() + ", " +
                order.getShippingAddressProvinceCode());

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