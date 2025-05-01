package com.example.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.app.adapters.CartAdapter;
import com.example.app.models.Cart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.app.R;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private ImageView ivReturn, ivDelete;
    private Button btnCheckout;
    private List<Cart> cartList;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        ivReturn = findViewById(R.id.ivReturn);
        ivDelete = findViewById(R.id.ivDelete);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Initialize cart list with mock data
        cartList = new ArrayList<>();
        cartList.add(new Cart("1", "https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "Cùng con trưởng thành - Mình không thích bị cô lập - Bài học về sự dũng cảm - Dạy trẻ chống lại bạo lực tinh thần - Tránh xa tổn thương", 250000, 1, true));
        cartList.add(new Cart("2", "https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg", "Chuyện Con Mèo Dạy Hải Âu Bay", 250000, 1, true));
        cartList.add(new Cart("3", "https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg", "Những Con Mèo Sau Bức Tường Hoa", 250000, 1, true));
        cartList.add(new Cart("4", "https://salt.tikicdn.com/ts/product/18/08/cb/f09cdf8650d2f2a3f397ef968a312783.jpg", "Bộ ba phép thuật - Úm ba la ánh sáng hiện ra", 150000, 1, true));

        // Setup RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartList, this);
        rvCartItems.setAdapter(cartAdapter);

        // Update total amount initially
        updateTotalAmount();

        // Back button
        ivReturn.setOnClickListener(v -> finish());

        // Delete selected items
        ivDelete.setOnClickListener(v -> {
            // Remove selected items
            Iterator<Cart> iterator = cartList.iterator();
            boolean hasSelectedItems = false;
            while (iterator.hasNext()) {
                Cart item = iterator.next();
                if (item.isSelected()) {
                    iterator.remove();
                    hasSelectedItems = true;
                }
            }

            if (!hasSelectedItems) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để xóa", Toast.LENGTH_SHORT).show();
            } else {
                cartAdapter.notifyDataSetChanged();
                updateTotalAmount();
                Toast.makeText(this, "Đã xóa các sản phẩm được chọn", Toast.LENGTH_SHORT).show();
            }
        });

        // Checkout button
        btnCheckout.setOnClickListener(v -> {
            boolean hasSelectedItems = false;
            for (Cart item : cartList) {
                if (item.isSelected()) {
                    hasSelectedItems = true;
                    break;
                }
            }

            if (!hasSelectedItems) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
            } else {
                // Remove selected items after checkout
                Iterator<Cart> iterator = cartList.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().isSelected()) {
                        iterator.remove();
                    }
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalAmount();
                Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCartChanged() {
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double total = 0;
        for (Cart item : cartList) {
            if (item.isSelected()) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        tvTotalAmount.setText(String.format("%,.0f đ", total));
    }
}