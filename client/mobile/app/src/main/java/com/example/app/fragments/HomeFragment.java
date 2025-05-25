package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.activities.ListBookActivity;
import com.example.app.adapters.BannerAdapter;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.response.BookResponse;
import com.example.app.models.response.CategoryResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.HeaderController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private BannerAdapter bannerAdapter;
    private final Handler handler = new Handler();
    private Runnable autoSlideRunnable;

    private final Map<Integer, RecyclerView> recyclerViewMap = new HashMap<>();
    private final Map<Integer, BookAdapter> adapterMap = new HashMap<>();
    private final List<Category> categoryList = new ArrayList<>();
    private final Map<Integer, List<Book>> bookListMap = new HashMap<>(); // Store book lists for each category

    private ApiService apiService;

    // Constants
    private static final int CATEGORY_RECOMMENDATION = 0;
    private static final int CATEGORY_CHILDREN = 1;
    private static final int CATEGORY_LITERATURE = 2;
    private static final int CATEGORY_ECONOMIC = 3;
    private static final int CATEGORY_LIFE_SKILLS = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        HeaderController.setupHeader(view, requireActivity());

        apiService = RetrofitClient.getApiService();

        setupBanner(view);
        fetchCategories();
        setupRecyclerViews(view);
        fetchBooks();

        return view;
    }

    private void setupBanner(View view) {
        viewPager2 = view.findViewById(R.id.bannerSlider);
        List<Integer> imageList = List.of(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3);
        bannerAdapter = new BannerAdapter(imageList);
        viewPager2.setAdapter(bannerAdapter);
        startAutoSlide(imageList.size());
    }

    private void setupRecyclerViews(View view) {
        int[][] configs = {
                {CATEGORY_CHILDREN, R.id.childrenBooksRecyclerView, R.id.childrenBooksTitle},
                {CATEGORY_LITERATURE, R.id.literatureBooksRecyclerView, R.id.literatureBooksTitle},
                {CATEGORY_ECONOMIC, R.id.economicBooksRecyclerView, R.id.economicBooksTitle},
                {CATEGORY_LIFE_SKILLS, R.id.lifeSkillsBooksRecyclerView, R.id.lifeSkillsBooksTitle}
        };

        for (int[] config : configs) {
            int categoryId = config[0];
            int recyclerId = config[1];
            int titleId = config[2];
            setupBookSection(view, categoryId, recyclerId, titleId);
        }

        setupBookSection(view, CATEGORY_RECOMMENDATION, R.id.recommendationsRecyclerView, R.id.recommendationsTitle);

    }

    private void setupBookSection(View view, int categoryId, int recyclerViewId, int titleViewId) {
        RecyclerView recyclerView = view.findViewById(recyclerViewId);
        // Set LayoutManager based on category
        // Set LayoutManager based on category
        if (recyclerViewId == R.id.recommendationsRecyclerView) {
            // Grid layout with 2 columns for recommendationsRecyclerView
            GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            // Horizontal scrolling for other RecyclerViews
            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        // Disable nested scrolling (already set in XML, confirming programmatically)
        recyclerView.setNestedScrollingEnabled(false);
        BookAdapter adapter = new BookAdapter(requireContext(), new ArrayList<>(), categoryList);
        recyclerView.setAdapter(adapter);

        recyclerViewMap.put(categoryId, recyclerView);
        adapterMap.put(categoryId, adapter);

        View titleView = view.findViewById(titleViewId);
        titleView.setOnClickListener(v -> openListBookActivity(categoryId));
    }

    private void openListBookActivity(int categoryId) {
        Intent intent = new Intent(requireContext(), ListBookActivity.class);
        intent.putExtra("category_id", categoryId);
        String name = categoryId == CATEGORY_RECOMMENDATION
                ? "Đề xuất dành riêng cho bạn"
                : categoryList.stream()
                .filter(c -> c.getId() == categoryId)
                .map(Category::getName)
                .findFirst()
                .orElse("Unknown");
        intent.putExtra("category_name", name);

        // For recommendations, pass the filtered book list
        if (categoryId == CATEGORY_RECOMMENDATION) {
            List<Book> recommendationList = bookListMap.getOrDefault(categoryId, new ArrayList<>());
            if (recommendationList.isEmpty()) {
                Toast.makeText(getContext(), "Đang tải sách đề xuất, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra("book_list", new ArrayList<>(recommendationList));
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
                    adapterMap.values().forEach(a -> a.setCategories(new ArrayList<>(categoryList)));
                } else {
                    showApiError("Không thể lấy danh mục.", response);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showFailureError("danh mục", t);
            }
        });
    }

    private void fetchBooks() {
        int[] categoryIds = {CATEGORY_CHILDREN, CATEGORY_LITERATURE, CATEGORY_ECONOMIC, CATEGORY_LIFE_SKILLS, CATEGORY_RECOMMENDATION};
        for (int categoryId : categoryIds) {
            BookAdapter adapter = adapterMap.get(categoryId);
            if (adapter != null) {
                fetchBooksForCategory(categoryId, adapter);
            }
        }
    }

    private void fetchBooksForCategory(int categoryId, BookAdapter adapter) {
        Call<BookResponse> call = apiService.getBooks(categoryId == CATEGORY_RECOMMENDATION ? null : categoryId);
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getBooks();
                    if (categoryId == CATEGORY_RECOMMENDATION) {
                        // Filter books with averageRating == 5 (up to 5, shuffled)
                        books = getTopRatedRecommendations(books, 5);
                    }
                    bookListMap.put(categoryId, books); // Store the book list
                    adapter.setBooks(books);
                } else {
                    showApiError("Không thể lấy sách cho danh mục " + categoryId + ".", response);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showFailureError("sách cho danh mục " + categoryId, t);
            }
        });
    }

    private List<Book> getTopRatedRecommendations(List<Book> books, int count) {
        if (books == null || books.isEmpty()) return new ArrayList<>();

        // Filter books with averageRating == 5
        List<Book> topRatedBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAverageRating() == 5.0) { // Assuming getAverageRating() returns the averageRating
                topRatedBooks.add(book);
            }
        }

        // Shuffle the list to ensure variety in recommendations
        Collections.shuffle(topRatedBooks, new Random());

        // Return up to 'count' books
        return topRatedBooks.subList(0, Math.min(count, topRatedBooks.size()));
    }

    private void showApiError(String baseMessage, Response<?> response) {
        String message = baseMessage;
        if (response.errorBody() != null) {
            try {
                message += " Mã lỗi: " + response.code() + " - " + response.errorBody().string();
            } catch (IOException e) {
                message += " (Lỗi đọc phản hồi: " + e.getMessage() + ")";
            }
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Log.e("HomeFragment", message);
    }

    private void showFailureError(String target, Throwable t) {
        String message = "Lỗi khi gọi API lấy " + target + ": ";
        message += t instanceof IOException
                ? "Không thể kết nối đến server. Vui lòng kiểm tra mạng."
                : "Lỗi không xác định: " + t.getMessage();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Log.e("HomeFragment", message, t);
    }

    private void startAutoSlide(int itemCount) {
        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int next = (viewPager2.getCurrentItem() + 1) % itemCount;
                viewPager2.setCurrentItem(next, true);
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(autoSlideRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoSlideRunnable);
    }
}