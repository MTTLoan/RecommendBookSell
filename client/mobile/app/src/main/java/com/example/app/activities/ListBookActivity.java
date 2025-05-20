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
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbook);

        // Initialize views
        listBookRecyclerView = findViewById(R.id.listBookRecyclerView);
        ivReturn = findViewById(R.id.ivReturn);
        tvTitle = findViewById(R.id.tvTitle);

        // Get category ID and name from Intent
        int categoryId = getIntent().getIntExtra("category_id", 0); // Default to 0 (all books)
        String categoryName = getIntent().getStringExtra("category_name");
        if (categoryName == null || categoryName.equals("Unknown")) {
            categoryName = "Đang tải danh mục...";
        }
        tvTitle.setText(categoryName);

        ivReturn.setOnClickListener(v -> finish());

        // Initialize lists
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();

        // Setup RecyclerView
        listBookAdapter = new BookAdapter(this, filteredBookList, categoryList);
        listBookRecyclerView.setHasFixedSize(true);
        listBookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listBookRecyclerView.setAdapter(listBookAdapter);

        // Initialize ApiService
        apiService = RetrofitClient.getApiService();

        // Fetch categories first
        fetchCategories(categoryId);
    }

    private void fetchCategories(int categoryId) {
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getCategories());
                    listBookAdapter.setCategories(new ArrayList<>(categoryList)); // Cập nhật adapter với danh mục

                    // Cập nhật tiêu đề nếu danh mục được tìm thấy
                    Category category = categoryList.stream()
                            .filter(c -> c.getId() == categoryId)
                            .findFirst()
                            .orElse(null);
                    if (category != null) {
                        tvTitle.setText(category.getName());
                    }

                    // Fetch books after categories are loaded
                    fetchBooks(categoryId);
                } else {
                    showApiError("Không thể lấy danh mục.", response);
                    fetchBooks(categoryId); // Tiếp tục tải sách nếu danh mục thất bại
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showFailureError("danh mục", t);
                fetchBooks(categoryId); // Tiếp tục tải sách nếu có lỗi
            }
        });
    }

    private void fetchBooks(int categoryId) {
        Call<BookResponse> call = apiService.getBooks(categoryId == 0 ? null : categoryId); // Truyền null nếu categoryId là 0
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBooks() != null) {
                    filteredBookList.clear();
                    filteredBookList.addAll(response.body().getBooks());

                    // Update adapter
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