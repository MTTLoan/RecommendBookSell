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
import com.example.app.models.Review;
import com.example.app.utils.HeaderController;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
        loadDummyData();

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

    private void loadDummyData() {
        // Tạo danh sách ảnh
        List<Book.Image> imageList = new ArrayList<>();
        imageList.add(new Book.Image("https://via.placeholder.com/180x278.png?text=Sách+1", "Ảnh 1"));
        imageList.add(new Book.Image("https://via.placeholder.com/180x278.png?text=Sách+2", "Ảnh 2"));
        imageList.add(new Book.Image("https://via.placeholder.com/180x278.png?text=Sách+3", "Ảnh 3"));

        // Khởi tạo adapter hình ảnh
        BookImageAdapter imageAdapter = new BookImageAdapter(this, imageList);
        imageViewPager.setAdapter(imageAdapter);

        new TabLayoutMediator(tabIndicator, imageViewPager,
                (tab, position) -> tab.setText(String.valueOf(position + 1))
        ).attach();

        // Thông tin sách giả lập
        txtBookName.setText("Chuyện Cổ Tích Thế Giới");
        txtAuthor.setText("Tác giả: Nguyễn Nhật Ánh");
        txtRating.setText("★ 4.5/5");
        tvPrice.setText("132,000đ");
        tvDescription.setText("Chuyện con mèo dạy hải âu bay là kiệt tác dành cho thiếu nhi của nhà văn Chi Lê nổi tiếng Luis Sepúlveda...");
        tvToggle.setVisibility(View.VISIBLE);

        // Đánh giá tổng thể
        tvRating.setText("4.5/5");
        tvNumberReview.setText("(100 đánh giá)");

        // Danh sách đánh giá
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(1, 1, 1, 4.8f, "Rất hay, con tôi rất thích!", "2024-05-01"));
        reviews.add(new Review(2, 2, 2, 4.0f, "Minh họa đẹp, giấy tốt.", "2024-05-02"));
        reviews.add(new Review(3, 3, 3, 4.5f, "Câu chuyện hấp dẫn và dễ hiểu cho trẻ nhỏ.", "2024-05-03"));

        DisplayReviewAdapter reviewAdapter = new DisplayReviewAdapter(reviews);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();
    }
}