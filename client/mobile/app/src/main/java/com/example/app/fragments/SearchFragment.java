package com.example.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ImageView btnFilter;
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView categoryRecyclerView;
    private BookAdapter recommendationsAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Book> bookList;
    private List<Category> categoryList;
    private TextView noResultsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // Initialize views
        searchView = view.findViewById(R.id.searchView);
        btnFilter = view.findViewById(R.id.btnFilter);
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        noResultsText = view.findViewById(R.id.noResultsText);

        // Initialize data
        initData();

        // Set up RecyclerView for header
        HeaderController.setupHeader(requireActivity());

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup SearchView
        setupSearchView();

        // Setup Filter Button
        setupFilterButton();

        return view;
    }

    private void initData() {
        // Sample category data
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Sách thiếu nhi", "Fictional books", "https://example.com/category_fiction.jpg"));
        categoryList.add(new Category(2, "Sách văn học", "Non-fictional books", "https://example.com/category_nonfiction.jpg"));
        categoryList.add(new Category(3, "Sách kinh tế", "Science books", "https://example.com/category_science.jpg"));

        // Sample book data
        bookList = new ArrayList<>();
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Cùng con trưởng thành - Mình không thích bị cô lập", "Rèn luyện kỹ năng sống từ sớm...", images1, 28000.0, 3.5, 2, 10, 1, "2025-04-28 02:27:21", "Nguyễn Nhật Ánh"));

        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/12/34/56/789abc123def456ghi789jkl.png", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Bé tập làm quen với toán học", "Sách giúp trẻ làm quen...", images2, 35000.0, 4.0, 5, 15, 1, "2025-04-29 10:15:30", "Nguyễn Nhật Ánh"));
    }

    private void setupRecyclerViews() {
        // Recommendations RecyclerView
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationsAdapter = new BookAdapter(getContext(), bookList, categoryList);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);

        // Category RecyclerView
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            Toast.makeText(getContext(), "Selected: " + category.getName(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Filter clicked", Toast.LENGTH_SHORT).show();
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
            noResultsText.setVisibility(View.VISIBLE);
            recommendationsRecyclerView.setVisibility(View.GONE);
        } else {
            noResultsText.setVisibility(View.GONE);
            recommendationsRecyclerView.setVisibility(View.VISIBLE);
            recommendationsAdapter.updateList(filteredList);
        }
    }
}