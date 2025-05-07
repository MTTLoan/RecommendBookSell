package com.example.app.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.StatusOrder;

import java.util.List;

public class StatusOrderAdapter extends RecyclerView.Adapter<StatusOrderAdapter.StatusOrderViewHolder> {
    private static final String TAG = "StatusOrderAdapter";
    private List<StatusOrder> statusList;
    private int selectedPosition = 0; // Mặc định chọn vị trí đầu tiên
    private OnStatusClickListener listener;

    public interface OnStatusClickListener {
        void onStatusClick(int position);
    }

    public StatusOrderAdapter(List<StatusOrder> statusList, OnStatusClickListener listener) {
        this.statusList = statusList;
        this.listener = listener;
        // Đặt trạng thái mặc định cho mục đầu tiên
        if (!statusList.isEmpty()) {
            statusList.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public StatusOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false);
        return new StatusOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusOrderViewHolder holder, int position) {
        StatusOrder status = statusList.get(position);
        holder.tvStatus.setText(status.getName());

        // Cập nhật giao diện khi được chọn
        if (status.isSelected()) {
            holder.tvStatus.setTextColor(0xFF3FBF48); // Màu xanh lá
            holder.tvStatus.setTypeface(null, android.graphics.Typeface.BOLD); // In đậm
            holder.statusContainer.setBackgroundResource(R.drawable.border_bottom); // Áp dụng viền dưới
            Log.d(TAG, "Setting bold text and border bottom at position: " + position);
        } else {
            holder.tvStatus.setTextColor(0xFF666666); // Màu xám mặc định
            holder.tvStatus.setTypeface(null, android.graphics.Typeface.NORMAL); // Không in đậm
            holder.statusContainer.setBackgroundColor(Color.WHITE); // Background trắng
            Log.d(TAG, "Setting normal text and white background at position: " + position);
        }

        // Xử lý sự kiện nhấn
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                // Cập nhật trạng thái được chọn
                setSelectedPosition(currentPosition);
                if (listener != null) listener.onStatusClick(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public void setSelectedPosition(int position) {
        // Đặt tất cả trạng thái về không chọn
        for (int i = 0; i < statusList.size(); i++) {
            statusList.get(i).setSelected(i == position);
        }
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    static class StatusOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus;
        ConstraintLayout statusContainer;

        public StatusOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            statusContainer = itemView.findViewById(R.id.statusContainer);
            if (statusContainer == null) {
                Log.e(TAG, "statusContainer is null in ViewHolder!");
            }
        }
    }
}