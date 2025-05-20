package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.OrderItemAdapter;
import com.example.app.models.HasReviewsResponse;
import com.example.app.models.Order;
import com.example.app.models.request.StatusUpdateRequest;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private TextView tvTitle, tvStatus, tvOrderCode, tvOrderDate, tvShippingAddress, tvTotalGoods, tvShippingFee, tvTotal;
    private RecyclerView rvOrderItems;
    private Button btnCancelOrder, btnReceived, btnReturn, btnRate;
    private ImageView ivReturn;
    private Order order;
    private String oldStatus;
    private ApiService apiService;
    private String token;

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
        btnReceived = findViewById(R.id.btnReceived);
        btnReturn = findViewById(R.id.btnReturn);
        btnRate = findViewById(R.id.btnRate);
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService và token
        apiService = RetrofitClient.getApiService();
        token = AuthUtils.getToken(this);

        // Lấy Order từ Intent
        order = getIntent().getParcelableExtra("order");
        if (order == null) {
            Log.e("OrderActivity", "Order is null");
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lưu trạng thái ban đầu
        oldStatus = order.getStatus();

        // Hiển thị thông tin đơn hàng
        displayOrderDetails();

        // Thiết lập các nút dựa trên trạng thái
        setupButtons();

        // Sự kiện nút quay lại
        ivReturn.setOnClickListener(v -> {
            // Gửi broadcast để làm mới danh sách đơn hàng
            Intent intent = new Intent("com.example.app.REFRESH_ORDERS");
            intent.putExtra("status", order.getStatus()); // Trạng thái hiện tại
            intent.putExtra("oldStatus", oldStatus); // Trạng thái ban đầu
            LocalBroadcastManager.getInstance(OrderActivity.this).sendBroadcast(intent);
            Log.d("OrderActivity", "Sent refresh broadcast on back - newStatus: " + order.getStatus() + ", oldStatus: " + oldStatus);
            finish(); // Quay lại OrderHistoryFragment
        });
    }

    private void displayOrderDetails() {
        tvTitle.setText("THÔNG TIN ĐƠN HÀNG");
        tvStatus.setText("Trạng thái: " + order.getStatus());
        tvOrderCode.setText(String.valueOf(order.getId()));
        tvOrderDate.setText(order.getFormattedOrderDate());
        tvShippingAddress.setText(String.format("%s, Phường %d, Quận %d, Tỉnh %d",
                order.getShippingDetail(), order.getShippingWard(), order.getShippingDistrict(), order.getShippingProvince()));

        // Tính tổng tiền hàng
        double totalGoods = order.getTotalAmount();
        double shippingFee = 20000; // Giả định phí vận chuyển
        double total = totalGoods + shippingFee;

        tvTotalGoods.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", totalGoods));
        tvShippingFee.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", shippingFee));
        tvTotal.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", total));

        // Thiết lập RecyclerView cho danh sách sản phẩm
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderItemAdapter adapter = new OrderItemAdapter(order.getItems());
        rvOrderItems.setAdapter(adapter);
    }

    private void setupButtons() {
        // Ẩn tất cả các nút mặc định
        btnCancelOrder.setVisibility(View.GONE);
        btnReceived.setVisibility(View.GONE);
        btnReturn.setVisibility(View.GONE);
        btnRate.setVisibility(View.GONE);

        // Thiết lập hành động dựa trên trạng thái
        switch (order.getStatus()) {
            case "Đang đóng gói":
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setOnClickListener(v -> showConfirmDialog(
                        "Xác nhận hủy đơn hàng",
                        "Bạn có chắc muốn hủy đơn hàng này?",
                        () -> updateOrderStatus("Đã hủy")
                ));
                break;
            case "Chờ giao hàng":
                btnReceived.setVisibility(View.VISIBLE);
                btnReceived.setOnClickListener(v -> showConfirmDialog(
                        "Xác nhận nhận hàng",
                        "Bạn đã nhận được hàng và muốn xác nhận?",
                        () -> updateOrderStatus("Đã giao")
                ));
                break;
            case "Đã giao":
                btnReturn.setVisibility(View.VISIBLE);
                btnReturn.setOnClickListener(v -> showConfirmDialog(
                        "Xác nhận trả hàng",
                        "Bạn muốn yêu cầu trả hàng/hoàn tiền?",
                        () -> updateOrderStatus("Trả hàng")
                ));
                // Kiểm tra xem đơn hàng đã có review chưa
                checkHasReviews();
                break;
            case "Trả hàng":
            case "Đã hủy":
                // Không hiển thị nút
                break;
        }
    }

    private void checkHasReviews() {
        if (token == null) {
            Log.e("OrderActivity", "Token is null");
            return;
        }
        apiService.getReviewsForOrder("Bearer " + token, order.getId()).enqueue(new Callback<HasReviewsResponse>() {
            @Override
            public void onResponse(Call<HasReviewsResponse> call, Response<HasReviewsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    boolean hasReviews = response.body().hasReviews();
                    Log.d("OrderActivity", "Order " + order.getId() + " has reviews: " + hasReviews);
                    if (!hasReviews) {
                        // Chưa có review, hiển thị nút đánh giá
                        btnRate.setVisibility(View.VISIBLE);
                        btnRate.setOnClickListener(v -> {
                            Intent intent = new Intent(OrderActivity.this, ReviewActivity.class);
                            intent.putExtra("order", order);
                            startActivity(intent);
                        });
                    } else {
                        // Đã có review, ẩn nút
                        btnRate.setVisibility(View.GONE);
                    }
                } else {
                    String errorMsg = "Error checking reviews: " + response.message();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("OrderActivity", "Error parsing error body: " + e.getMessage());
                    }
                    Log.e("OrderActivity", errorMsg);
                    // Mặc định hiển thị nút nếu lỗi
                    btnRate.setVisibility(View.VISIBLE);
                    btnRate.setOnClickListener(v -> {
                        Intent intent = new Intent(OrderActivity.this, ReviewActivity.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onFailure(Call<HasReviewsResponse> call, Throwable t) {
                Log.e("OrderActivity", "Failed to check reviews: " + t.getMessage());
                // Mặc định hiển thị nút nếu lỗi
                btnRate.setVisibility(View.VISIBLE);
                btnRate.setOnClickListener(v -> {
                    Intent intent = new Intent(OrderActivity.this, ReviewActivity.class);
                    intent.putExtra("order", order);
                    startActivity(intent);
                });
            }
        });
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Hủy", null)
                .setCancelable(true)
                .show();
    }

    private void updateOrderStatus(String newStatus) {
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        StatusUpdateRequest request = new StatusUpdateRequest(newStatus);
        apiService.updateOrderStatus("Bearer " + token, order.getId(), request)
                .enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(OrderActivity.this, "Cập nhật trạng thái thành công: " + newStatus, Toast.LENGTH_SHORT).show();
                            // Cập nhật trạng thái đơn hàng
                            order.setStatus(newStatus);
                            // Làm mới giao diện
                            displayOrderDetails();
                            setupButtons();
                            // Gửi broadcast để làm mới danh sách đơn hàng
                            Intent intent = new Intent("com.example.app.REFRESH_ORDERS");
                            intent.putExtra("status", newStatus); // Trạng thái mới
                            intent.putExtra("oldStatus", order.getStatus()); // Trạng thái cũ
                            LocalBroadcastManager.getInstance(OrderActivity.this).sendBroadcast(intent);
                            Log.d("OrderActivity", "Sent refresh broadcast for newStatus: " + newStatus + ", oldStatus: " + order.getStatus());
                        } else {
                            String errorMsg = "Lỗi cập nhật trạng thái: " + response.message();
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e("OrderActivity", "Error parsing error body: " + e.getMessage());
                            }
                            Toast.makeText(OrderActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e("OrderActivity", errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("OrderActivity", "Update status failed: " + t.getMessage());
                    }
                });
    }
}