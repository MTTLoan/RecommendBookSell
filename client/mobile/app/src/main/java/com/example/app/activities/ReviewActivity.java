package com.example.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.ReviewAdapter;
import com.example.app.models.Book;
import com.example.app.models.Order;
import com.example.app.models.OrderItem;
import com.example.app.models.Review;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerReviews;
    private ReviewAdapter reviewAdapter;
    private List<Book> bookList;
    private Button btnSubmitReview;
    private ImageView ivReturn;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerReviews = findViewById(R.id.recycler_reviews);
        btnSubmitReview = findViewById(R.id.btn_submit_review);
        ivReturn = findViewById(R.id.ivReturn);

        // Lấy Order từ Intent
        order = (Order) getIntent().getParcelableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy danh sách sách từ Order
        bookList = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            if (item.getBook() != null) {
                bookList.add(item.getBook());
            }
        }

        // Thiết lập RecyclerView
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, bookList);
        recyclerReviews.setAdapter(reviewAdapter);

        // Log để kiểm tra dữ liệu
        Log.d("ReviewActivity", "bookList size: " + bookList.size());

        // Hiển thị tiêu đề
        setTitle("Đánh Giá Đơn Hàng");

        // Xử lý nút Back
        ivReturn.setOnClickListener(v -> finish());

        // Xử lý khi nhấn nút "Gửi"
        btnSubmitReview.setOnClickListener(v -> submitReviews());
    }

    private void submitReviews() {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        List<Review> reviews = new ArrayList<>();
        boolean hasValidReview = false;

        // Lấy dữ liệu từ tất cả các item trong RecyclerView
        for (int i = 0; i < recyclerReviews.getChildCount(); i++) {
            ReviewAdapter.ReviewViewHolder holder = (ReviewAdapter.ReviewViewHolder) recyclerReviews.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                float rating = holder.ratingBar.getRating();
                String content = holder.etContent.getText().toString().trim();
                String packaging = holder.etPackaging.getText().toString().trim();
                String shipping = holder.etShipping.getText().toString().trim();
                String comment = holder.etComment.getText().toString().trim();

                // Kiểm tra nếu có đánh giá hợp lệ
                if (rating > 0 || !content.isEmpty() || !packaging.isEmpty() || !shipping.isEmpty() || !comment.isEmpty()) {
                    hasValidReview = true;
                    String fullComment = content + " | " + packaging + " | " + shipping + " | " + comment;
                    Book book = bookList.get(i);
                    Review review = new Review(
                            0, // ID sẽ được server tạo
                            order.getUserId(),
                            book.getId(),
                            (int) rating,
                            fullComment,
                            null // createdAt sẽ được server tạo
                    );
                    reviews.add(review);
                }
            }
        }

        if (!hasValidReview) {
            Toast.makeText(this, "Vui lòng nhập ít nhất một đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đếm số lượng phản hồi thành công
        AtomicInteger successCount = new AtomicInteger(0);
        int totalReviews = reviews.size();

        // Gửi từng đánh giá lên server
        for (Review review : reviews) {
            apiService.submitReview("Bearer " + token, review).enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("ReviewActivity", "Review submitted for bookId: " + review.getBookId());
                        if (successCount.incrementAndGet() == totalReviews) {
                            // Tất cả đánh giá đã gửi thành công
                            showThankYouDialog();
                        }
                    } else {
                        Toast.makeText(ReviewActivity.this, "Lỗi gửi đánh giá: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    Toast.makeText(ReviewActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ReviewActivity", "Submit review failed: " + t.getMessage());
                }
            });
        }
    }

    private void showThankYouDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cảm ơn bạn!")
                .setMessage("Cảm ơn bạn đã đánh giá! Đánh giá của bạn giúp chúng tôi cải thiện dịch vụ.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}