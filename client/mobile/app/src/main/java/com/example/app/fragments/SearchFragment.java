package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.app.R;
import com.example.app.activities.ListBookActivity;
import com.example.app.adapters.BookAdapter;
import com.example.app.adapters.CategoryAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.response.BookResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.HeaderController;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private SearchView searchView;
    private ImageView btnFilter;
    private RecyclerView searchResultsRecyclerView;
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView categoryRecyclerView;
    private BookAdapter searchResultsAdapter;
    private BookAdapter recommendationsAdapter;
    private CategoryAdapter categoryAdapter;
    private TextView recommendationsTitle;
    private List<Book> bookList;
    private List<Book> recommendationList;
    private List<Category> categoryList;
    private TextView noResultsText;
    private ApiService apiService;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    // Filter state
    private Integer selectedCategoryId = null;
    private float minPrice = 0f;
    private float maxPrice = 700000f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // Initialize views
        searchView = view.findViewById(R.id.searchView);
        btnFilter = view.findViewById(R.id.btnFilter);
        recommendationsTitle = view.findViewById(R.id.recommendationsTitle);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        noResultsText = view.findViewById(R.id.noResultsText);

        // Initialize API service
        apiService = RetrofitClient.getApiService();

        // Set up RecyclerView for header
        HeaderController.setupHeader(requireActivity());

        // Initialize lists
        bookList = new ArrayList<>();
        recommendationList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Setup RecyclerViews
        setupRecyclerViews();

        // Load data from API: Fetch categories first, then books
        fetchCategories();

        // Setup SearchView
        setupSearchView();

        // Setup Filter Button
        setupFilterButton();

        // Setup Recommendations Title click
        setupRecommendationsTitle();

        // Clear search when back button is pressed
        searchView.setOnCloseListener(() -> {
            clearSearch();
            return false;
        });

        return view;
    }

    private void fetchCategories() {
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCategories() != null) {
                    categoryList = response.body().getCategories();
                    categoryAdapter.setCategories(categoryList);
                    searchResultsAdapter.setCategories(categoryList);
                    recommendationsAdapter.setCategories(categoryList);
                    Log.d(TAG, "Fetched categories: " + categoryList.size() + ", Categories: " + categoryList.toString());
                    fetchBooks(null);
                } else {
                    Log.e(TAG, "Failed to fetch categories: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                    categoryList = new ArrayList<>();
                    categoryAdapter.setCategories(categoryList);
                    searchResultsAdapter.setCategories(categoryList);
                    recommendationsAdapter.setCategories(categoryList);
                    fetchBooks(null);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                categoryList = new ArrayList<>();
                categoryAdapter.setCategories(categoryList);
                searchResultsAdapter.setCategories(categoryList);
                recommendationsAdapter.setCategories(categoryList);
                fetchBooks(null);
            }
        });
    }

    private void fetchBooks(Integer categoryId) {
        Call<BookResponse> call = apiService.getBooks(categoryId);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBooks() != null) {
                    bookList = response.body().getBooks();
                    recommendationList = getTopRatedRecommendations(bookList, 5);
                    recommendationsAdapter.updateList(recommendationList);
                    if (categoryId == null) {
                        searchResultsRecyclerView.setVisibility(View.GONE);
                        noResultsText.setVisibility(View.GONE);
                    } else {
                        searchResultsAdapter.updateList(bookList);
                    }
                    Log.d(TAG, "Fetched books: " + bookList.size() + ", Recommendations: " + recommendationList.size());
                } else {
                    Log.e(TAG, "Failed to fetch books: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Không thể tải sách", Toast.LENGTH_SHORT).show();
                    bookList = new ArrayList<>();
                    recommendationList = new ArrayList<>();
                    recommendationsAdapter.updateList(recommendationList);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                bookList = new ArrayList<>();
                recommendationList = new ArrayList<>();
                recommendationsAdapter.updateList(recommendationList);
            }
        });
    }

    private List<Book> getTopRatedRecommendations(List<Book> books, int count) {
        if (books == null || books.isEmpty()) return new ArrayList<>();
        List<Book> topRatedBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAverageRating() == 5.0) {
                topRatedBooks.add(book);
            }
        }
        Collections.shuffle(topRatedBooks, new Random());
        return topRatedBooks.subList(0, Math.min(count, topRatedBooks.size()));
    }

    private void setupRecyclerViews() {
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        searchResultsAdapter = new BookAdapter(requireContext(), new ArrayList<>(), categoryList, 0);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationsAdapter = new BookAdapter(requireContext(), recommendationList != null ? recommendationList : new ArrayList<>(), categoryList, 0);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);

        categoryRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList != null ? categoryList : new ArrayList<>());
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
                searchHandler.removeCallbacks(searchRunnable);
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> searchBooks(newText);
                searchHandler.postDelayed(searchRunnable, 500);
                return true;
            }
        });
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.filter_dialog, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Initialize views
        ImageView ivBack = bottomSheetView.findViewById(R.id.ivBack);
        TextView tvReset = bottomSheetView.findViewById(R.id.tvReset);
        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.chipGroup);
        RangeSlider sliderPrice = bottomSheetView.findViewById(R.id.sliderPrice);
        TextView tvPriceRange = bottomSheetView.findViewById(R.id.tvPriceRange);
        Button btnApply = bottomSheetView.findViewById(R.id.btnApply);

        // Populate ChipGroup with categories
        for (Category category : categoryList) {
            Chip chip = new Chip(requireContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_background_selector); // Define in res/color
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_selector)); // Define in res/color
            chip.setId(category.getId());
            chipGroup.addView(chip);
            if (selectedCategoryId != null && category.getId() == selectedCategoryId) {
                chip.setChecked(true);
            }
        }

        // Set initial price range
        sliderPrice.setValues(minPrice, maxPrice);
        updatePriceRangeText(tvPriceRange, minPrice, maxPrice);

        // Price Slider Listener
        sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            float newMinPrice = values.get(0);
            float newMaxPrice = values.get(1);
            updatePriceRangeText(tvPriceRange, newMinPrice, newMaxPrice);
        });

        // ChipGroup Listener
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedCategoryId = checkedId == View.NO_ID ? null : checkedId;
        });

        // Reset Button
        tvReset.setOnClickListener(v -> {
            selectedCategoryId = null;
            minPrice = 0f;
            maxPrice = 700000f;
            chipGroup.clearCheck();
            sliderPrice.setValues(0f, 700000f);
            updatePriceRangeText(tvPriceRange, minPrice, maxPrice);
            String currentQuery = searchView.getQuery().toString().trim();
            if (!currentQuery.isEmpty() || selectedCategoryId != null || minPrice > 0 || maxPrice < 700000f) {
                searchBooks(currentQuery);
            } else {
                clearSearch();
            }
        });

        // Apply Button
        btnApply.setOnClickListener(v -> {
            List<Float> values = sliderPrice.getValues();
            minPrice = values.get(0);
            maxPrice = values.get(1);
            String currentQuery = searchView.getQuery().toString().trim();
            if (!currentQuery.isEmpty() || selectedCategoryId != null || minPrice > 0 || maxPrice < 700000f) {
                searchBooks(currentQuery);
            } else {
                clearSearch();
            }
            bottomSheetDialog.dismiss();
        });

        // Back Button
        ivBack.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Show the bottom sheet
        bottomSheetDialog.show();

        // Back Button
        ivBack.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Show the bottom sheet
        bottomSheetDialog.show();
    }

    private void updatePriceRangeText(TextView tvPriceRange, float min, float max) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String priceRange = formatter.format(min) + " - " + formatter.format(max) + "đ";
        tvPriceRange.setText(priceRange);
    }

    private void setupRecommendationsTitle() {
        recommendationsTitle.setOnClickListener(v -> {
            if (recommendationList == null || recommendationList.isEmpty()) {
                Toast.makeText(getContext(), "Đang tải sách đề xuất, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(requireContext(), ListBookActivity.class);
            intent.putExtra("category_id", 0);
            intent.putExtra("category_name", "Đề xuất dành riêng cho bạn");
            intent.putExtra("book_list", new ArrayList<>(recommendationList));
            startActivity(intent);
        });
    }

    private void searchBooks(String query) {
        if (query == null) {
            query = ""; // Handle null query to avoid NPE
        }
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty() && selectedCategoryId == null && minPrice == 0f && maxPrice == 700000f) {
            clearSearch();
            return;
        }

        Log.d(TAG, "SearchBooks - Query: " + trimmedQuery);
        Log.d(TAG, "SearchBooks - CategoryId: " + selectedCategoryId);
        Log.d(TAG, "SearchBooks - Price Range: " + minPrice + " to " + maxPrice);

        Call<BookResponse> call = apiService.searchBooks(trimmedQuery, selectedCategoryId, minPrice, maxPrice);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful()) {
                    BookResponse bookResponse = response.body();
                    if (bookResponse != null) {
                        List<Book> filteredList = bookResponse.getBooks();
                        Log.d(TAG, "Response Body: success=" + (bookResponse.isSuccess() ? "true" : "false") +
                                ", msg=" + (bookResponse.getMsg() != null ? bookResponse.getMsg() : "null") +
                                ", books=" + (filteredList != null ? filteredList.size() : "null"));
                        if (filteredList != null && !filteredList.isEmpty()) {
                            noResultsText.setVisibility(View.GONE);
                            searchResultsRecyclerView.setVisibility(View.VISIBLE);
                            searchResultsAdapter.updateList(filteredList);
                        } else {
                            noResultsText.setVisibility(View.VISIBLE);
                            searchResultsRecyclerView.setVisibility(View.GONE);
                            searchResultsAdapter.updateList(new ArrayList<>());
                            Toast.makeText(getContext(), bookResponse.getMsg() != null ? bookResponse.getMsg() : "Không tìm thấy sách phù hợp", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Response body is null");
                        noResultsText.setVisibility(View.VISIBLE);
                        searchResultsRecyclerView.setVisibility(View.GONE);
                        searchResultsAdapter.updateList(new ArrayList<>());
                        Toast.makeText(getContext(), "Không thể tìm kiếm sách: Dữ liệu trả về trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to search books: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    noResultsText.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    searchResultsAdapter.updateList(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e(TAG, "Search API call failed: " + t.getMessage());
                noResultsText.setVisibility(View.VISIBLE);
                searchResultsRecyclerView.setVisibility(View.GONE);
                searchResultsAdapter.updateList(new ArrayList<>());
                Toast.makeText(getContext(), "Lỗi kết nối khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSearch() {
        noResultsText.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        searchView.clearFocus();
        searchView.setQuery("", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!searchView.getQuery().toString().isEmpty()) {
            clearSearch();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}