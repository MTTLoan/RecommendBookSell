package com.example.app.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.activities.OrderActivity;
import com.example.app.activities.ReviewActivity;
import com.example.app.models.Order;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.app.R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderDate.setText(String.format("Ngày đặt hàng: %s", order.getOrderDate()));
        holder.tvTotalAmount.setText(String.format(Locale.forLanguageTag("vi-VN"), "Tổng tiền: %,d VNĐ", (int) order.getTotalAmount()));
        holder.tvStatus.setText(String.format("%s", order.getStatus()));

        // Thiết lập RecyclerView cho danh sách sách
        OrderItemAdapter itemAdapter = new OrderItemAdapter(order.getItems());
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvOrderItems.setAdapter(itemAdapter);

        // Hiển thị các nút theo trạng thái
        holder.btnCancelOrder.setVisibility(View.GONE);
        holder.btnReceived.setVisibility(View.GONE);
        holder.btnReturn.setVisibility(View.GONE);
        holder.btnRate.setVisibility(View.GONE);

        switch (order.getStatus()) {
            case "Đang đóng gói":
                holder.btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "Chờ giao hàng":
                holder.btnReceived.setVisibility(View.VISIBLE);
                break;
            case "Đã giao":
                holder.btnReturn.setVisibility(View.VISIBLE);
                holder.btnRate.setVisibility(View.VISIBLE);
                holder.btnRate.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), ReviewActivity.class);
                    intent.putExtra("order", order);
                    holder.itemView.getContext().startActivity(intent);
                });
                break;
            case "Trả hàng":
            case "Đã hủy":
                // Không có nút
                break;
        }

        // Sự kiện nhấn vào đơn hàng để chuyển sang OrderActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), OrderActivity.class);
            holder.itemView.getContext().startActivity(intent);
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
            tvOrderDate = itemView.findViewById(com.example.app.R.id.tvOrderDate);
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