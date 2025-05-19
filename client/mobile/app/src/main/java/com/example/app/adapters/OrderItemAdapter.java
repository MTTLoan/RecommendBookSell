package com.example.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.OrderItem;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private final List<OrderItem> items;

    public OrderItemAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        if (item.getBook() != null) {
            holder.tvBookTitle.setText(item.getBook().getName());
            // Hiển thị danh sách tác giả
            String authors = item.getBook().getAuthor().isEmpty()
                    ? "Unknown"
                    : item.getBook().getAuthor().stream().collect(Collectors.joining(", "));
            holder.tvBookTitle.setText(item.getBook().getName() + " - " + authors);
            if (!item.getBook().getImages().isEmpty()) {
                Picasso.get()
                        .load(item.getBook().getImages().get(0).getUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivBookImage);
            }
        } else {
            holder.tvBookTitle.setText("Không có thông tin sách");
        }
        holder.tvQuantity.setText(String.format("SL: %d", item.getQuantity()));
        holder.tvPrice.setText(String.format(Locale.forLanguageTag("vi-VN"), "%,.0fđ", item.getUnitPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookTitle, tvQuantity, tvPrice;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.ivBookImage);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}