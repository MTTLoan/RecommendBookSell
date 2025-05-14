package com.example.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.ReviewAdapter;
import com.example.app.models.Book;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerReviews;
    private ReviewAdapter reviewAdapter;
    private List<Book> bookList;
    private Button btnSubmitReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerReviews = findViewById(R.id.recycler_reviews);
        btnSubmitReview = findViewById(R.id.btn_submit_review);

        // Khởi tạo danh sách sách
        bookList = new ArrayList<>();

        // Thiết lập RecyclerView
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, bookList);
        recyclerReviews.setAdapter(reviewAdapter);

        // Tải dữ liệu sách ảo (không có đánh giá)
        loadFakeBooks();

        // Log để kiểm tra dữ liệu
        Log.d("ReviewActivity", "bookList size: " + bookList.size());

        reviewAdapter.notifyDataSetChanged();

        // Hiển thị tiêu đề
        setTitle("Đánh Giá Đơn Hàng");

        // Xử lý khi nhấn nút "Gửi"
        btnSubmitReview.setOnClickListener(v -> {
            // Lấy dữ liệu từ tất cả các item trong RecyclerView
            for (int i = 0; i < recyclerReviews.getChildCount(); i++) {
                ReviewAdapter.ReviewViewHolder holder = (ReviewAdapter.ReviewViewHolder) recyclerReviews.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    // Lấy dữ liệu từ RatingBar và các EditText
                    float rating = holder.ratingBar.getRating();
                    String content = holder.etContent.getText().toString().trim();
                    String packaging = holder.etPackaging.getText().toString().trim();
                    String shipping = holder.etShipping.getText().toString().trim();
                    String comment = holder.etComment.getText().toString().trim();

                    // Nối các đánh giá thành một chuỗi
                    String fullComment = content + " | " + packaging + " | " + shipping + " | " + comment;

                    // Lấy thông tin sách
                    Book book = bookList.get(i);
                    String bookName = book.getName();

                    // Giả lập lưu vào cơ sở dữ liệu (log)
                    Log.d("ReviewActivity", "Book: " + bookName + ", Rating: " + rating + ", Comment: " + fullComment);

                    // TODO: Lưu vào cơ sở dữ liệu thực tế tại đây
                    // Ví dụ: Gửi fullComment và rating lên server hoặc lưu vào SQLite
                }
            }
        });
    }


    // Tải dữ liệu sách ảo (không có đánh giá)
    private void loadFakeBooks() {
        // Tạo danh sách ảnh cho sách 1
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://example.com/image1.jpg", "Image 1 alt"));

        // Tạo danh sách ảnh cho sách 2
        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://example.com/image2.jpg", "Image 2 alt"));

        // Thêm sách vào danh sách
        bookList.add(new Book(
                1,
                "Sách Mindset Tâm Lý Học Thành Công - Carol Dw...",
                "Mô tả sách 1",
                images1,
                100000,
                5.0, // averageRating
                10,  // ratingCount
                50,  // stockQuantity
                1,   // categoryId
                "2025-04-30",
                "Carol Dweck" // author
        ));
        bookList.add(new Book(
                2,
                "Sách Tâm Lý Học Về Tiền",
                "Mô tả sách 2",
                images2,
                120000,
                5.0, // averageRating
                8,   // ratingCount
                30,  // stockQuantity
                1,   // categoryId
                "2025-04-30",
                "Tác giả 2" // author
        ));
    }
}