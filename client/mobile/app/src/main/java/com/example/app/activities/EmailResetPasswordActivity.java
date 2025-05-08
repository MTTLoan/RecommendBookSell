package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.R;
import com.example.app.fragments.OTPFragment;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailResetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnSendOTP;
    private ImageView ivReturn;
    private TextView tbtnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reset_password);

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        ivReturn = findViewById(R.id.ivReturn);
        tbtnSignup = findViewById(R.id.tbtnSignup);

        // Sự kiện nhấn nút "Gửi OTP"
        btnSendOTP.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService();
            Call<ApiService.ForgotPasswordResponse> call = apiService.sendPasswordResetOTP(new ApiService.ForgotPasswordRequest(email));
            call.enqueue(new Callback<ApiService.ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ApiService.ForgotPasswordResponse> call, Response<ApiService.ForgotPasswordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(EmailResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        // Chuyển sang OTPFragment (truyền email qua Bundle)
                        Bundle bundle = new Bundle();
                        bundle.putString("email", email);
                        OTPFragment otpFragment = OTPFragment.newInstance(email, "Đặt lại mật khẩu", "forgot_password");
                        otpFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.otp_fragment, otpFragment)
                                .addToBackStack(null)
                                .commit();
                        findViewById(R.id.otp_fragment).setVisibility(View.VISIBLE);
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Gửi OTP thất bại";
                            Toast.makeText(EmailResetPasswordActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(EmailResetPasswordActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ForgotPasswordResponse> call, Throwable t) {
                    Toast.makeText(EmailResetPasswordActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> finish());

        // Sự kiện nhấn "Đăng nhập"
        tbtnSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}