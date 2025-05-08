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

        tvTitle.setText("Danh sách sách");

        ivReturn.setOnClickListener(v -> finish());

        // Initialize book list
        bookList = new ArrayList<>();

        // Dummy data (as you already wrote)
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Cùng con trưởng thành - Mình không thích bị cô lập", "Rèn luyện kỹ năng sống từ sớm...", images1, 28000.0, 3.5, 2, 10, 1, "2025-04-28 02:27:21", "Nguyễn Nhật Ánh"));

        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/12/34/56/789abc123def456ghi789jkl.png", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Bé tập làm quen với toán học", "Sách giúp trẻ làm quen với toán học...", images2, 35000.0, 4.0, 5, 15, 1, "2025-04-29 10:15:30", "Nguyễn Nhật Ánh"));

        List<Book.Image> images3 = new ArrayList<>();
        images3.add(new Book.Image("https://salt.tikicdn.com/ts/product/78/90/12/345mno678pqr901stu234vwx.png", "345mno678pqr901stu234vwx.png"));
        bookList.add(new Book(3, "Những câu chuyện cổ tích Việt Nam", "Tập hợp các câu chuyện cổ tích nổi tiếng...", images3, 45000.0, 4.5, 10, 20, 2, "2025-04-30 14:20:45", "Nguyễn Nhật Ánh"));

        List<Book.Image> images4 = new ArrayList<>();
        images4.add(new Book.Image("https://salt.tikicdn.com/ts/product/56/78/90/123yz456abc789def012ghi.png", "123yz456abc789def012ghi.png"));
        bookList.add(new Book(4, "Khám phá thế giới động vật", "Giới thiệu các loài động vật...", images4, 52000.0, 4.2, 8, 12, 1, "2025-05-01 09:30:00", "Nguyễn Nhật Ánh"));

        List<Book.Image> images5 = new ArrayList<>();
        images5.add(new Book.Image("https://salt.tikicdn.com/ts/product/90/12/34/567jkl890mno123pqr456stu.png", "567jkl890mno123pqr456stu.png"));
        bookList.add(new Book(5, "Hành trình của giọt nước", "Câu chuyện khoa học về vòng tuần hoàn của nước...", images5, 30000.0, 3.8, 3, 18, 3, "2025-05-02 16:45:10", "Nguyễn Nhật Ánh"));

        // Setup adapter (null for category list if not filtering)
        listBookAdapter = new BookAdapter(this, bookList, new ArrayList<>()); // or pass null if unused

        // Set layout manager and adapter
        listBookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        listBookRecyclerView.setAdapter(listBookAdapter);
    }
}
