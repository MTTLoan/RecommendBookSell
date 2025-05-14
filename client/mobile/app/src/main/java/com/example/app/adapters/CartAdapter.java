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
import com.example.app.models.Cart;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Cart> cartList;
    private Context context;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<Cart> cartList, OnCartChangeListener listener) {
        this.context = context;
        this.cartList = cartList;
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
        Cart cartItem = cartList.get(position);

//        // Load image using Picasso (use placeholder since it's mock data)
//        Picasso.get().load(cartItem.getImageUrl())
//                .placeholder(R.drawable.placeholder_book)
//                .into(holder.bookImage);

//        holder.bookTitle.setText(cartItem.getTitle());
//        holder.price.setText(String.format("%,.0fđ", cartItem.getPrice()));
//        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
//        holder.checkBox.setChecked(cartItem.isSelected());

        // Checkbox listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            cartItem.setSelected(isChecked);
            listener.onCartChanged();
        });

        // Decrease quantity
        holder.decreaseButton.setOnClickListener(v -> {
//            int qty = cartItem.getQuantity();
//            if (qty > 1) {
//                cartItem.setQuantity(qty - 1);
//                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
//                listener.onCartChanged();
//            }
        });

        // Increase quantity
        holder.increaseButton.setOnClickListener(v -> {
//            cartItem.setQuantity(cartItem.getQuantity() + 1);
//            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
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