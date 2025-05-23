package com.example.app.activities;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.models.request.ChangePasswordRequest;
import com.example.app.models.response.ChangePasswordResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.example.app.utils.UIUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnChangePassword;
    private ImageView ivReturn;
    private ApiService apiService;
    private TextInputLayout tfNewPassword, tfConfirmPassword, tfOldPassword;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo các view
        etOldPassword = findViewById(R.id.etPasswordOld);
        etNewPassword = findViewById(R.id.etPasswordNew);
        etConfirmPassword = findViewById(R.id.eConfirmPassword);
        tfOldPassword = findViewById(R.id.tfPasswordOld);
        tfNewPassword = findViewById(R.id.tfPasswordNew);
        tfConfirmPassword = findViewById(R.id.tfPasswordConfirm);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Sự kiện nút Back
        ivReturn.setOnClickListener(v -> finish());

        // Sự kiện nút Đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

//            // Kiểm tra dữ liệu nhập
//            if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
//                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (!newPassword.equals(confirmPassword)) {
//                Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (newPassword.length() < 8) {
//                Toast.makeText(this, R.string.error_password_too_short, Toast.LENGTH_SHORT).show();
//                return;
//            }

            // Vô hiệu hóa nút để tránh nhấn liên tục
            btnChangePassword.setEnabled(false);

            // Gọi API để đổi mật khẩu
            changePassword(oldPassword, newPassword, confirmPassword);
        });

        // Cài đặt toggle hiển thị mật khẩu
        UIUtils.setupPasswordToggle(tfOldPassword);
        UIUtils.setupPasswordToggle(tfNewPassword);
        UIUtils.setupPasswordToggle(tfConfirmPassword);
    }

    private void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        String token = AuthUtils.getToken(this);
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Log.d("ChangePasswordActivity", "Old: " + oldPassword + ", New: " + newPassword + ", Confirm: " + confirmPassword);

        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword, confirmPassword);

        Call<ChangePasswordResponse> call = apiService.changePassword(token, request);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                btnChangePassword.setEnabled(true);
                Log.d("ChangePasswordActivity", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String message = "Mật khẩu đã được thay đổi.";
                    Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    finish(); // Quay lại
                } else {
                    String errorMessage = "Lỗi không xác định";
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("ChangePasswordActivity", "Error body: " + errorBody);
                        JSONObject errorJson = new JSONObject(errorBody);
                        if (errorJson.has("msg")) {
                            errorMessage = errorJson.getString("msg");
                        }
                    } catch (Exception e) {
                        Log.e("ChangePasswordActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                String errorMessage = t instanceof IOException ? "Lỗi mạng" : "Lỗi không xác định";
                Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("ChangePasswordActivity", "API failure", t);
            }
        });
    }
}