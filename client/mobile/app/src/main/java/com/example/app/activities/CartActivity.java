package com.example.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.CartAdapter;
import com.example.app.models.Book;
import com.example.app.models.Cart;
import com.example.app.models.CartItem;
import com.example.app.models.Order;
import com.example.app.models.OrderItem;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private ImageView ivReturn, ivDelete;
    private Button btnCheckout;
    private List<CartItem> cartItems;
    private CartAdapter cartAdapter;
    private ApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo views
        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        ivReturn = findViewById(R.id.ivReturn);
        ivDelete = findViewById(R.id.ivDelete);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Khởi tạo ApiService từ RetrofitClient
        apiService = RetrofitClient.getApiService();

        // Lấy token xác thực
        authToken = AuthUtils.getToken(this);
        if (authToken == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo danh sách giỏ hàng
        cartItems = new ArrayList<>();

        // Thiết lập RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, this, authToken);
        rvCartItems.setAdapter(cartAdapter);

        // Hiển thị giỏ hàng
        displayCart();

        // Nút quay lại
        ivReturn.setOnClickListener(v -> finish());

        // Xóa các sản phẩm được chọn
        ivDelete.setOnClickListener(v -> deleteSelectedItems());

        // Nút thanh toán
        btnCheckout.setOnClickListener(v -> proceedToCheckout());
    }

    private void displayCart() {
        Call<Cart> call = apiService.getCart("Bearer " + authToken);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body().getItems());
                    Log.d("CartActivity", "Cart Items: " + cartItems); // Thêm log
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                } else {
//                    Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("CartActivity", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CartActivity", "Lỗi kết nối: " + t.getMessage());
//                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteSelectedItems() {
        List<CartItem> itemsToDelete = new ArrayList<>();
        Iterator<CartItem> iterator = cartItems.iterator();
        boolean hasSelectedItems = false;

        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getSelected()) {
                itemsToDelete.add(item);
                hasSelectedItems = true;
            }
        }

        if (!hasSelectedItems) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa " + itemsToDelete.size() + " sản phẩm đã chọn không?")
                .setPositiveButton("Có", (dialog, which) -> deleteItemsSequentially(itemsToDelete, 0))
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void deleteItemsSequentially(List<CartItem> itemsToDelete, int index) {
        if (index >= itemsToDelete.size()) {
            cartAdapter.notifyDataSetChanged();
            updateTotalAmount();
            Toast.makeText(CartActivity.this, "Đã xóa " + itemsToDelete.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem item = itemsToDelete.get(index);
        Call<Cart> call = apiService.deleteCartItem("Bearer " + authToken, item.getBookId());
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    cartItems.removeIf(i -> i.getBookId() == item.getBookId());
                    deleteItemsSequentially(itemsToDelete, index + 1);
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi khi xóa sản phẩm: " + response.code(), Toast.LENGTH_SHORT).show();
                    displayCart();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CartActivity", "Lỗi khi xóa sản phẩm: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                displayCart();
            }
        });
    }

    // Cập nhật số lượng
    public void updateCartItemQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity < 1) {
            return; // Không cho phép số lượng < 1
        }

        cartItem.setQuantity(newQuantity);
        Cart updatedCart = new Cart();
        updatedCart.setItems(cartItems);

        Call<Cart> call = apiService.updateCart("Bearer " + authToken, updatedCart);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                } else {
//                    Toast.makeText(CartActivity.this, "Lỗi khi cập nhật số lượng: " + response.code(), Toast.LENGTH_SHORT).show();
                    displayCart(); // Tải lại giỏ hàng nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CartActivity", "Lỗi khi cập nhật số lượng: " + t.getMessage());
//                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                displayCart(); // Tải lại giỏ hàng nếu lỗi
            }
        });
    }

    // Cập nhật trạng thái selected
    public void updateCartItemSelected(CartItem cartItem, boolean newSelected) {
        cartItem.setSelected(newSelected);
        Cart updatedCart = new Cart();
        updatedCart.setItems(cartItems);

        Call<Cart> call = apiService.updateCart("Bearer " + authToken, updatedCart);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                } else {
//                    Toast.makeText(CartActivity.this, "Lỗi khi cập nhật trạng thái: " + response.code(), Toast.LENGTH_SHORT).show();
                    displayCart(); // Tải lại giỏ hàng nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CartActivity", "Lỗi khi cập nhật trạng thái: " + t.getMessage());
//                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                displayCart(); // Tải lại giỏ hàng nếu lỗi
            }
        });
    }
    private void updateTotalAmount() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.getSelected()) {
                Book book = item.getBook();
                if (book != null) {
                    total += book.getPrice() * item.getQuantity();
                }
            }
        }
        tvTotalAmount.setText(String.format("%,.0f đ", total));
    }

    @Override
    public void onCartChanged() {
        updateTotalAmount();
    }

    private void proceedToCheckout() {
        List<CartItem> selectedItems = new ArrayList<>();
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            if (item.getSelected()) {
                selectedItems.add(item);
                totalAmount += item.getBook().getPrice() * item.getQuantity();
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : selectedItems) {
            orderItems.add(new OrderItem(
                    cartItem.getBookId(),
                    cartItem.getQuantity(),
                    cartItem.getBook().getPrice(),
                    cartItem.getBook()
            ));
        }

        Order order = new Order();
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setUserId(AuthUtils.getUserId(this));

        Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }
}