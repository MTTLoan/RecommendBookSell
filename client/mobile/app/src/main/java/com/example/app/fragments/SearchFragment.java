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
import com.example.app.adapters.RecommendationAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.response.BookResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.example.app.utils.HeaderController;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private RecommendationAdapter recommendationsAdapter;
    private CategoryAdapter categoryAdapter;
    private TextView recommendationsTitle;
    private List<Book> bookList;
    private List<Book> recommendationList;
    private List<Category> categoryList;
    private TextView noResultsText;
    private ApiService apiService;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    // Trạng thái bộ lọc
    private Integer selectedCategoryId = null;
    private float minPrice = 0f;
    private float maxPrice = 700000f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // Khởi tạo các view
        searchView = view.findViewById(R.id.searchView);
        btnFilter = view.findViewById(R.id.btnFilter);
        recommendationsTitle = view.findViewById(R.id.recommendationsTitle);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        noResultsText = view.findViewById(R.id.noResultsText);

        // Khởi tạo API service
        apiService = RetrofitClient.getApiService();

        // Thiết lập header
        HeaderController.setupHeader(requireActivity());

        // Khởi tạo danh sách
        bookList = new ArrayList<>();
        recommendationList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Thiết lập RecyclerViews
        setupRecyclerViews();

        // Tải dữ liệu từ API: Lấy danh mục trước, sau đó lấy sách
        fetchCategories();

        // Thiết lập SearchView
        setupSearchView();

        // Thiết lập nút bộ lọc
        setupFilterButton();

        // Thiết lập sự kiện click cho tiêu đề đề xuất
        setupRecommendationsTitle();

        // Xóa tìm kiếm khi nhấn nút back
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
                    Log.d(TAG, "Đã lấy danh mục: " + categoryList.size() + ", Danh mục: " + categoryList.toString());
                    fetchBooks(null);
                } else {
                    Log.e(TAG, "Không thể lấy danh mục: " + response.code() + " - " + response.message());
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
                Log.e(TAG, "Lỗi gọi API: " + t.getMessage());
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
        // Lấy danh sách sách chung
        Call<BookResponse> call = apiService.getBooks(categoryId);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBooks() != null) {
                    bookList = response.body().getBooks();
                    Log.d(TAG, "Đã lấy sách: " + bookList.size());
                    if (categoryId == null) {
                        searchResultsRecyclerView.setVisibility(View.GONE);
                        noResultsText.setVisibility(View.GONE);
                    } else {
                        searchResultsAdapter.updateList(bookList);
                    }
                } else {
                    Log.e(TAG, "Không thể lấy sách: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Không thể tải sách", Toast.LENGTH_SHORT).show();
                    bookList = new ArrayList<>();
                    searchResultsAdapter.updateList(bookList);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi gọi API: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                bookList = new ArrayList<>();
                searchResultsAdapter.updateList(bookList);
            }
        });

        // Lấy sách đề xuất
        fetchRecommendations();
    }

    private void fetchRecommendations() {
        String token = AuthUtils.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem đề xuất.", Toast.LENGTH_SHORT).show();
            recommendationList = new ArrayList<>();
            recommendationsAdapter.setBooks(recommendationList);
            return;
        }

        Call<BookResponse> call = apiService.getRecommendations("Bearer " + token);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recommendationList.clear();
                    List<Book> books = response.body().getBooks();
                    if (books != null) {
                        recommendationList.addAll(books);
                        Log.d(TAG, "Đã tải đề xuất: " + books.size() + " sách");
                    } else {
                        Log.w(TAG, "Danh sách sách đề xuất rỗng");
                    }
                    recommendationsAdapter.setBooks(recommendationList);
                } else {
                    Log.e(TAG, "Không thể lấy sách đề xuất: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Không thể tải sách đề xuất", Toast.LENGTH_SHORT).show();
                    recommendationList = new ArrayList<>();
                    recommendationsAdapter.setBooks(recommendationList);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi gọi API đề xuất: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối khi tải sách đề xuất", Toast.LENGTH_SHORT).show();
                recommendationList = new ArrayList<>();
                recommendationsAdapter.setBooks(recommendationList);
            }
        });
    }

    private void setupRecyclerViews() {
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        searchResultsAdapter = new BookAdapter(requireContext(), new ArrayList<>(), categoryList, 0);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationsRecyclerView.setNestedScrollingEnabled(false);
        recommendationsAdapter = new RecommendationAdapter(requireContext(), recommendationList != null ? recommendationList : new ArrayList<>(), categoryList, 0);
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

        // Khởi tạo các view
        ImageView ivBack = bottomSheetView.findViewById(R.id.ivBack);
        TextView tvReset = bottomSheetView.findViewById(R.id.tvReset);
        ChipGroup chipGroup = bottomSheetView.findViewById(R.id.chipGroup);
        RangeSlider sliderPrice = bottomSheetView.findViewById(R.id.sliderPrice);
        TextView tvPriceRange = bottomSheetView.findViewById(R.id.tvPriceRange);
        Button btnApply = bottomSheetView.findViewById(R.id.btnApply);

        // Điền danh mục vào ChipGroup
        for (Category category : categoryList) {
            Chip chip = new Chip(requireContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.chip_background_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_selector));
            chip.setId(category.getId());
            chipGroup.addView(chip);
            if (selectedCategoryId != null && category.getId() == selectedCategoryId) {
                chip.setChecked(true);
            }
        }

        // Thiết lập khoảng giá ban đầu
        sliderPrice.setValues(minPrice, maxPrice);
        updatePriceRangeText(tvPriceRange, minPrice, maxPrice);

        // Lắng nghe thay đổi giá
        sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            float newMinPrice = values.get(0);
            float newMaxPrice = values.get(1);
            updatePriceRangeText(tvPriceRange, newMinPrice, newMaxPrice);
        });

        // Lắng nghe ChipGroup
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedCategoryId = checkedId == View.NO_ID ? null : checkedId;
        });

        // Nút đặt lại
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

        // Nút áp dụng
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

        // Nút quay lại
        ivBack.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Hiển thị bottom sheet
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
            intent.putParcelableArrayListExtra("book_list", new ArrayList<>(recommendationList));
            startActivity(intent);
        });
    }

    private void searchBooks(String query) {
        if (query == null) {
            query = "";
        }
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty() && selectedCategoryId == null && minPrice == 0f && maxPrice == 700000f) {
            clearSearch();
            return;
        }

        Log.d(TAG, "Tìm kiếm - Từ khóa: " + trimmedQuery);
        Log.d(TAG, "Tìm kiếm - ID danh mục: " + selectedCategoryId);
        Log.d(TAG, "Tìm kiếm - Khoảng giá: " + minPrice + " đến " + maxPrice);

        Call<BookResponse> call = apiService.searchBooks(trimmedQuery, selectedCategoryId, minPrice, maxPrice);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful()) {
                    BookResponse bookResponse = response.body();
                    if (bookResponse != null) {
                        List<Book> filteredList = bookResponse.getBooks();
                        Log.d(TAG, "Phản hồi API: success=" + (bookResponse.isSuccess() ? "true" : "false") +
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
                        Log.e(TAG, "Phản hồi API rỗng");
                        noResultsText.setVisibility(View.VISIBLE);
                        searchResultsRecyclerView.setVisibility(View.GONE);
                        searchResultsAdapter.updateList(new ArrayList<>());
                        Toast.makeText(getContext(), "Không thể tìm kiếm sách: Dữ liệu trả về trống", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Không thể tìm kiếm sách: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Lỗi API: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi đọc phản hồi: " + e.getMessage());
                        }
                    }
                    noResultsText.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.GONE);
                    searchResultsAdapter.updateList(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi gọi API tìm kiếm: " + t.getMessage());
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