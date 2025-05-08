package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.utils.AuthUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        if (AuthUtils.getToken(this) != null) {
            startActivity(new Intent(this, Menu.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish(); // Đóng SplashActivity
    }
}