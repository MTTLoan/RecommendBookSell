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

public class OrderConfirmItemAdapter extends RecyclerView.Adapter<OrderConfirmItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems;

    public OrderConfirmItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_confirmation, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.tvBookName.setText(item.getBookName());
        holder.tvQuantity.setText("SL: " + item.getQuantity());
        holder.tvPrice.setText(String.format("%,d VNĐ", (int) item.getUnitPrice()));
        Picasso.get().load(item.getImageUrl()).into(holder.ivBookImage);

        // Thêm contentDescription cho mục
        holder.itemView.setContentDescription(
                "Sản phẩm: " + item.getBookName() + ", Số lượng: " + item.getQuantity() + ", Giá: " + String.format("%,d VNĐ", (int) item.getUnitPrice())
        );
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookImage;
        TextView tvBookName, tvQuantity, tvPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookImage = itemView.findViewById(R.id.iv_book_image);
            tvBookName = itemView.findViewById(R.id.tv_book_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}