package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.example.app.R;
import com.example.app.models.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private Context context;
    private OnItemClickListener listener;

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

        // Định dạng createdAt từ ISO 8601 sang YYYY-MM-DD HH:mm:ss
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(notification.getCreatedAt());
            holder.tvTime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.tvTime.setText(notification.getCreatedAt()); // Hiển thị gốc nếu lỗi
        }

        // Tải hình ảnh bằng Picasso
        if (notification.getImageUrl() != null && !notification.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(notification.getImageUrl())
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(holder.imageBook);
        } else {
            holder.imageBook.setImageResource(R.drawable.placeholder_book);
        }

        if (notification.isRead()) {
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // Trắng
        } else {
            holder.itemView.setBackgroundColor(0xFFFFF8F8); // Hồng nhạt
        }

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Notification clickedNotification = notifications.get(currentPosition);
                listener.onItemClick(clickedNotification);
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