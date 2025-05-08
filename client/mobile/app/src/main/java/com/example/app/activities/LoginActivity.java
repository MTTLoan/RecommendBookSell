package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.models.User;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.example.app.network.ApiService.UserLoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private TextView tvForgotPassword, tvSignUp;
    private Button btnLogin;
    private ImageView ivReturn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Gán các đối tượng view
        etEmail = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvforgotPassword);
        tvSignUp = findViewById(R.id.tbtnSignup);
        btnLogin = findViewById(R.id.btnLogin);
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> finish());

        // Sự kiện nhấn nút "Đăng ký"
        tvSignUp.setOnClickListener(v -> {
            Log.d(TAG, "Login link clicked");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Sự kiện đăng nhập thông thường
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo request cho đăng nhập thông thường
            UserLoginRequest request = new UserLoginRequest(email, password, true);
            Log.d(TAG, "Request Body: email=" + email + ", password=" + password);

            // Gọi API đăng nhập
            apiService.login(request).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "Request URL: " + call.request().url());
                    Log.d(TAG, "Response Code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                        AuthUtils.saveToken(LoginActivity.this, user.getToken());
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        String errorMessage = "Lỗi đăng nhập (Mã: " + response.code() + "): ";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += response.errorBody().string();
                            } catch (Exception e) {
                                errorMessage += "Không thể đọc lỗi từ server";
                            }
                        } else {
                            errorMessage += "Không có thông tin lỗi";
                        }
                        Log.e(TAG, errorMessage);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    String errorMessage = "Đăng nhập thất bại: " + t.getMessage();
                    Log.e(TAG, errorMessage, t);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Cài đặt toggle hiển thị mật khẩu
        setupPasswordToggle(etPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
    }

    // Cài đặt toggle hiển thị mật khẩu
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
