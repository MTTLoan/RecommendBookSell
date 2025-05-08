package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.R;
import com.example.app.models.User;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout tfEmail, tfPassword;
    private TextView tvForgotPassword, tvSignUp;
    private Button btnLogin;
    private ImageView ivReturn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Gán các đối tượng view
        tfEmail = findViewById(R.id.tfUsername);
        tfPassword = findViewById(R.id.tfPassword);
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
            Log.d(TAG, "SignUp link clicked");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Sự kiện đăng nhập thông thường
        btnLogin.setOnClickListener(v -> {
            String email = tfEmail.getEditText().getText().toString().trim();
            String password = tfPassword.getEditText().getText().toString().trim();

            // Xóa lỗi cũ
            tfEmail.setError(null);
            tfPassword.setError(null);

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    tfEmail.setError("Tên tài khoản hoặc email là bắt buộc");
                }
                if (password.isEmpty()) {
                    tfPassword.setError("Mật khẩu là bắt buộc");
                }
                return;
            }

            // Tạo request cho đăng nhập thông thường
            ApiService.UserLoginRequest request = new ApiService.UserLoginRequest(email, password, true);
            Log.d(TAG, "Request Body: identifier=" + email + ", password=" + password);

            // Gọi API đăng nhập
            apiService.login(request).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "Request URL: " + call.request().url());
                    Log.d(TAG, "Response Code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        AuthUtils.saveToken(LoginActivity.this, user.getToken());
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            JSONObject errorObj = new JSONObject(errorBody);

                            if (errorObj.has("errors")) {
                                // Lỗi validation từ middleware
                                JSONArray errors = errorObj.getJSONArray("errors");
                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject error = errors.getJSONObject(i);
                                    String param = error.getString("param");
                                    String msg = error.getString("msg");
                                    if ("identifier".equals(param)) {
                                        tfEmail.setError(msg);
                                    } else if ("password".equals(param)) {
                                        tfPassword.setError(msg);
                                    }
                                }
                            } else if (errorObj.has("msg")) {
                                // Lỗi từ controller (ví dụ: tài khoản không tồn tại, mật khẩu sai)
                                String msg = errorObj.getString("msg");
                                if (msg.contains("Tên tài khoản hoặc email")) {
                                    tfEmail.setError(msg);
                                } else if (msg.contains("Mật khẩu")) {
                                    tfPassword.setError(msg);
                                } else {
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing response: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "API call failed: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Sự kiện "Quên mật khẩu"
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, EmailResetPasswordActivity.class));
        });



        // Cài đặt toggle hiển thị mật khẩu
        tfPassword.setEndIconOnClickListener(v -> {
            if (tfPassword.getEditText().getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                tfPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tfPassword.setEndIconDrawable(R.drawable.visibility_24px);
            } else {
                tfPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tfPassword.setEndIconDrawable(R.drawable.visibility_off_24px);
            }
            tfPassword.getEditText().setSelection(tfPassword.getEditText().length());
        });
    }
}