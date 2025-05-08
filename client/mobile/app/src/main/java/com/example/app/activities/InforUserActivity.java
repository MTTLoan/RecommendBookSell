package com.example.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        // Set click listeners
        tvEditPersonalInfoLabel.setOnClickListener(v -> {
            // Navigate to Edit Personal Info screen
            Toast.makeText(this, "Chuyển đến màn hình sửa thông tin cá nhân", Toast.LENGTH_SHORT).show();
            // Replace with Intent to EditPersonalInfoActivity
            // Intent intent = new Intent(this, EditPersonalInfoActivity.class);
            // startActivity(intent);
        });

        tvChangePasswordLabel.setOnClickListener(v -> {
            // Navigate to Change Password screen
            Toast.makeText(this, "Chuyển đến màn hình đổi mật khẩu", Toast.LENGTH_SHORT).show();
            // Replace with Intent to ChangePasswordActivity
            // Intent intent = new Intent(this, ChangePasswordActivity.class);
            // startActivity(intent);
        });

        tvLogOut.setOnClickListener(v -> {
            // Show confirmation dialog before logout
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> logout())
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void logout() {
        // Get JWT token from SharedPreferences
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(this, "Token không tồn tại", Toast.LENGTH_SHORT).show();
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