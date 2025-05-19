package com.example.app.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.adapters.BookImageAdapter;
import com.example.app.adapters.DisplayReviewAdapter;
import com.example.app.models.Book;
import com.example.app.models.Image;
import com.example.app.models.Review;
import com.example.app.utils.HeaderController;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private TabLayout tabIndicator;
    private TextView txtBookName, txtAuthor, txtRating, tvPrice, tvDescription,
            tvToggle, tvRating, tvNumberReview;
    private RecyclerView reviewRecyclerView;

    private boolean isDescriptionExpanded = false;

    private int quantity = 1;
    private TextView tvQuantity;
    private ImageButton btnIncrease, btnDecrease;
    private Button btnAddToCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        initViews();
        // Set up RecyclerView for header
        HeaderController.setupHeader(this);
        findViewById(R.id.ivReturn).setOnClickListener(v -> onBackPressed());

        // Retrieve the Book object from Intent
        Book book = getIntent().getParcelableExtra("book");
        if (book != null) {
            loadBookData(book);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvQuantity = findViewById(R.id.tvQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Hiển thị số lượng ban đầu
        tvQuantity.setText(String.valueOf(quantity));

        // Nút Tăng
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // Nút Giảm
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                }
            }
        });

        // Nút Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookDetailActivity.this,
                        "Đã thêm " + quantity + " sản phẩm vào giỏ hàng",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        imageViewPager = findViewById(R.id.imageViewPager);
        tabIndicator = findViewById(R.id.tabIndicator);
        txtBookName = findViewById(R.id.txtBookName);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtRating = findViewById(R.id.txtRating);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvToggle = findViewById(R.id.tvToggle);
        tvRating = findViewById(R.id.tvRating);
        tvNumberReview = findViewById(R.id.tvNumberReview);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);

        reviewRecyclerView.setNestedScrollingEnabled(false); // Vô hiệu hóa cuộn của RecyclerView
        tvToggle.setOnClickListener(v -> toggleDescription());
    }

    private void toggleDescription() {
        if (isDescriptionExpanded) {
            tvDescription.setMaxLines(5);
            tvToggle.setText("Xem thêm");
        } else {
            tvDescription.setMaxLines(Integer.MAX_VALUE);
            tvToggle.setText("Thu gọn");
        }
        isDescriptionExpanded = !isDescriptionExpanded;
    }

    private void loadBookData(Book book) {
        // Load book images
        List<Image> imageList = book.getImages();
        BookImageAdapter imageAdapter = new BookImageAdapter(this, imageList);
        imageViewPager.setAdapter(imageAdapter);

        new TabLayoutMediator(tabIndicator, imageViewPager,
                (tab, position) -> tab.setText(String.valueOf(position + 1))
        ).attach();

        // Load book details
        txtBookName.setText(book.getName());
        txtAuthor.setText("Tác giả: " + book.getAuthor());
        txtRating.setText(String.format("★ %.1f/5", book.getAverageRating()));
        tvPrice.setText(String.format("%,.0fđ", book.getPrice()));
        tvDescription.setText(book.getDescription());
        tvToggle.setVisibility(book.getDescription().length() > 100 ? View.VISIBLE : View.GONE);

        // Load overall rating and review count
        tvRating.setText(String.format("%.1f/5", book.getAverageRating()));
        tvNumberReview.setText(String.format("(%d đánh giá)", book.getRatingCount()));

        // Simulate reviews for this book (in a real app, fetch via API using book.getId())
        List<Review> reviews = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//        reviews.add(new Review(1, book.getId(), 1, 4, "Rất hay, con tôi rất thích!", LocalDateTime.parse("2024-05-01 12:00:00", formatter)));
//        reviews.add(new Review(2, book.getId(), 2, 5, "Minh họa đẹp, giấy tốt.", LocalDateTime.parse("2024-05-02 12:00:00", formatter)));
//        reviews.add(new Review(3, book.getId(), 3, 5, "Câu chuyện hấp dẫn và dễ hiểu cho trẻ nhỏ.", LocalDateTime.parse("2024-05-03 12:00:00", formatter)));

        DisplayReviewAdapter reviewAdapter = new DisplayReviewAdapter(reviews);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();
    }
}