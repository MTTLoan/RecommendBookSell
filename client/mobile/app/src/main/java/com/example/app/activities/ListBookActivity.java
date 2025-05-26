package com.example.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.response.BookResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListBookActivity extends AppCompatActivity {
    private BookAdapter listBookAdapter;
    private RecyclerView listBookRecyclerView;
    private List<Book> bookList;
    private List<Book> filteredBookList;
    private ImageView ivReturn;
    private TextView tvTitle;
    private ApiService apiService;
    private List<Category> categoryList;

    private static final int CATEGORY_RECOMMENDATION = 0;
    private static final int CATEGORY_BEST_SELLERS = 7;
    private static final int CATEGORY_NEW_BOOKS = 8; // Thêm category cho sách mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbook);

        // Initialize views
        listBookRecyclerView = findViewById(R.id.listBookRecyclerView);
        ivReturn = findViewById(R.id.ivReturn);
        tvTitle = findViewById(R.id.tvTitle);

        // Get data from Intent
        int categoryId = getIntent().getIntExtra("category_id", 0);
        String categoryName = getIntent().getStringExtra("category_name");
        List<Book> bookListFromIntent = getIntent().getParcelableArrayListExtra("book_list"); // Use Parcelable

        // Set title
        if (categoryName == null || categoryName.equals("Unknown")) {
            categoryName = categoryId == CATEGORY_RECOMMENDATION ? "Đề xuất dành riêng cho bạn" :
                    categoryId == CATEGORY_BEST_SELLERS ? "Sách bán chạy nhất" :
                            categoryId == CATEGORY_NEW_BOOKS ? "Sách mới nhất tháng" : "Đang tải danh mục...";
        }
        tvTitle.setText(categoryName);

        // Set return button listener
        ivReturn.setOnClickListener(v -> finish());

        // Initialize lists
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Setup RecyclerView
        listBookAdapter = new BookAdapter(this, filteredBookList, categoryList, 0);
        listBookRecyclerView.setHasFixedSize(true);
        listBookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listBookRecyclerView.setAdapter(listBookAdapter);

        // Initialize ApiService
        apiService = RetrofitClient.getApiService();

        // Handle based on categoryId
        if (categoryId == CATEGORY_BEST_SELLERS) {
            fetchBestSellers(); // Fetch best sellers directly
        } else if (categoryId == CATEGORY_NEW_BOOKS) {
            fetchNewBooks(); // Fetch new books directly
        } else if (bookListFromIntent != null && !bookListFromIntent.isEmpty()) {
            filteredBookList.clear();
            filteredBookList.addAll(bookListFromIntent);
            listBookAdapter.notifyDataSetChanged();
            fetchCategories(categoryId); // Fetch categories for category names
        } else {
            fetchCategories(categoryId);
        }
    }

    private void fetchCategories(int categoryId) {
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getCategories());
                    listBookAdapter.setCategories(new ArrayList<>(categoryList));

                    // Update title only for non-recommendation, non-best-sellers, non-new-books categories
                    if (categoryId != CATEGORY_RECOMMENDATION && categoryId != CATEGORY_BEST_SELLERS && categoryId != CATEGORY_NEW_BOOKS) {
                        Category category = categoryList.stream()
                                .filter(c -> c.getId() == categoryId)
                                .findFirst()
                                .orElse(null);
                        if (category != null) {
                            tvTitle.setText(category.getName());
                        }
                    }

                    // Fetch books only if no book_list was provided
                    if (getIntent().getParcelableArrayListExtra("book_list") == null) {
                        fetchBooks(categoryId);
                    }
                } else {
                    showApiError("Không thể lấy danh mục.", response);
                    if (getIntent().getParcelableArrayListExtra("book_list") == null) {
                        fetchBooks(categoryId);
                    }
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showFailureError("danh mục", t);
                if (getIntent().getParcelableArrayListExtra("book_list") == null) {
                    fetchBooks(categoryId);
                }
            }
        });
    }

    private void fetchBooks(int categoryId) {
        Call<BookResponse> call = apiService.getBooks(categoryId == 0 ? null : categoryId);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBooks() != null) {
                    filteredBookList.clear();
                    filteredBookList.addAll(response.body().getBooks());
                    listBookAdapter.notifyDataSetChanged();

                    if (filteredBookList.isEmpty()) {
                        Toast.makeText(ListBookActivity.this, "Không có sách nào trong danh mục này.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showApiError("Không thể lấy danh sách sách.", response);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showFailureError("sách", t);
            }
        });
    }

    private void fetchBestSellers() {
        apiService.getBestSellers().enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBooks() != null) {
                    filteredBookList.clear();
                    filteredBookList.addAll(response.body().getBooks());
                    listBookAdapter.notifyDataSetChanged();

                    if (filteredBookList.isEmpty()) {
                        Toast.makeText(ListBookActivity.this, "Không có sách bán chạy nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showApiError("Không thể lấy sách bán chạy nhất.", response);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showFailureError("sách bán chạy nhất", t);
            }
        });
    }

    private void fetchNewBooks() {
        apiService.getNewBooks().enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBooks() != null) {
                    filteredBookList.clear();
                    filteredBookList.addAll(response.body().getBooks());
                    listBookAdapter.notifyDataSetChanged();

                    if (filteredBookList.isEmpty()) {
                        Toast.makeText(ListBookActivity.this, "Không có sách mới nào.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showApiError("Không thể lấy sách mới nhất.", response);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showFailureError("sách mới nhất", t);
            }
        });
    }

    private void showApiError(String baseMessage, Response<?> response) {
        String message = baseMessage;
        if (response.errorBody() != null) {
            try {
                message += " Mã lỗi: " + response.code() + " - " + response.errorBody().string();
            } catch (Exception e) {
                message += " (Lỗi đọc phản hồi: " + e.getMessage() + ")";
            }
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("ListBookActivity", message);
    }

    private void showFailureError(String target, Throwable t) {
        String message = "Lỗi khi gọi API lấy " + target + ": ";
        message += t instanceof java.io.IOException
                ? "Không thể kết nối đến server. Vui lòng kiểm tra mạng."
                : "Lỗi không xác định: " + t.getMessage();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("ListBookActivity", message, t);
    }
}