package com.example.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.adapters.OrderConfirmItemAdapter;
import com.example.app.models.Order;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView tvUserFullName, tvUserPhoneNumber, tvShippingAddress, tvTotalAmount;
    private RecyclerView rvOrderItems;
    private Button btnPlaceOrder;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Ánh xạ view
        tvUserFullName = findViewById(R.id.tv_user_full_name);
        tvUserPhoneNumber = findViewById(R.id.tv_user_phone_number);
        tvShippingAddress = findViewById(R.id.tv_shipping_address);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rvOrderItems = findViewById(R.id.rv_order_items);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        // Lấy dữ liệu Order từ Intent
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin người dùng
        tvUserFullName.setText("Họ tên: " + order.getUserFullName());
        tvUserPhoneNumber.setText("Số điện thoại: " + order.getUserPhoneNumber());

        // Hiển thị danh sách sản phẩm
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderConfirmItemAdapter adapter = new OrderConfirmItemAdapter(order.getItems());
        rvOrderItems.setAdapter(adapter);

        // Hiển thị tổng giá trị
        tvTotalAmount.setText(String.format("Tổng: %,d VNĐ", (int) order.getTotalAmount()));

        // Lấy thông tin địa chỉ từ Province Open API
        fetchAddressDetails();

        // Xử lý nút Đặt hàng
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void fetchAddressDetails() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            String provinceUrl = "https://provinces.open-api.vn/api/p/" + order.getShippingAddressProvinceCode();
            String districtUrl = "https://provinces.open-api.vn/api/d/" + order.getShippingAddressDistrictCode();
            String wardUrl = "https://provinces.open-api.vn/api/w/" + order.getShippingAddressWardCode();

            try {
                // Lấy tên tỉnh
                Request provinceRequest = new Request.Builder().url(provinceUrl).build();
                Response provinceResponse = client.newCall(provinceRequest).execute();
                String provinceName = new JSONObject(provinceResponse.body().string()).getString("name");

                // Lấy tên quận/huyện
                Request districtRequest = new Request.Builder().url(districtUrl).build();
                Response districtResponse = client.newCall(districtRequest).execute();
                String districtName = new JSONObject(districtResponse.body().string()).getString("name");

                // Lấy tên phường/xã
                Request wardRequest = new Request.Builder().url(wardUrl).build();
                Response wardResponse = client.newCall(wardRequest).execute();
                String wardName = new JSONObject(wardResponse.body().string()).getString("name");

                // Cập nhật UI trên main thread
                runOnUiThread(() -> {
                    String fullAddress = order.getShippingDetailedAddress() + ", " + wardName + ", " + districtName + ", " + provinceName;
                    tvShippingAddress.setText("Địa chỉ: " + fullAddress);
                });

            } catch (IOException | org.json.JSONException e) {
                runOnUiThread(() -> tvShippingAddress.setText("Địa chỉ: " + order.getShippingDetailedAddress() + " (Không lấy được thông tin địa chỉ)"));
            }
        }).start();
    }

    private void placeOrder() {
        // Gửi yêu cầu đặt hàng lên server (dùng Retrofit)
        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}