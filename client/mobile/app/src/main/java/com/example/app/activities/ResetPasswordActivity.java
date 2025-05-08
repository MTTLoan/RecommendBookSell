package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";
    private EditText etPassword, etConfirmPassword;
    private Button btnLogin; // "Xác nhận" button
    private ImageView ivReturn;
    private TextView tbtnSignup;
    private String userEmail;
    private String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resert_password);

        // Initialize UI elements
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ivReturn = findViewById(R.id.ivReturn);
        tbtnSignup = findViewById(R.id.tbtnSignup);

        // Retrieve email and OTP from Intent
        userEmail = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "No email provided in Intent");
            Toast.makeText(this, "Lỗi: Không tìm thấy email", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (otp == null || otp.isEmpty()) {
            Log.e(TAG, "No OTP provided in Intent");
            Toast.makeText(this, "Lỗi: Không tìm thấy OTP", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.d(TAG, "Received email: " + userEmail + ", OTP: " + otp);

        // Handle "Xác nhận" button click
        btnLogin.setOnClickListener(v -> {
            String newPassword = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Check if passwords are valid
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            } else if (!isPasswordValid(newPassword)) {
                Toast.makeText(this, "Mật khẩu không hợp lệ. Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, và số.", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(newPassword, confirmPassword);
            }
        });

        // Handle "Quay lại" (Back) button click
        ivReturn.setOnClickListener(v -> onBackPressed());

        // Handle "Đăng nhập" (Login) link click
        tbtnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class); // Replace with your actual LoginActivity
            startActivity(intent);
            finish();
        });

        setupPasswordToggle(etPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
        setupPasswordToggle(etConfirmPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
    }

    private boolean isPasswordValid(String password) {
        // Example password validation: minimum 8 characters, at least one lowercase, one uppercase, and one digit
        return password.length() >= 8 && password.matches(".*[a-z].*") && password.matches(".*[A-Z].*") && password.matches(".*\\d.*");
    }

    private void resetPassword(String newPassword, String confirmPassword) {
        Log.d(TAG, "Attempting to reset password for email: " + userEmail + ", OTP: " + otp);

        // Create the request object
        ApiService.ResetPasswordRequest request = new ApiService.ResetPasswordRequest(
                userEmail, newPassword, confirmPassword, otp);

        // Log the request body as JSON using Gson
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);
        Log.d(TAG, "Request Body: " + jsonRequest); // Log the JSON string

        // Make the API request
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiService.OtpResponse> call = apiService.resetPassword(request);
        call.enqueue(new Callback<ApiService.OtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.OtpResponse> call, @NonNull Response<ApiService.OtpResponse> response) {
                Log.d(TAG, "onResponse: HTTP Code = " + response.code());
                Log.d(TAG, "onResponse: body = " + (response.body() != null ? response.body().toString() : "null"));

                if (response.isSuccessful() && response.body() != null && "Reset password successfully".equals(response.body().getMessage())) {
                    Log.d(TAG, "Password reset successful");
                    Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Đặt lại mật khẩu thất bại";
                    Log.d(TAG, "Password reset failed: " + message);
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.OtpResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupPasswordToggle(EditText editText, int iconShow, int iconHide) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = editText.getCompoundDrawables()[2] != null
                        ? editText.getCompoundDrawables()[2].getBounds().width() : 0;

                if (event.getRawX() >= (editText.getRight() - drawableEnd - editText.getPaddingEnd())) {
                    if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconShow, 0);
                    } else {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconHide, 0);
                    }
                    editText.setSelection(editText.length());
                    return true;
                }
            }
            return false;
        });
    }
}
