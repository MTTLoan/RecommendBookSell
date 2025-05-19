package com.example.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.adapters.BookImageAdapter;
import com.example.app.adapters.DisplayReviewAdapter;
import com.example.app.models.Book;
import com.example.app.models.Image;
import com.example.app.models.Review;
import com.example.app.models.response.BookResponse;
import com.example.app.models.response.ReviewResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.HeaderController;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {
    private ViewPager2 imageViewPager;
    private TextView tabIndicator;
    private TextView txtBookName, txtAuthor, tvPrice, tvDescription,
            tvToggle, tvRating, tvNumberReview;
    private RatingBar ratingBar;
    private RecyclerView reviewRecyclerView;
    private DisplayReviewAdapter reviewAdapter;
    private boolean isDescriptionExpanded = false;
    private int quantity = 1;
    private TextView tvQuantity;
    private ImageButton btnIncrease, btnDecrease;
    private Button btnAddToCart;
    private ApiService apiService;
    private int bookId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        initViews();
        // Set up RecyclerView for header
        HeaderController.setupHeader(this);
        findViewById(R.id.ivReturn).setOnClickListener(v -> onBackPressed());

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Lấy bookId từ Intent
        bookId =1;
                //getIntent().getIntExtra("bookId", -1);
        if (bookId == -1) {
            Toast.makeText(this, "ID sách không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập số lượng và giỏ hàng
        tvQuantity.setText(String.valueOf(quantity));
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
        btnAddToCart.setOnClickListener(v ->
                Toast.makeText(this, "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show());

        // Gọi API để lấy dữ liệu
        fetchBookDetail();
        fetchBookReviews();
    }

    private void initViews() {
        imageViewPager = findViewById(R.id.imageViewPager);
        tabIndicator = findViewById(R.id.tabIndicator);
        txtBookName = findViewById(R.id.txtBookName);
        txtAuthor = findViewById(R.id.txtAuthor);
        ratingBar = findViewById(R.id.rbrating_bar);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvToggle = findViewById(R.id.tvToggle);
        tvRating = findViewById(R.id.tvRating);
        tvNumberReview = findViewById(R.id.tvNumberReview);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Thiết lập RecyclerView
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewAdapter = new DisplayReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        tvToggle.setOnClickListener(v -> toggleDescription());
    }

    private void toggleDescription() {
        if (isDescriptionExpanded) {
            tvDescription.setMaxLines(5);
            tvToggle.setText("Xem thêm");
        } else {
            tvDescription.setMaxLines(Integer.MAX_VALUE);
            tvToggle.setText("Thu gọn");
        }
        isDescriptionExpanded = !isDescriptionExpanded;
    }

    private void fetchBookDetail() {
        Log.d("BookDetailActivity", "Fetching book with ID: " + bookId);
        Call<BookResponse> call = apiService.getBookDetail(bookId);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBook() != null) {
                    Book book = response.body().getBook();

                    Log.d("BookDetailActivity", "Book data: " + new com.google.gson.Gson().toJson(book));
                    loadBookData(book);
                } else {
                    String errorMessage = "Lỗi lấy thông tin sách.";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("BookDetailActivity", "Error body: " + errorBody);
                        JSONObject errorJson = new JSONObject(errorBody);
                        if (errorJson.has("msg")) {
                            errorMessage = errorJson.getString("msg");
                        }
                    } catch (Exception e) {
                        Log.e("BookDetailActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(BookDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                String errorMessage = t instanceof java.io.IOException ? "Lỗi mạng" : "Lỗi không xác định";
                Toast.makeText(BookDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("BookDetailActivity", "API failure: " + t.getMessage(), t);
                finish();
            }
        });
    }

    private void fetchBookReviews() {
        Log.d("BookDetailActivity", "Fetching reviews for book ID: " + bookId);
        Call<ReviewResponse> call = apiService.getBookReviews(bookId);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getReviews() != null) {
                    List<Review> reviews = response.body().getReviews();
                    Log.d("BookDetailActivity", "Reviews data: " + new com.google.gson.Gson().toJson(reviews));
                    reviewAdapter = new DisplayReviewAdapter(reviews);
                    reviewRecyclerView.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();
                } else {
                    String errorMessage = "Lỗi lấy danh sách đánh giá.";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("BookDetailActivity", "Reviews error body: " + errorBody);
                        JSONObject errorJson = new JSONObject(errorBody);
                        if (errorJson.has("msg")) {
                            errorMessage = errorJson.getString("msg");
                        }
                    } catch (Exception e) {
                        Log.e("BookDetailActivity", "Error parsing reviews error body", e);
                    }
                    Toast.makeText(BookDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    reviewAdapter = new DisplayReviewAdapter(new ArrayList<>());
                    reviewRecyclerView.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                String errorMessage = t instanceof java.io.IOException ? "Lỗi mạng" : "Lỗi không xác định";
                Toast.makeText(BookDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("BookDetailActivity", "Reviews API failure: " + t.getMessage(), t);
                reviewAdapter = new DisplayReviewAdapter(new ArrayList<>());
                reviewRecyclerView.setAdapter(reviewAdapter);
            }
        });
    }

    private void loadBookData(Book book) {
        // Load book images
        List<Image> imageList = book.getImages() != null ? book.getImages() : new ArrayList<>();
        BookImageAdapter imageAdapter = new BookImageAdapter(this, imageList);
        imageViewPager.setAdapter(imageAdapter);

        // Bắt sự kiện click ảnh
        imageAdapter.setOnImageClickListener(position -> {
            int nextPosition = position + 1;
            if (nextPosition >= imageAdapter.getItemCount()) {
                nextPosition = 0; // Quay lại ảnh đầu nếu đang ảnh cuối
            }
            imageViewPager.setCurrentItem(nextPosition, true); // true = có animation
        });

        imageViewPager.setAdapter(imageAdapter);

        // Cập nhật tabIndicator...
        int totalImages = imageAdapter.getItemCount();
        if (totalImages > 0) {
            tabIndicator.setText("1/" + totalImages);
        } else {
            tabIndicator.setText("0/0");
        }

        imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabIndicator.setText((position + 1) + "/" + totalImages);
            }
        });

        // Load book details
        txtBookName.setText(book.getName());
        txtAuthor.setText("Tác giả: " + String.join(", ", book.getAuthor()));
        ratingBar.setRating(book.getAverageRating());

        // Định dạng giá
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvPrice.setText(currencyFormat.format(book.getPrice()));

        tvDescription.setText(book.getDescription() != null ? book.getDescription() : "Không có mô tả");
        tvToggle.setVisibility(book.getDescription() != null && book.getDescription().length() > 100 ? View.VISIBLE : View.GONE);

        // Load overall rating and review count
        tvRating.setText(String.format("%.1f/5", book.getAverageRating()));
        tvNumberReview.setText(String.format("(%d đánh giá)", book.getRatingCount()));
    }

}