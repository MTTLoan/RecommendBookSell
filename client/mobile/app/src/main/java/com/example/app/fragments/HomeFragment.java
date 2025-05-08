package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.activities.ListBookActivity;
import com.example.app.adapters.BannerAdapter;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.utils.HeaderController;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private BannerAdapter bannerAdapter;
    private List<Integer> imageList;
    private Handler handler = new Handler();
    private Runnable autoSlideRunnable;

    private RecyclerView recommendationsRecyclerView;
    private RecyclerView bestDealsRecyclerView;
    private BookAdapter recommendationsAdapter;
    private BookAdapter bestDealsAdapter;
    private List<Book> bookList;
    private List<Category> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        // Set up RecyclerView for header
        HeaderController.setupHeader(requireActivity());

        viewPager2 = view.findViewById(R.id.bannerSlider);
        imageList = new ArrayList<>();

        // Add images to the list
        imageList.add(R.drawable.banner1);
        imageList.add(R.drawable.banner2);
        imageList.add(R.drawable.banner3);

        bannerAdapter = new BannerAdapter(imageList);
        viewPager2.setAdapter(bannerAdapter);

        // Auto slide functionality
        startAutoSlide();

        // Initialize RecyclerViews
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        bestDealsRecyclerView = view.findViewById(R.id.bestDealsRecyclerView);

        // Setup layout managers
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestDealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize category list
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Sách thiếu nhi", "Fictional books", "https://example.com/category_fiction.jpg"));
        categoryList.add(new Category(2, "Sách văn học", "Non-fictional books", "https://example.com/category_nonfiction.jpg"));
        categoryList.add(new Category(3, "Sách kinh tế", "Science books", "https://example.com/category_science.jpg"));

        // Initialize book list
        bookList = new ArrayList<>();
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Cùng con trưởng thành - Mình không thích bị cô lập", "Rèn luyện kỹ năng sống từ sớm...", images1, 28000.0, 3.5, 2, 10, 1, "2025-04-28 02:27:21", "Nguyễn Nhật Ánh"));

        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/12/34/56/789abc123def456ghi789jkl.png", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Bé tập làm quen với toán học", "Sách giúp trẻ làm quen...", images2, 35000.0, 4.0, 5, 15, 1, "2025-04-29 10:15:30", "Nguyễn Nhật Ánh"));

        List<Book.Image> images3 = new ArrayList<>();
        images3.add(new Book.Image("https://salt.tikicdn.com/ts/product/78/90/12/345mno678pqr901stu234vwx.png", "345mno678pqr901stu234vwx.png"));
        bookList.add(new Book(3, "Những câu chuyện cổ tích Việt Nam", "Tập hợp các câu chuyện...", images3, 45000.0, 4.5, 10, 20, 2, "2025-04-30 14:20:45", "Nguyễn Nhật Ánh"));

        List<Book.Image> images4 = new ArrayList<>();
        images4.add(new Book.Image("https://salt.tikicdn.com/ts/product/56/78/90/123yz456abc789def012ghi.png", "123yz456abc789def012ghi.png"));
        bookList.add(new Book(4, "Khám phá thế giới động vật", "Giới thiệu các loài động vật...", images4, 52000.0, 4.2, 8, 12, 1, "2025-05-01 09:30:00", "Nguyễn Nhật Ánh"));

        List<Book.Image> images5 = new ArrayList<>();
        images5.add(new Book.Image("https://salt.tikicdn.com/ts/product/90/12/34/567jkl890mno123pqr456stu.png", "567jkl890mno123pqr456stu.png"));
        bookList.add(new Book(5, "Hành trình của giọt nước", "Câu chuyện khoa học về vòng tuần hoàn...", images5, 30000.0, 3.8, 3, 18, 3, "2025-05-02 16:45:10", "Nguyễn Nhật Ánh"));

        // Filter books
        List<Book> recommendedBooks = new ArrayList<>(bookList);
        List<Book> literatureBooks = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getCategoryId() == 2) {
                literatureBooks.add(book);
            }
        }

        // Set up click listeners
        view.findViewById(R.id.recommendationsTitle).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ListBookActivity.class);
            intent.putExtra("category_id", 0);
            intent.putExtra("category_name", "Đề xuất dành riêng cho bạn");
            startActivity(intent);
        });

        view.findViewById(R.id.bestDealsTitle).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ListBookActivity.class);
            intent.putExtra("category_id", 2);
            intent.putExtra("category_name", "Sách văn học");
            startActivity(intent);
        });

        // Setup adapters
        recommendationsAdapter = new BookAdapter(getContext(), bookList, categoryList);
        bestDealsAdapter = new BookAdapter(getContext(), bookList, categoryList);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);
        bestDealsRecyclerView.setAdapter(bestDealsAdapter);

        return view;
    }

    private void startAutoSlide() {
        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % imageList.size();
                viewPager2.setCurrentItem(nextItem, true);
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
}