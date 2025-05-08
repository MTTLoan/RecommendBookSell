package com.example.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.CategoryAdapter;
import com.example.app.R;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.utils.HeaderController;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private ImageView btnFilter;
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView categoryRecyclerView;
    private BookAdapter recommendationsAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Book> bookList;
    private List<Category> categoryList;
    private TextView noResultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchView = findViewById(R.id.searchView);
        btnFilter = findViewById(R.id.btnFilter);
        recommendationsRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        noResultsText = findViewById(R.id.noResultsText);

        // Initialize data
        initData();

        // Set up RecyclerView for header
        HeaderController.setupHeader(this);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup SearchView
        setupSearchView();

        // Setup Filter Button
        setupFilterButton();
    }

    private void initData() {
        // Sample category data
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Sách thiếu nhi", "Fictional books", "https://example.com/category_fiction.jpg"));
        categoryList.add(new Category(2, "Sách văn học", "Non-fictional books", "https://example.com/category_nonfiction.jpg"));
        categoryList.add(new Category(3, "Sách kinh tế", "Science books", "https://example.com/category_science.jpg"));
        categoryList.add(new Category(1, "Sách thiếu nhi", "Fictional books", "https://example.com/category_fiction.jpg"));
        categoryList.add(new Category(2, "Sách văn học", "Non-fictional books", "https://example.com/category_nonfiction.jpg"));
        categoryList.add(new Category(3, "Sách kinh tế", "Science books", "https://example.com/category_science.jpg"));

        // Sample book data
        bookList = new ArrayList<>();

        // Book 1
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Cùng con trưởng thành - Mình không thích bị cô lập", "Rèn luyện kỹ năng sống từ sớm giúp thúc đẩy quá trình hình thành và phát triển nhân cách sau này của trẻ.", images1, 28000.0, 3.5, 2, 10, 1, "2025-04-28 02:27:21", "Nguyễn Nhật Ánh"));

        // Book 2
        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/12/34/56/789abc123def456ghi789jkl.png", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Bé tập làm quen với toán học", "Sách giúp trẻ làm quen với các khái niệm toán học cơ bản qua hình ảnh sinh động.", images2, 35000.0, 4.0, 5, 15, 1, "2025-04-29 10:15:30", "Nguyễn Nhật Ánh"));
        
    }

    private void setupRecyclerViews() {
        // Recommendations RecyclerView
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recommendationsAdapter = new BookAdapter(this, bookList, categoryList);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);

        // Category RecyclerView
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            Toast.makeText(this, "Selected: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                searchView.setBackgroundColor(getResources().getColor(R.color.search_background_focused));
            } else {
                searchView.setBackgroundColor(getResources().getColor(R.color.light_dark));
            }
        });
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(SearchActivity.this, "Filter clicked", Toast.LENGTH_SHORT).show();
            // Implement filter dialog or activity here
        });
    }

    private void filterBooks(String query) {
        List<Book> filteredList = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getName().toLowerCase().contains(query.toLowerCase()) || book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }

        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE); // Show "No results" message
            recommendationsRecyclerView.setVisibility(View.GONE); // Hide RecyclerView
        } else {
            noResultsText.setVisibility(View.GONE); // Hide "No results" message
            recommendationsRecyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            recommendationsAdapter.updateList(filteredList); // Update RecyclerView with filtered results
        }
    }
}
