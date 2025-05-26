package com.example.app.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.activities.OrderActivity;
import com.example.app.activities.ReviewActivity;
import com.example.app.models.HasReviewsResponse;
import com.example.app.models.Order;
import com.example.app.models.Review;
import com.example.app.models.request.StatusUpdateRequest;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private final Runnable onStatusChanged; // Callback để tải lại fragment

    public OrderAdapter(List<Order> orderList, Runnable onStatusChanged) {
        this.orderList = orderList;
        this.onStatusChanged = onStatusChanged;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderDate.setText(String.format("Ngày đặt hàng: %s", order.getFormattedOrderDate()));
        holder.tvTotalAmount.setText(String.format(Locale.forLanguageTag("vi-VN"), "Tổng tiền: %,d VNĐ", (int) order.getTotalAmount()));
        holder.tvStatus.setText(order.getStatus());

        // Thiết lập RecyclerView cho danh sách sách
        OrderItemAdapter itemAdapter = new OrderItemAdapter(order.getItems());
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvOrderItems.setAdapter(itemAdapter);

        // Hiển thị các nút theo trạng thái
        holder.btnCancelOrder.setVisibility(View.GONE);
        holder.btnReceived.setVisibility(View.GONE);
        holder.btnReturn.setVisibility(View.GONE);
        holder.btnRate.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getApiService();
        String token = AuthUtils.getToken(holder.itemView.getContext());

        // Kiểm tra xem đơn hàng có trong vòng 7 ngày trước không
        boolean isWithinSevenDays = isOrderWithinSevenDays(order.getOrderDate());

        switch (order.getStatus()) {
            case "Đang đóng gói":
                holder.btnCancelOrder.setVisibility(View.VISIBLE);
                holder.btnCancelOrder.setOnClickListener(v -> showConfirmDialog(
                        holder.itemView.getContext(),
                        "Xác nhận hủy đơn hàng",
                        "Bạn có chắc muốn hủy đơn hàng này?",
                        () -> updateOrderStatus(apiService, token, order, "Đã hủy", holder.itemView.getContext())
                ));
                break;
            case "Chờ giao hàng":
                holder.btnReceived.setVisibility(View.VISIBLE);
                holder.btnReceived.setOnClickListener(v -> showConfirmDialog(
                        holder.itemView.getContext(),
                        "Xác nhận nhận hàng",
                        "Bạn đã nhận được hàng và muốn xác nhận?",
                        () -> updateOrderStatus(apiService, token, order, "Đã giao", holder.itemView.getContext())
                ));
                break;
            case "Đã giao":
                if (isWithinSevenDays) {
                    holder.btnReturn.setVisibility(View.VISIBLE);
                    holder.btnReturn.setOnClickListener(v -> showConfirmDialog(
                            holder.itemView.getContext(),
                            "Xác nhận trả hàng",
                            "Bạn muốn yêu cầu trả hàng/hoàn tiền?",
                            () -> updateOrderStatus(apiService, token, order, "Trả hàng", holder.itemView.getContext())
                    ));
                }
                // Kiểm tra xem đơn hàng đã có review chưa
                checkHasReviews(apiService, token, order, holder);
                break;
            case "Trả hàng":
            case "Đã hủy":
                // Không có nút
                break;
        }

        // Sự kiện nhấn vào đơn hàng để chuyển sang OrderActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OrderActivity.class);
            intent.putExtra("order", order); // Truyền order để hiển thị chi tiết
            holder.itemView.getContext().startActivity(intent);
        });
    }

    // Phương thức kiểm tra xem orderDate có trong vòng 7 ngày trước không
    private boolean isOrderWithinSevenDays(String orderDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());
            Date orderDateParsed = inputFormat.parse(orderDate);
            if (orderDateParsed == null) return false;

            // Lấy thời gian hiện tại
            Date currentDate = new Date();

            // Tính thời gian 7 ngày trước
            long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L; // 7 ngày tính bằng mili giây
            Date sevenDaysAgo = new Date(currentDate.getTime() - sevenDaysInMillis);

            // Kiểm tra xem orderDate có nằm trong khoảng từ 7 ngày trước đến hiện tại không
            return orderDateParsed.after(sevenDaysAgo) && orderDateParsed.before(currentDate);
        } catch (ParseException e) {
            Log.e("OrderAdapter", "Error parsing order date: " + e.getMessage());
            return false; // Nếu có lỗi phân tích ngày, không hiển thị nút trả hàng
        }
    }

    private void checkHasReviews(ApiService apiService, String token, Order order, OrderViewHolder holder) {
        if (token == null) {
            Log.e("OrderAdapter", "Token is null");
            return;
        }
        apiService.getReviewsForOrder("Bearer " + token, order.getId()).enqueue(new Callback<HasReviewsResponse>() {
            @Override
            public void onResponse(Call<HasReviewsResponse> call, Response<HasReviewsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    boolean hasReviews = response.body().hasReviews();
                    Log.d("OrderAdapter", "Order " + order.getId() + " has reviews: " + hasReviews);
                    if (!hasReviews) {
                        // Chưa có review, hiển thị nút đánh giá
                        holder.btnRate.setVisibility(View.VISIBLE);
                        holder.btnRate.setOnClickListener(v -> {
                            Intent intent = new Intent(holder.itemView.getContext(), ReviewActivity.class);
                            intent.putExtra("order", order);
                            holder.itemView.getContext().startActivity(intent);
                        });
                    } else {
                        // Đã có review, ẩn nút
                        holder.btnRate.setVisibility(View.GONE);
                    }
                } else {
                    String errorMsg = "Error checking reviews: " + response.message();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("OrderAdapter", "Error parsing error body: " + e.getMessage());
                    }
                    Log.e("OrderAdapter", errorMsg);
                    // Mặc định hiển thị nút nếu lỗi
                    holder.btnRate.setVisibility(View.VISIBLE);
                    holder.btnRate.setOnClickListener(v -> {
                        Intent intent = new Intent(holder.itemView.getContext(), ReviewActivity.class);
                        intent.putExtra("order", order);
                        holder.itemView.getContext().startActivity(intent);
                    });
                }
            }

            @Override
            public void onFailure(Call<HasReviewsResponse> call, Throwable t) {
                Log.e("OrderAdapter", "Failed to check reviews: " + t.getMessage());
                // Mặc định hiển thị nút nếu lỗi
                holder.btnRate.setVisibility(View.VISIBLE);
                holder.btnRate.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ReviewActivity.class);
                    intent.putExtra("order", order);
                    holder.itemView.getContext().startActivity(intent);
                });
            }
        });
    }
    private void showConfirmDialog(android.content.Context context, String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Hủy", null)
                .setCancelable(true)
                .show();
    }

    private void updateOrderStatus(ApiService apiService, String token, Order order, String newStatus, android.content.Context context) {
        if (token == null) {
            Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        StatusUpdateRequest request = new StatusUpdateRequest(newStatus);
        apiService.updateOrderStatus("Bearer " + token, order.getId(), request)
                .enqueue(new retrofit2.Callback<Order>() {
                    @Override
                    public void onResponse(retrofit2.Call<Order> call, retrofit2.Response<Order> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(context, "Cập nhật trạng thái thành công: " + newStatus, Toast.LENGTH_SHORT).show();
                            if (onStatusChanged != null) {
                                onStatusChanged.run();
                            }
                            // Gửi broadcast để làm mới danh sách đơn hàng
                            Intent intent = new Intent("com.example.app.REFRESH_ORDERS");
                            intent.putExtra("status", newStatus); // Gửi trạng thái mới
                            intent.putExtra("oldStatus", order.getStatus()); // Gửi trạng thái cũ
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            Log.d("OrderAdapter", "Sent refresh broadcast for newStatus: " + newStatus + ", oldStatus: " + order.getStatus());
                        } else {
                            String errorMsg = "Lỗi cập nhật trạng thái: " + response.message();
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e("OrderAdapter", "Error parsing error body: " + e.getMessage());
                            }
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                            Log.e("OrderAdapter", errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Order> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("OrderAdapter", "Update status failed: " + t.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderDate, tvTotalAmount, tvStatus;
        RecyclerView rvOrderItems;
        Button btnCancelOrder, btnReceived, btnReturn, btnRate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            rvOrderItems = itemView.findViewById(R.id.rvOrderItems);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnReceived = itemView.findViewById(R.id.btnReceived);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }
}