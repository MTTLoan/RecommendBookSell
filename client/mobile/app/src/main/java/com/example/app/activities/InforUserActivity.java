package com.example.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.app.R;
import com.example.app.models.User;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.example.app.models.response.LogoutResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforUserActivity extends AppCompatActivity {

    private static final String TAG = "InforUserActivity";

    private TextView tvEditPersonalInfoLabel;
    private TextView tvChangePasswordLabel;
    private TextView tvLogOut;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivAvatar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_user);

        // Initialize views
        tvEditPersonalInfoLabel = findViewById(R.id.tvEditPersonalInfoLabel);
        tvChangePasswordLabel = findViewById(R.id.tvChangePasswordLabel);
        tvLogOut = findViewById(R.id.tvlogOut);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        ivAvatar = findViewById(R.id.ivAvatar);

        // Initialize ApiService
        apiService = RetrofitClient.getApiService();

        // Check token using AuthUtils
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Log.e(TAG, "Token not found in SharedPreferences");
            Toast.makeText(this, "Token không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        } else {
            Log.d(TAG, "Token retrieved: " + token.substring(0, 10) + "...");
        }

        // Fetch user profile
        fetchUserProfile(token);

        // Set click listeners
        tvEditPersonalInfoLabel.setOnClickListener(v -> {
             Intent intent = new Intent(InforUserActivity.this, PersonalInfoActivity.class);
             startActivity(intent);
        });

        tvChangePasswordLabel.setOnClickListener(v -> {
             Intent intent = new Intent(InforUserActivity.this, ChangePasswordActivity.class);
             startActivity(intent);
        });

        tvLogOut.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> logout(token))
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void logout(String token) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang đăng xuất...");
        dialog.setCancelable(false);
        dialog.show();

        Call<LogoutResponse> call = apiService.logout(token);
        Log.d(TAG, "Logout request URL: " + call.request().url());
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    LogoutResponse logoutResponse = response.body();
                    Log.d(TAG, "Logout response body: " + new String(response.body().toString())); // Note: This might not show JSON
                    Log.d(TAG, "Logout response - success: " + logoutResponse.isSuccess());
                    Log.d(TAG, "Logout response - message: " + logoutResponse.getMessage());
                    Toast.makeText(InforUserActivity.this, logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (logoutResponse.isSuccess()) {
                        AuthUtils.clearToken(InforUserActivity.this);
                        Intent intent = new Intent(InforUserActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "Logout not successful despite message: " + logoutResponse.getMessage());
                        Toast.makeText(InforUserActivity.this, "Đăng xuất không thành công", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Logout failed: " + errorBody);
                        Toast.makeText(InforUserActivity.this, "Đăng xuất thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing logout response: " + e.getMessage());
                        Toast.makeText(InforUserActivity.this, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(TAG, "Logout API call failed: " + t.getMessage());
                Toast.makeText(InforUserActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfile(String token) {
        Call<User> call = apiService.getUserProfile(token);
        Log.d(TAG, "Profile request URL: " + call.request().url());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d(TAG, "Profile fetched: email=" + user.getEmail());

                    // Update UI
                    tvName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
                    tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");

                    // Load avatar with Glide
                    String photoUrl = user.getAvatar();
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(InforUserActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.avt)
                                .error(R.drawable.avt)
                                .into(ivAvatar);
                    } else {
                        ivAvatar.setImageResource(R.drawable.avt);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Profile fetch failed: " + errorBody);
                        Toast.makeText(InforUserActivity.this, "Không thể lấy thông tin hồ sơ: " + errorBody, Toast.LENGTH_LONG).show();
                        if (response.code() == 401 || response.code() == 403) {
                            AuthUtils.clearToken(InforUserActivity.this);
                            Intent intent = new Intent(InforUserActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing profile response: " + e.getMessage());
                        Toast.makeText(InforUserActivity.this, "Không thể lấy thông tin hồ sơ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Profile API call failed: " + t.getMessage());
                Toast.makeText(InforUserActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}