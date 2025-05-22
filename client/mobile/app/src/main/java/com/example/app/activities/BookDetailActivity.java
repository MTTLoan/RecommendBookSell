package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.adapters.BookImageAdapter;
import com.example.app.adapters.DisplayReviewAdapter;
import com.example.app.models.Book;
import com.example.app.models.Cart;
import com.example.app.models.CartItem;
import com.example.app.models.Category;
import com.example.app.models.Image;
import com.example.app.models.Review;
import com.example.app.models.response.BookDetailResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.models.response.ReviewResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.example.app.utils.HeaderController;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private TextView tvTilte, tabIndicator, txtBookName, txtAuthor, tvPrice, tvDescription,
            tvToggle, tvRating, tvNumberReview, tvQuantity, tvToggleReview;
    private ImageButton btnArrowLeft, btnArrowRight, btnIncrease, btnDecrease;
    private RatingBar ratingBar;
    private RecyclerView reviewRecyclerView;
    private Button btnAddToCart;

    private DisplayReviewAdapter reviewAdapter;
    private BookImageAdapter imageAdapter;
    private ApiService apiService;
    private int bookId;
    private int quantity = 1;
    private boolean isDescriptionExpanded = false;
    private List<Review> fullReviewList = new ArrayList<>();
    private boolean isReviewExpanded = false;
    private List<Category> categoryList = new ArrayList<>();
    private Book currentBook; // Lưu tạm book để cập nhật sau

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        initViews();
        HeaderController.setupHeader(this);
        findViewById(R.id.ivReturn).setOnClickListener(v -> onBackPressed());

        apiService = RetrofitClient.getApiService();
        bookId = getIntent().getIntExtra("bookId", -1);
        if (bookId == -1) {
            Toast.makeText(this, "ID sách không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupQuantityButtons();
        setupAddToCartButton();

        fetchCategories();
        fetchBookDetail();
        fetchBookReviews();
    }

    private void initViews() {
        tvTilte = findViewById(R.id.tvTitle);
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
        btnArrowLeft = findViewById(R.id.btnArrowLeft);
        btnArrowRight = findViewById(R.id.btnArrowRight);
        tvToggleReview = findViewById(R.id.tvToggleReview); // Khởi tạo tvToggleReview

        reviewAdapter = new DisplayReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewRecyclerView.setAdapter(reviewAdapter);

        tvToggle.setOnClickListener(v -> toggleDescription());
        tvToggleReview.setOnClickListener(v -> toggleReviews()); // Thêm sự kiện click cho tvToggleReview
    }

    private void setupQuantityButtons() {
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
    }

    private void setupAddToCartButton() {
        btnAddToCart.setOnClickListener(v -> {
            String token = AuthUtils.getToken(BookDetailActivity.this);
            if (token == null) {
                Toast.makeText(BookDetailActivity.this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BookDetailActivity.this, LoginActivity.class));
                return;
            }

            // Tạo CartItem
            CartItem cartItem = new CartItem(bookId, quantity);
            List<CartItem> items = new ArrayList<>();
            items.add(cartItem);

            // Tạo Cart
            Cart cart = new Cart();
            cart.setItems(items);

            // Gọi API thêm vào giỏ hàng
            apiService.addToCart("Bearer " + token, cart).enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(BookDetailActivity.this, "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        Log.d("BookDetailActivity", "Added to cart: " + response.body().toString());
                    } else {
                        String errorMsg = "Lỗi khi thêm vào giỏ hàng";
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorJson = new JSONObject(response.errorBody().string());
                                if (errorJson.has("message")) {
                                    errorMsg = errorJson.getString("message");
                                }
                            }
                        } catch (Exception e) {
                            Log.e("BookDetailActivity", "Error parsing error body", e);
                        }
                        Toast.makeText(BookDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        if (response.code() == 401) {
                            AuthUtils.clearToken(BookDetailActivity.this);
                            startActivity(new Intent(BookDetailActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Log.e("BookDetailActivity", "Add to cart failed: " + t.getMessage(), t);
                    Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void toggleDescription() {
        isDescriptionExpanded = !isDescriptionExpanded;
        tvDescription.setMaxLines(isDescriptionExpanded ? Integer.MAX_VALUE : 5);
        tvToggle.setText(isDescriptionExpanded ? "Thu gọn" : "Xem thêm");
    }

    private void toggleReviews() {
        isReviewExpanded = !isReviewExpanded;
        displayReviews(); // Cập nhật danh sách đánh giá khi toggle
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getCategories());
                    // Cập nhật lại tiêu đề nếu sách đã được tải
                    if (currentBook != null) {
                        updateCategoryTitle(currentBook);
                    }
                } else {
                    handleError(response.errorBody(), "Không thể lấy danh mục.");
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showError(t, "Lỗi khi tải danh mục.");
            }
        });
    }

    private void fetchBookDetail() {
        apiService.getBookDetail(bookId).enqueue(new Callback<BookDetailResponse>() {
            @Override
            public void onResponse(Call<BookDetailResponse> call, Response<BookDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Book book = response.body().getBook();
                    if (book != null) {
                        currentBook = book; // Lưu book tạm thời
                        loadBookData(book);
                    }
                } else {
                    handleError(response.errorBody(), "Lỗi lấy thông tin sách.");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BookDetailResponse> call, Throwable t) {
                showError(t, "Lỗi khi tải thông tin sách.");
                finish();
            }
        });
    }

    private void fetchBookReviews() {
        apiService.getBookReviews(bookId).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullReviewList = response.body().getReviews() != null ? response.body().getReviews() : new ArrayList<>();
                    displayReviews();
                } else {
                    handleError(response.errorBody(), "Lỗi lấy danh sách đánh giá.");
                    reviewAdapter.setReviews(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                showError(t, "Lỗi khi tải đánh giá.");
                reviewAdapter.setReviews(new ArrayList<>());
            }
        });
    }

    private void displayReviews() {
        List<Review> toDisplay;
        if (isReviewExpanded) {
            toDisplay = new ArrayList<>(fullReviewList); // Sao chép toàn bộ danh sách
            tvToggleReview.setText("Thu gọn");
        } else {
            toDisplay = fullReviewList.size() > 5 ? new ArrayList<>(fullReviewList.subList(0, 5)) : new ArrayList<>(fullReviewList);
            tvToggleReview.setText("Xem tất cả");
        }
        reviewAdapter.setReviews(toDisplay);
        tvToggleReview.setVisibility(fullReviewList.size() > 5 ? View.VISIBLE : View.GONE);
    }

    private void updateCategoryTitle(Book book) {
        String categoryName = "Unknown Category";
        for (Category category : categoryList) {
            if (category.getId() == book.getCategoryId()) {
                categoryName = category.getName();
                break;
            }
        }
        tvTilte.setText(categoryName);
    }

    private void loadBookData(Book book) {
        // Ảnh sách
        List<Image> imageList = book.getImages() != null ? book.getImages() : new ArrayList<>();
        imageAdapter = new BookImageAdapter(this, imageList);
        imageViewPager.setAdapter(imageAdapter);

        setupImagePagerNavigation();

        // Cập nhật tiêu đề danh mục
        updateCategoryTitle(book);

        // Thông tin sách
        txtBookName.setText(book.getName());
        txtAuthor.setText("Tác giả: " + String.join(", ", book.getAuthor()));
        // Rating
        if (book.getRatingCount() == 0) {
            ratingBar.setVisibility(View.GONE);
            tvRating.setText("Chưa có đánh giá");
            tvNumberReview.setText("");
        } else {
            ratingBar.setVisibility(View.VISIBLE);
            float averageRating = (float) book.getAverageRating();
            ratingBar.setRating(averageRating);
            tvRating.setText(String.format(Locale.US, "%.1f/5", averageRating));
            tvNumberReview.setText(String.format(Locale.US, "(%d đánh giá)", book.getRatingCount()));
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvPrice.setText(currencyFormat.format(book.getPrice()));

        String description = book.getDescription();
        tvDescription.setText(description != null ? description : "Không có mô tả");
        tvToggle.setVisibility(description != null && description.length() > 100 ? View.VISIBLE : View.GONE);
    }

    private void setupImagePagerNavigation() {
        int totalImages = imageAdapter.getItemCount();
        tabIndicator.setText(totalImages > 0 ? "1/" + totalImages : "0/0");

        imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setText((position + 1) + "/" + totalImages);
            }
        });

        imageAdapter.setOnImageClickListener(position -> {
            int nextPosition = (position + 1) % imageAdapter.getItemCount();
            imageViewPager.setCurrentItem(nextPosition, true);
        });

        btnArrowLeft.setOnClickListener(v -> {
            int current = imageViewPager.getCurrentItem();
            imageViewPager.setCurrentItem(current > 0 ? current - 1 : totalImages - 1, true);
        });

        btnArrowRight.setOnClickListener(v -> {
            int current = imageViewPager.getCurrentItem();
            imageViewPager.setCurrentItem(current < totalImages - 1 ? current + 1 : 0, true);
        });
    }

    private void handleError(okhttp3.ResponseBody errorBody, String defaultMsg) {
        String errorMessage = defaultMsg;
        try {
            if (errorBody != null) {
                JSONObject errorJson = new JSONObject(errorBody.string());
                if (errorJson.has("msg")) {
                    errorMessage = errorJson.getString("msg");
                }
            }
        } catch (Exception e) {
            Log.e("BookDetailActivity", "Error parsing error body", e);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void showError(Throwable t, String defaultMsg) {
        String errorMessage = t instanceof java.io.IOException ? "Lỗi mạng" : defaultMsg;
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.e("BookDetailActivity", "API error: " + t.getMessage(), t);
    }
}