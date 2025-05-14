package com.example.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword; // Mật khẩu cũ
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnChangePassword;
    private ImageView ivReturn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo các view
        etOldPassword = findViewById(R.id.etPasswordOld);
        etNewPassword = findViewById(R.id.etPasswordNew);
        etConfirmPassword = findViewById(R.id.eConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword); // Nút "Đổi mật khẩu"
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService từ RetrofitClient
        apiService = RetrofitClient.getApiService();

        // Sự kiện nút Back
        ivReturn.setOnClickListener(v -> finish());

        // Sự kiện nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Kiểm tra dữ liệu nhập
            if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API để đổi mật khẩu
            changePassword("user@example.com", oldPassword, newPassword, confirmPassword); // Thay user@example.com bằng email thực tế
        });
    }

    private void changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        ApiService.ChangePasswordRequest request = new ApiService.ChangePasswordRequest(email, oldPassword, newPassword, confirmPassword);
        Call<ApiService.ChangePasswordResponse> call = apiService.changePassword(request);

        call.enqueue(new Callback<ApiService.ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ApiService.ChangePasswordResponse> call, Response<ApiService.ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                } else {
                    String errorMessage = response.errorBody() != null ? response.errorBody().toString() : "Lỗi khi đổi mật khẩu";
                    Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ChangePasswordResponse> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}