// adapters/CartAdapter.java
package com.example.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.activities.CartActivity;
import com.example.app.models.Book;
import com.example.app.models.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Book book = cartItem.getBook();

        if (book != null) {
            // Load hình ảnh sách
            if (book.getImages() != null && !book.getImages().isEmpty()) {
                Picasso.get().load(book.getImages().get(0).getUrl())
                        .placeholder(R.drawable.placeholder_book)
                        .into(holder.bookImage);
            } else {
                holder.bookImage.setImageResource(R.drawable.placeholder_book);
            }

            // Hiển thị tiêu đề và giá
            holder.bookTitle.setText(book.getName());
            holder.price.setText(String.format("%,.0fđ", book.getPrice()));
            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
            holder.checkBox.setChecked(cartItem.isSelected());
        }

        // Checkbox listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            listener.onCartChanged();
        });

        // Giảm số lượng
        holder.decreaseButton.setOnClickListener(v -> {
            int qty = cartItem.getQuantity();
            if (qty > 1) {
                ((CartActivity) context).updateCartItemQuantity(cartItem, qty - 1);
                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
            }
        });

        // Tăng số lượng
        holder.increaseButton.setOnClickListener(v -> {
            ((CartActivity) context).updateCartItemQuantity(cartItem, cartItem.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView bookImage;
        TextView bookTitle, price, quantity;
        ImageButton decreaseButton, increaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cbSelect);
            bookImage = itemView.findViewById(R.id.ivBookImage);
            bookTitle = itemView.findViewById(R.id.tvBookTitle);
            price = itemView.findViewById(R.id.tvPrice);
            quantity = itemView.findViewById(R.id.tvQuantity);
            decreaseButton = itemView.findViewById(R.id.btnDecrease);
            increaseButton = itemView.findViewById(R.id.btnIncrease);
        }
    }
}