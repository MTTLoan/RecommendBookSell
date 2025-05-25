package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.app.utils.AuthUtils;
import com.example.app.utils.HeaderController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private BannerAdapter bannerAdapter;
    private Handler handler;
    private Runnable autoSlideRunnable;

    private RecyclerView bestSellersRecyclerView;
    private RecyclerView newBooksRecyclerView;
    private RecyclerView recommendationsRecyclerView;
    private BookAdapter bestSellersAdapter;
    private BookAdapter newBooksAdapter;
    private BookAdapter recommendationsAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private List<Book> bestSellersList = new ArrayList<>();
    private List<Book> newBooksList = new ArrayList<>();
    private List<Book> recommendationList = new ArrayList<>();

    private ApiService apiService;

    // Constants
    private static final int CATEGORY_RECOMMENDATION = 0;
    private static final int CATEGORY_BEST_SELLERS = 7;
    private static final int CATEGORY_NEW_BOOKS = 8;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        HeaderController.setupHeader(view, requireActivity());

        apiService = RetrofitClient.getApiService();

        setupBanner(view);
        setupNewBooksRecyclerView(view);
        setupBestSellersRecyclerView(view);
        setupRecommendationsRecyclerView(view);
        fetchCategories();
        fetchBooks();

        // Đặt tiêu đề tháng hiện tại
        TextView newBooksTitle = view.findViewById(R.id.newBooksTitle);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        String currentMonth = sdf.format(Calendar.getInstance().getTime());
        newBooksTitle.setText("Sách mới nhất tháng " + currentMonth);

        return view;
    }

    private void setupBanner(View view) {
        viewPager2 = view.findViewById(R.id.bannerSlider);
        List<Integer> imageList = List.of(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3);
        bannerAdapter = new BannerAdapter(imageList);
        viewPager2.setAdapter(bannerAdapter);
        startAutoSlide(imageList.size());
    }

    private void setupNewBooksRecyclerView(View view) {
        newBooksRecyclerView = view.findViewById(R.id.newBooksRecyclerView);
        newBooksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        newBooksRecyclerView.setNestedScrollingEnabled(false);
        newBooksAdapter = new BookAdapter(requireContext(), newBooksList, categoryList, 0);
        newBooksRecyclerView.setAdapter(newBooksAdapter);

        TextView newBooksTitle = view.findViewById(R.id.newBooksTitle);
        newBooksTitle.setOnClickListener(v -> openListBookActivity(CATEGORY_NEW_BOOKS)); // Thêm sự kiện click
    }

    private void setupBestSellersRecyclerView(View view) {
        bestSellersRecyclerView = view.findViewById(R.id.bestSellersRecyclerView);
        bestSellersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        bestSellersRecyclerView.setNestedScrollingEnabled(false);
        bestSellersAdapter = new BookAdapter(requireContext(), bestSellersList, categoryList, 1);
        bestSellersRecyclerView.setAdapter(bestSellersAdapter);

        TextView bestSellersTitle = view.findViewById(R.id.bestSellersTitle);
        bestSellersTitle.setText("Sách bán chạy nhất");
        bestSellersTitle.setOnClickListener(v -> openListBookActivity(CATEGORY_BEST_SELLERS));
    }

    private void setupRecommendationsRecyclerView(View view) {
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        recommendationsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false));
        recommendationsRecyclerView.setNestedScrollingEnabled(false);
        recommendationsAdapter = new BookAdapter(requireContext(), recommendationList, categoryList, 0);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);

        View recommendationsTitle = view.findViewById(R.id.recommendationsTitle);
        recommendationsTitle.setOnClickListener(v -> openListBookActivity(CATEGORY_RECOMMENDATION));
    }

    private void openListBookActivity(int categoryId) {
        Intent intent = new Intent(requireContext(), ListBookActivity.class);
        intent.putExtra("category_id", categoryId);
        String name = categoryId == CATEGORY_RECOMMENDATION ? "Đề xuất dành riêng cho bạn" :
                categoryId == CATEGORY_BEST_SELLERS ? "Sách bán chạy nhất" :
                        categoryId == CATEGORY_NEW_BOOKS ? "Sách mới nhất tháng" : "Danh mục khác";
        intent.putExtra("category_name", name);

        List<Book> bookList = categoryId == CATEGORY_RECOMMENDATION ? recommendationList :
                categoryId == CATEGORY_BEST_SELLERS ? bestSellersList :
                        categoryId == CATEGORY_NEW_BOOKS ? newBooksList : new ArrayList<>();
        if (bookList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải sách, vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putParcelableArrayListExtra("book_list", new ArrayList<>(bookList));

        startActivity(intent);
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getCategories());
                    Log.d("HomeFragment", "Categories loaded: " + categoryList.size());
                    bestSellersAdapter.setCategories(new ArrayList<>(categoryList));
                    newBooksAdapter.setCategories(new ArrayList<>(categoryList));
                    recommendationsAdapter.setCategories(new ArrayList<>(categoryList));
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
        fetchBestSellersBooks();
        fetchNewBooks();
        fetchRecommendations();
    }

    private void fetchBestSellersBooks() {
        Call<BookResponse> call = apiService.getBestSellers();
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bestSellersList.clear();
                    List<Book> books = response.body().getBooks();
                    if (books != null) {
                        bestSellersList.addAll(books);
                        Log.d("HomeFragment", "Best Sellers loaded: " + books.size() + " books");
                        for (Book book : books) {
                            Log.d("HomeFragment", "Book ID: " + book.getId() + ", Category ID: " + book.getCategoryId());
                        }
                    } else {
                        Log.w("HomeFragment", "Best Sellers books is null");
                    }
                    bestSellersAdapter.setBooks(bestSellersList);
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
        Call<BookResponse> call = apiService.getNewBooks();
        call.enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newBooksList.clear();
                    List<Book> books = response.body().getBooks();
                    if (books != null) {
                        newBooksList.addAll(books);
                        Log.d("HomeFragment", "New Books loaded: " + books.size() + " books");
                        for (Book book : books) {
                            Log.d("HomeFragment", "New Book ID: " + book.getId() + ", Category ID: " + book.getCategoryId());
                        }
                    } else {
                        Log.w("HomeFragment", "New Books is null");
                    }
                    newBooksAdapter.setBooks(newBooksList);
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

    private void fetchRecommendations() {
        String token = AuthUtils.getToken(requireContext());
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem đề xuất.", Toast.LENGTH_SHORT).show();
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
                        Log.d("HomeFragment", "Recommendations loaded: " + books.size() + " books");
                    } else {
                        Log.w("HomeFragment", "Recommendations books is null");
                    }
                    recommendationsAdapter.setBooks(recommendationList);
                } else {
                    showApiError("Không thể lấy sách đề xuất.", response);
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                showFailureError("sách đề xuất", t);
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
        handler = new Handler();
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
        if (handler != null) {
            handler.removeCallbacks(autoSlideRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null) {
            handler.postDelayed(autoSlideRunnable, 4000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(autoSlideRunnable);
        }
    }
}