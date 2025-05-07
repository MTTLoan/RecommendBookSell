package com.example.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.app.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.models.Book;
import com.example.app.models.Book.Image;
import com.example.app.adapters.BannerAdapter;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Category;
import com.example.app.utils.HeaderController;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up RecyclerView for header
        HeaderController.setupHeader(this);

        viewPager2 = findViewById(R.id.bannerSlider);
        imageList = new ArrayList<>();

        // Add images to the list (replace with actual resource IDs)
        imageList.add(R.drawable.banner1);
        imageList.add(R.drawable.banner2);
        imageList.add(R.drawable.banner3);

        bannerAdapter = new BannerAdapter(imageList);
        viewPager2.setAdapter(bannerAdapter);

        // Auto slide functionality
        startAutoSlide();

        // Initialize RecyclerViews
        recommendationsRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        bestDealsRecyclerView = findViewById(R.id.bestDealsRecyclerView);

        // Setup layout managers
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize category list
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Sách thiếu nhi", "Các cuốn sách dành cho thiếu nhi, giúp phát triển tư duy và khả năng sáng tạo."));
        categoryList.add(new Category(2, "Sách văn học", "Các tác phẩm văn học nổi tiếng và ý nghĩa."));
        categoryList.add(new Category(3, "Sách khoa học", "Sách khám phá khoa học dành cho mọi lứa tuổi."));

        // Initialize book list with multiple books
        bookList = new ArrayList<>();

        // Book 1
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png",
                "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(
                1,
                "Cùng con trưởng thành - Mình không thích bị cô lập",
                "Rèn luyện kỹ năng sống từ sớm giúp thúc đẩy quá trình hình thành và phát triển nhân cách sau này của trẻ.",
                images1,
                28000.0,
                3.5,
                2,
                10,
                1,
                "2025-04-28 02:27:21",
                "Nguyễn Nhật Ánh"
        ));

        // Book 2
        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/12/34/56/789abc123def456ghi789jkl.png",
                "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(
                2,
                "Bé tập làm quen với toán học",
                "Sách giúp trẻ làm quen với các khái niệm toán học cơ bản qua hình ảnh sinh động.",
                images2,
                35000.0,
                4.0,
                5,
                15,
                1,
                "2025-04-29 10:15:30",
                "Nguyễn Nhật Ánh"
        ));

        // Book 3
        List<Book.Image> images3 = new ArrayList<>();
        images3.add(new Book.Image("https://salt.tikicdn.com/ts/product/78/90/12/345mno678pqr901stu234vwx.png",
                "345mno678pqr901stu234vwx.png"));
        bookList.add(new Book(
                3,
                "Những câu chuyện cổ tích Việt Nam",
                "Tập hợp các câu chuyện cổ tích nổi tiếng, giúp trẻ hiểu về văn hóa và đạo đức.",
                images3,
                45000.0,
                4.5,
                10,
                20,
                2,
                "2025-04-30 14:20:45",
                "Nguyễn Nhật Ánh"
        ));

        // Book 4
        List<Book.Image> images4 = new ArrayList<>();
        images4.add(new Book.Image("https://salt.tikicdn.com/ts/product/56/78/90/123yz456abc789def012ghi.png",
                "123yz456abc789def012ghi.png"));
        bookList.add(new Book(
                4,
                "Khám phá thế giới động vật",
                "Giới thiệu các loài động vật qua hình minh họa sống động và thông tin thú vị.",
                images4,
                52000.0,
                4.2,
                8,
                12,
                1,
                "2025-05-01 09:30:00",
                "Nguyễn Nhật Ánh"
        ));

        // Book 5
        List<Book.Image> images5 = new ArrayList<>();
        images5.add(new Book.Image("https://salt.tikicdn.com/ts/product/90/12/34/567jkl890mno123pqr456stu.png",
                "567jkl890mno123pqr456stu.png"));
        bookList.add(new Book(
                5,
                "Hành trình của giọt nước",
                "Câu chuyện khoa học về vòng tuần hoàn của nước, phù hợp cho trẻ em.",
                images5,
                30000.0,
                3.8,
                3,
                18,
                3,
                "2025-05-02 16:45:10",
                "Nguyễn Nhật Ánh"
        ));

        // Setup adapters
        recommendationsAdapter = new BookAdapter(this, bookList, categoryList);
        bestDealsAdapter = new BookAdapter(this, bookList, categoryList);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);
        bestDealsRecyclerView.setAdapter(bestDealsAdapter);

    }

    private void startAutoSlide() {
        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % imageList.size();
                viewPager2.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 4000); // Auto slide every 3 seconds
            }
        };
        handler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(autoSlideRunnable); // Stop auto slide when activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(autoSlideRunnable, 4000); // Restart auto slide when activity is resumed
    }

}
