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

public class OrderConfirmItemAdapter extends RecyclerView.Adapter<OrderConfirmItemAdapter.OrderConfirmItemViewHolder> {
    private List<OrderItem> items;

    public OrderConfirmItemAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderConfirmItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_confirmation, parent, false);
        return new OrderConfirmItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderConfirmItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.tvBookName.setText(item.getBookName());
        holder.tvQuantity.setText("x" + item.getQuantity());
        holder.tvPrice.setText(String.format("%,d VNĐ", (int) (item.getUnitPrice() * item.getQuantity())));

        // Tải ảnh sách từ imageUrl bằng Picasso
        Picasso.get()
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_book) // Ảnh placeholder nếu lỗi
                .into(holder.ivBookImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderConfirmItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookName, tvQuantity, tvPrice;
        ImageView ivBookImage;

        public OrderConfirmItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivBookImage = itemView.findViewById(R.id.iv_book_image);
        }
    }
}