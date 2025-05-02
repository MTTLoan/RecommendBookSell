package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private Context context;
    private OnItemClickListener listener;

    // Interface để xử lý sự kiện nhấn
    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> notifications, OnItemClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvContent.setText(notification.getMessage());
        holder.tvTime.setText(notification.getCreatedAt());

        // Đổi màu nền dựa trên trạng thái isRead
        if (notification.isRead()) {
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // #FFFFFF (trắng)
        } else {
            holder.itemView.setBackgroundColor(0xFFFFF8F8); // #FFF8F8 (hồng nhạt)
        }

        // Sự kiện nhấn
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
                if (currentPosition != RecyclerView.NO_POSITION) { // Kiểm tra vị trí hợp lệ
                    Notification clickedNotification = notifications.get(currentPosition);
                    clickedNotification.setRead(true); // Đánh dấu đã đọc
                    notifyItemChanged(currentPosition); // Cập nhật giao diện
                    listener.onItemClick(clickedNotification);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBook;
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook = itemView.findViewById(R.id.imageBook);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}