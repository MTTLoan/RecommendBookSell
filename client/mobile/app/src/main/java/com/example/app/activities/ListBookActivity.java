package com.example.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;

import java.util.ArrayList;
import java.util.List;

public class ListBookActivity extends AppCompatActivity {
    private BookAdapter listBookAdapter;
    private RecyclerView listBookRecyclerView;
    private List<Book> bookList;
    private List<Book> filteredBookList; // List to hold filtered books
    private ImageView ivReturn;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbook);

        // Initialize views
        listBookRecyclerView = findViewById(R.id.listBookRecyclerView);
        ivReturn = findViewById(R.id.ivReturn);
        tvTitle = findViewById(R.id.tvTitle);

        // Get category ID and name from Intent
        int categoryId = getIntent().getIntExtra("category_id", 0); // Default to 0 (all books)
        String categoryName = getIntent().getStringExtra("category_name");
        if (categoryName != null) {
            tvTitle.setText(categoryName); // Set the title to the category name
        } else {
            tvTitle.setText("Danh sách sách");
        }

        ivReturn.setOnClickListener(v -> finish());

        // Initialize book list
        bookList = new ArrayList<>();
        filteredBookList = new ArrayList<>();

        // Dummy data (as you already wrote)
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Cùng con trưởng thành - Mình không thích bị cô lập", "Rèn luyện kỹ năng sống từ sớm...", images1, 28000.0, 3.5, 2, 10, 1, "2025-04-28 02:27:21", "Tô Bảo"));

        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/5b/21/12/3d905ef72b7de07171761e4b1819543c.jpg", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Rèn luyện Kỹ Năng Sống dành cho học sinh - 25 thói quen tốt để thành công", "Sách giúp trẻ làm quen với toán học...", images2, 35000.0, 4.0, 5, 15, 1, "2025-04-29 10:15:30", "Nguyễn Nhật Ánh"));

        List<Book.Image> images3 = new ArrayList<>();
        images3.add(new Book.Image("https://salt.tikicdn.com/ts/product/16/72/77/dff96564663b63ba96b2c74b60261dcd.jpg", "345mno678pqr901stu234vwx.png"));
        bookList.add(new Book(3, "Xứ Sở Miên Man", "Tập hợp các câu chuyện cổ tích nổi tiếng...", images3, 45000.0, 4.5, 10, 20, 2, "2025-04-30 14:20:45", "Nhật Sơn"));

        List<Book.Image> images4 = new ArrayList<>();
        images4.add(new Book.Image("https://salt.tikicdn.com/ts/product/56/bc/59/f63f4561ee47a86e1843e671fc6355e5.jpg", "123yz456abc789def012ghi.png"));
        bookList.add(new Book(4, "Tuổi Thơ Dữ Dội - Tập 2", "Giới thiệu các loài động vật...", images4, 52000.0, 4.2, 8, 12, 1, "2025-05-01 09:30:00", "Mai Anh"));

        List<Book.Image> images5 = new ArrayList<>();
        images5.add(new Book.Image("https://salt.tikicdn.com/ts/product/0f/f9/70/e273b6980de4f6f550329aafe91578d8.jpg", "567jkl890mno123pqr456stu.png"));
        bookList.add(new Book(5, "Búp Sen Xanh", "Câu chuyện khoa học về vòng tuần hoàn của nước...", images5, 30000.0, 3.8, 3, 18, 3, "2025-05-02 16:45:10", "Sơn Tùng"));

        // Filter books by category ID
        if (categoryId == 0) {
            filteredBookList.addAll(bookList); // Show all books if categoryId is 0
        } else {
            for (Book book : bookList) {
                if (book.getCategoryId() == categoryId) {
                    filteredBookList.add(book);
                }
            }
        }

        // Setup adapter
        listBookAdapter = new BookAdapter(this, filteredBookList, new ArrayList<>()); // Pass filtered list

        listBookRecyclerView.setHasFixedSize(true);
        // Set layout manager and adapter
        listBookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        listBookRecyclerView.setAdapter(listBookAdapter);
    }
}