package com.example.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforUserActivity extends AppCompatActivity {

    private TextView tvEditPersonalInfoLabel;
    private TextView tvChangePasswordLabel;
    private TextView tvLogOut;
    private SharedPreferences prefs;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inforuser);

        // Initialize views
        tvEditPersonalInfoLabel = findViewById(R.id.tvEditPersonalInfoLabel);
        tvChangePasswordLabel = findViewById(R.id.tvChangePasswordLabel);
        tvLogOut = findViewById(R.id.tvlogOut);

        // Initialize SharedPreferences and ApiService
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        apiService = RetrofitClient.getApiService();

        // Debug: Check if token exists
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Token không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            Log.d("InforUserActivity", "Token retrieved: " + token);
            Toast.makeText(this, "Token found: " + token.substring(0, 10) + "...", Toast.LENGTH_LONG).show();
        }

        // Set click listeners
        tvEditPersonalInfoLabel.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển đến màn hình sửa thông tin cá nhân", Toast.LENGTH_SHORT).show();
            // Replace with Intent to EditPersonalInfoActivity
            // Intent intent = new Intent(this, EditPersonalInfoActivity.class);
            // startActivity(intent);
        });

        tvChangePasswordLabel.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển đến màn hình đổi mật khẩu", Toast.LENGTH_SHORT).show();
            // Replace with Intent to ChangePasswordActivity
            // Intent intent = new Intent(this, ChangePasswordActivity.class);
            // startActivity(intent);
        });

        tvLogOut.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> logout())
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void logout() {
        // Ensure prefs is initialized (though it should already be from onCreate)
        if (prefs == null) {
            prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        }

        // Get JWT token from SharedPreferences
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Token không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Show loading dialog
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang đăng xuất...");
        dialog.setCancelable(false);
        dialog.show();

        // Call logout API
        Call<ApiService.LogoutResponse> call = apiService.logout("Bearer " + token);
        call.enqueue(new Callback<ApiService.LogoutResponse>() {
            @Override
            public void onResponse(Call<ApiService.LogoutResponse> call, Response<ApiService.LogoutResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.LogoutResponse logoutResponse = response.body();
                    Toast.makeText(InforUserActivity.this, logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (logoutResponse.isSuccess()) {
                        // Clear token and navigate to LoginActivity
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("jwt_token");
                        editor.apply();

                        Intent intent = new Intent(InforUserActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(InforUserActivity.this, "Đăng xuất thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(InforUserActivity.this, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.LogoutResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(InforUserActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}