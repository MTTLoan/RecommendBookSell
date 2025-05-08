package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.R;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etPassword, etConfirmPassword;
    private Button btnLogin;
    private ImageView ivReturn;
    private TextView tbtnSignup;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resert_password);

        // Ánh xạ view
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ivReturn = findViewById(R.id.ivReturn);
        tbtnSignup = findViewById(R.id.tbtnSignup);

        // Lấy email và otp từ Intent
        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        // Sự kiện nhấn nút "Xác nhận"
        btnLogin.setOnClickListener(v -> {
            String newPassword = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService();
            Call<ApiService.ResetPasswordResponse> call = apiService.resetPassword(new ApiService.ResetPasswordRequest(email, otp, newPassword, confirmPassword));
            call.enqueue(new Callback<ApiService.ResetPasswordResponse>() {
                @Override
                public void onResponse(Call<ApiService.ResetPasswordResponse> call, Response<ApiService.ResetPasswordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPasswordActivity.this, CongratulationActivity.class));
                        finish();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Đặt lại mật khẩu thất bại";
                            Toast.makeText(ResetPasswordActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiService.ResetPasswordResponse> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        setupPasswordToggle(etPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
        setupPasswordToggle(etConfirmPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
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