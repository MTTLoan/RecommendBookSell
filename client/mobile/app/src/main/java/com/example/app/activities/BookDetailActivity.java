package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.adapters.BookImageAdapter;
import com.example.app.adapters.DisplayReviewAdapter;
import com.example.app.adapters.RecommendationAdapter;
import com.example.app.models.Book;
import com.example.app.models.Cart;
import com.example.app.models.CartItem;
import com.example.app.models.Category;
import com.example.app.models.Image;
import com.example.app.models.Review;
import com.example.app.models.response.BookDetailResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.models.response.ReviewResponse;
import com.example.app.models.response.BookResponse;
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
            tvToggle, tvRating, tvNumberReview, tvQuantity, tvToggleReview, recommendationsTitle;
    private ImageButton btnArrowLeft, btnArrowRight, btnIncrease, btnDecrease;
    private RatingBar ratingBar;
    private RecyclerView reviewRecyclerView, recommendationsRecyclerView;
    private Button btnAddToCart;

    private DisplayReviewAdapter reviewAdapter;
    private BookImageAdapter imageAdapter;
    private RecommendationAdapter recommendationsAdapter;
    private ApiService apiService;
    private int bookId;
    private int quantity = 1;
    private boolean isDescriptionExpanded = false;
    private List<Review> fullReviewList = new ArrayList<>();
    private boolean isReviewExpanded = false;
    private List<Category> categoryList = new ArrayList<>();
    private List<Book> recommendationList = new ArrayList<>();
    private Book currentBook; // Lưu tạm book để cập nhật sau
    private static final int CATEGORY_RECOMMENDATION = 0;

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
        setupRecommendationsRecyclerView();

        fetchCategories();
        fetchBookDetail();
        fetchBookReviews();
        fetchRecommendations();
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
        recommendationsRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnArrowLeft = findViewById(R.id.btnArrowLeft);
        btnArrowRight = findViewById(R.id.btnArrowRight);
        tvToggleReview = findViewById(R.id.tvToggleReview);
        recommendationsTitle = findViewById(R.id.recommendationsTitle);

        reviewAdapter = new DisplayReviewAdapter(new ArrayList<>());
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setNestedScrollingEnabled(false);
        reviewRecyclerView.setAdapter(reviewAdapter);

        tvToggle.setOnClickListener(v -> toggleDescription());
        tvToggleReview.setOnClickListener(v -> toggleReviews());
        recommendationsTitle.setOnClickListener(v -> openListBookActivity());
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

            boolean isFromRecommendation = getIntent().getBooleanExtra("fromRecommendation", false);
            Log.d("BookDetailActivity", "isFromRecommendation: " + isFromRecommendation);

            CartItem cartItem = new CartItem(bookId, quantity, currentBook, isFromRecommendation);
            List<CartItem> items = new ArrayList<>();
            items.add(cartItem);

            Cart cart = new Cart();
            cart.setItems(items);

            apiService.addToCart("Bearer " + token, cart).enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(BookDetailActivity.this, "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        Log.d("BookDetailActivity", "Added to cart: " + response.body().toString());

                        if (isFromRecommendation) {
                            apiService.recordRecommendationAddToCart("Bearer " + token, bookId, response.body().getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Log.d("BookDetailActivity", "Add to cart recorded for bookId: " + bookId);
                                    } else {
                                        Log.e("BookDetailActivity", "Failed to record add_to_cart, status: " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("BookDetailActivity", "Error recording add to cart: " + t.getMessage());
                                }
                            });
                        }
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
                    Log.e("BookDetailActivity", "Add to cart failed: " + t.getMessage());
                    Toast.makeText(BookDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupRecommendationsRecyclerView() {
        recommendationsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        recommendationsRecyclerView.setNestedScrollingEnabled(false);
        recommendationsAdapter = new RecommendationAdapter(this, recommendationList, categoryList, 0);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);
    }

    private void toggleDescription() {
        isDescriptionExpanded = !isDescriptionExpanded;
        tvDescription.setMaxLines(isDescriptionExpanded ? Integer.MAX_VALUE : 5);
        tvToggle.setText(isDescriptionExpanded ? "Thu gọn" : "Xem thêm");
    }

    private void toggleReviews() {
        isReviewExpanded = !isReviewExpanded;
        displayReviews();
    }

    private void openListBookActivity() {
        Intent intent = new Intent(this, ListBookActivity.class);
        intent.putExtra("category_id", CATEGORY_RECOMMENDATION);
        intent.putExtra("category_name", "Đề xuất liên quan");
        intent.putParcelableArrayListExtra("book_list", new ArrayList<>(recommendationList));
        if (recommendationList.isEmpty()) {
            Toast.makeText(this, "Đang tải sách đề xuất, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getCategories());
                    recommendationsAdapter.setCategories(new ArrayList<>(categoryList));
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
                        currentBook = book;
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

    private void fetchRecommendations() {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem đề xuất.", Toast.LENGTH_SHORT).show();
            recommendationsTitle.setVisibility(View.GONE);
            recommendationsRecyclerView.setVisibility(View.GONE);
            return;
        }

        apiService.getRecommendationsByBook("Bearer " + token, bookId).enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendationList.clear();
                    List<Book> books = response.body().getBooks();
                    if (books != null) {
                        recommendationList.addAll(books);
                        Log.d("BookDetailActivity", "Recommendations loaded: " + books.size() + " books");
                    } else {
                        Log.w("BookDetailActivity", "Recommendations books is null");
                    }
                    recommendationsAdapter.setBooks(recommendationList);
                    recommendationsTitle.setVisibility(recommendationList.isEmpty() ? View.GONE : View.VISIBLE);
                    recommendationsRecyclerView.setVisibility(recommendationList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    handleError(response.errorBody(), "Không thể lấy sách đề xuất.");
                    recommendationsTitle.setVisibility(View.GONE);
                    recommendationsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showError(t, "Lỗi khi tải sách đề xuất.");
                recommendationsTitle.setVisibility(View.GONE);
                recommendationsRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void displayReviews() {
        List<Review> toDisplay;
        if (isReviewExpanded) {
            toDisplay = new ArrayList<>(fullReviewList);
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
        List<Image> imageList = book.getImages() != null ? book.getImages() : new ArrayList<>();
        imageAdapter = new BookImageAdapter(this, imageList);
        imageViewPager.setAdapter(imageAdapter);

        setupImagePagerNavigation();

        updateCategoryTitle(book);

        txtBookName.setText(book.getName());
        txtAuthor.setText("Tác giả: " + String.join(", ", book.getAuthor()));
        if (book.getRatingCount() == 0) {
            ratingBar.setVisibility(View.GONE);
            tvRating.setText("Chưa có đánh giá");
            tvRating.setTextSize(12);
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