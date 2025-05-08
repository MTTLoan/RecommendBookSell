package com.example.app.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.app.R;
import com.example.app.fragments.OtpFragment;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private EditText edtEmail;
    private Button btnSendOtp;
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reset_password);

        edtEmail = findViewById(R.id.etEmail);
        btnSendOtp = findViewById(R.id.btnSendOTP);
        ivReturn = findViewById(R.id.ivReturn);

        btnSendOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            Log.d(TAG, "Email entered: " + email);

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            } else {
                sendForgotPasswordRequest(email);
            }
        });

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> finish());


    }

    private void sendForgotPasswordRequest(String email) {
        Log.d(TAG, "Sending OTP for email: " + email);

        ApiService apiService = RetrofitClient.getApiService();
        ApiService.ForgotPasswordRequest request = new ApiService.ForgotPasswordRequest(email);

        Call<ApiService.OtpResponse> call = apiService.forgotPassword(request);
        call.enqueue(new Callback<ApiService.OtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.OtpResponse> call, @NonNull Response<ApiService.OtpResponse> response) {
                Log.d(TAG, "onResponse: HTTP Code = " + response.code());
                Log.d(TAG, "onResponse: isSuccessful = " + response.isSuccessful());
                Log.d(TAG, "onResponse: body = " + (response.body() != null ? response.body().toString() : "null"));

                if (response.isSuccessful() && response.body() != null && "OTP sent successfully".equals(response.body().getMessage())) {
                    Log.d(TAG, "OTP request successful, showing OTP fragment");
                    Toast.makeText(ForgotPasswordActivity.this, "OTP đã được gửi đến email", Toast.LENGTH_SHORT).show();
                    showOtpFragment(email);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Gửi OTP thất bại";
                    Log.d(TAG, "OTP request failed: " + message);
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.OtpResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOtpFragment(String email) {
        Log.d(TAG, "showOtpFragment with email: " + email);

        OtpFragment otpFragment = OtpFragment.newInstance(email, "Đặt lại mật khẩu", "forgot_password");

        // Hiện FrameLayout chứa OTP Fragment
        findViewById(R.id.otp_fragment).setVisibility(android.view.View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.otp_fragment, otpFragment)
                .addToBackStack(null)
                .commit();
    }
}