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
import androidx.fragment.app.FragmentManager;
import com.example.app.fragments.OTPFragment;
import com.example.app.models.User;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText etUsername, etFullname, etEmail, etPhonenumber, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ImageView ivReturn;
    private TextView tbtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ các view từ layout
        etUsername = findViewById(R.id.etUsername);
        etFullname = findViewById(R.id.etFullname);
        etEmail = findViewById(R.id.etEmail);
        etPhonenumber = findViewById(R.id.etPhonenumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        ivReturn = findViewById(R.id.ivReturn);
        tbtnLogin = findViewById(R.id.tbtnLogin);

        // Kiểm tra ánh xạ của btnRegister
        if (btnRegister == null) {
            Log.e(TAG, "btnRegister is null - Check if ID 'btnRegister' exists in layout");
            Toast.makeText(this, "Lỗi: Không tìm thấy nút Đăng ký", Toast.LENGTH_LONG).show();
            return;
        }

        // Sự kiện nhấn nút "Đăng ký"
        btnRegister.setOnClickListener(v -> {
            Log.d(TAG, "Register button clicked");

            String username = etUsername.getText().toString().trim();
            String fullName = etFullname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhonenumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Kiểm tra các trường có rỗng không
            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Log.d(TAG, "Validation failed: Some fields are empty");
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "All fields are filled. Sending request to API...");
            Log.d(TAG, "Request data: username=" + username + ", fullName=" + fullName + ", email=" + email +
                    ", phoneNumber=" + phoneNumber + ", password=" + password);

            // Gọi API đăng ký
            ApiService apiService = RetrofitClient.getApiService();
            Call<User> call = apiService.register(new ApiService.RegisterRequest(username, fullName, email, phoneNumber, password, confirmPassword));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "API response received: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        String token = user.getToken();
                        String email = user.getEmail() != null ? user.getEmail() : etEmail.getText().toString().trim(); // Lấy email từ đối tượng User
                        Log.d(TAG, "Email passed to OTP: " + email);

                        if (token != null) {
                            Log.d(TAG, "Token received: " + token);
                            AuthUtils.saveToken(RegisterActivity.this, token);
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                            // Sau khi đăng ký thành công, chuyển sang màn hình xác nhận OTP
                            showOtpFragment(email);

                        } else {
                            Log.d(TAG, "Token is null");
                            Toast.makeText(RegisterActivity.this, "Không nhận được token", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            JSONObject errorObj = new JSONObject(response.errorBody().string());
                            Log.d(TAG, "API error response: " + errorObj.toString());
                            if (errorObj.has("errors")) {
                                JSONArray errors = errorObj.getJSONArray("errors");
                                StringBuilder errorMsg = new StringBuilder();
                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject error = errors.getJSONObject(i);
                                    errorMsg.append(error.getString("msg")).append("\n");
                                }
                                Toast.makeText(RegisterActivity.this, errorMsg.toString(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorObj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing API response: " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "API call failed: " + t.getMessage());
                    Toast.makeText(RegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> finish());

        // Sự kiện nhấn "Đăng nhập"
        tbtnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login link clicked");
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);

        setupPasswordToggle(etPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
        setupPasswordToggle(etConfirmPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);

    }

    private void showOtpFragment(String email) {
        Log.d(TAG, "showOtpFragment with email: " + email);

        OTPFragment otpFragment = OTPFragment.newInstance(email, "Đăng ký", "register");

        // Hiện FrameLayout chứa OTP Fragment
        findViewById(R.id.otp_fragment).setVisibility(android.view.View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.otp_fragment, otpFragment)
                .addToBackStack(null)
                .commit();
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