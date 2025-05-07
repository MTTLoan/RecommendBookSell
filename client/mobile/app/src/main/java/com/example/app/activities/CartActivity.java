package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.adapters.CartAdapter;
import com.example.app.models.Cart;
import com.example.app.models.Order;
import com.example.app.models.OrderItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        cartList.add(new Cart("3", "https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg", "Những Con Mèo Sau Bức Tường Hoa", 250000, 1, false));
        cartList.add(new Cart("4", "https://salt.tikicdn.com/ts/product/18/08/cb/f09cdf8650d2f2a3f397ef968a312783.jpg", "Bộ ba phép thuật - Úm ba la ánh sáng hiện ra", 150000, 1, false));

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
            List<Cart> selectedItems = new ArrayList<>();
            for (Cart item : cartList) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
            } else {
                // Tạo danh sách OrderItem từ các sản phẩm được chọn
                List<OrderItem> orderItems = new ArrayList<>();
                double totalAmount = 0;
                for (Cart cart : selectedItems) {
                    orderItems.add(new OrderItem(
                            Integer.parseInt(cart.getId()),
                            cart.getQuantity(),
                            cart.getPrice(),
                            cart.getTitle(),
                            cart.getImageUrl()
                    ));
                    totalAmount += cart.getPrice() * cart.getQuantity();
                }

                // Tạo đối tượng Order với mã địa chỉ hợp lệ
                Order order = new Order(
                        1, // id (giả lập)
                        1, // userId (giả lập)
                        "2025-05-08", // orderDate (giả lập)
                        totalAmount,
                        "PENDING", // status
                        26124, // shippingAddressWardCode (Phường Linh Trung)
                        774,   // shippingAddressDistrictCode (Quận Thủ Đức)
                        79,    // shippingAddressProvinceCode (TP. Hồ Chí Minh)
                        "37 đường số 8", // shippingDetailedAddress (giả lập)
                        orderItems,
                        "Mai Thị Thanh Loan", // userFullName (giả lập)
                        "0123456789" // userPhoneNumber (giả lập)
                );

                // Chuyển sang OrderConfirmationActivity
                try {
                    Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", order);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    // Xóa các sản phẩm đã chọn sau khi đặt hàng
                    Iterator<Cart> iterator = cartList.iterator();
                    while (iterator.hasNext()) {
                        Cart item = iterator.next();
                        if (item.isSelected()) {
                            iterator.remove();
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                    Toast.makeText(this, "Chuyển đến xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi khi chuyển đến OrderConfirmationActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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