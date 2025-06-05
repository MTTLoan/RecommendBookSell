package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;

public class CongratulationActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView ivReturn;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);

        tvTitle = findViewById(R.id.tvtitle);
        ivReturn = findViewById(R.id.ivReturn);
        btnLogin = findViewById(R.id.btnLogin);

        // Lấy thông tin từ Intent
        String title = getIntent().getStringExtra("title");
        if (title != null) {
            tvTitle.setText(title);
        }

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> finish());

        // Xử lý sự kiện quay lại màn hình đăng nhập
        btnLogin.setOnClickListener(v -> {
            // Điều hướng đến màn hình đăng nhập (LoginActivity)
            Intent intent = new Intent(CongratulationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Kết thúc Activity hiện tại để không thể quay lại
        });
    }
}
