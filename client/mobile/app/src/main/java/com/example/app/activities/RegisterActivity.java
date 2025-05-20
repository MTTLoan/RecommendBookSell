package com.example.app.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.example.app.R;                        
import androidx.fragment.app.FragmentManager;

import com.example.app.fragments.OTPFragmentRegister;
import com.example.app.models.User;
import com.example.app.models.request.RegisterRequest;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputLayout tfUsername, tfFullname, tfEmail, tfPhonenumber, tfPassword, tfConfirmPassword;
    private TextInputEditText etUsername, etFullname, etEmail, etPhonenumber, etPassword, etConfirmPassword;
    private Button btnRegister;
    private ImageView ivReturn;
    private TextView tbtnLogin;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ các view từ layout
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        tfUsername = findViewById(R.id.tfUsername);
        tfFullname = findViewById(R.id.tfFullname);
        tfEmail = findViewById(R.id.tfEmail);
        tfPhonenumber = findViewById(R.id.tfPhonenumber);
        tfPassword = findViewById(R.id.tfPassword);
        tfConfirmPassword = findViewById(R.id.tfConfirmPassword);

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

        // Thêm listener để cuộn khi bàn phím xuất hiện
        final View activityRootView = findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = activityRootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // Bàn phím xuất hiện
                    View focusedView = getCurrentFocus();
                    if (focusedView != null) {
                        int[] location = new int[2];
                        focusedView.getLocationInWindow(location);
                        int scrollTo = location[1] - keypadHeight;
                        nestedScrollView.smoothScrollTo(0, scrollTo > 0 ? scrollTo : 0);
                    }
                }
            }
        });

        // Sự kiện nhấn nút "Đăng ký"
        btnRegister.setOnClickListener(v -> {
            Log.d(TAG, "Register button clicked");

            // Xóa các lỗi cũ
            clearErrors();

            String username = etUsername.getText().toString().trim();
            String fullName = etFullname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhonenumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Kiểm tra các trường có rỗng không (validation client-side)
            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Log.d(TAG, "Validation failed: Some fields are empty");
                if (username.isEmpty()) tfUsername.setError("Tên tài khoản là bắt buộc");
                if (fullName.isEmpty()) tfFullname.setError("Họ và tên là bắt buộc");
                if (email.isEmpty()) tfEmail.setError("Email là bắt buộc");
                if (phoneNumber.isEmpty()) tfPhonenumber.setError("Số điện thoại là bắt buộc");
                if (password.isEmpty()) tfPassword.setError("Mật khẩu là bắt buộc");
                if (confirmPassword.isEmpty()) tfConfirmPassword.setError("Xác nhận mật khẩu là bắt buộc");
                return;
            }

            Log.d(TAG, "All fields are filled. Sending request to API...");
            Log.d(TAG, "Request data: username=" + username + ", fullName=" + fullName + ", email=" + email +
                    ", phoneNumber=" + phoneNumber + ", password=" + password);

            // Gọi API đăng ký
            ApiService apiService = RetrofitClient.getApiService();
            Call<User> call = apiService.register(new RegisterRequest(username, fullName, email, phoneNumber, password, confirmPassword));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "API response received: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        String token = user.getToken();
                        String email = user.getEmail() != null ? user.getEmail() : etEmail.getText().toString().trim();
                        Log.d(TAG, "Email passed to OTP: " + email);

                        if (token != null) {
                            Log.d(TAG, "Token received: " + token);
                            AuthUtils.saveToken(RegisterActivity.this, token);
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                            // Chuyển sang màn hình xác nhận OTP
                            showOtpFragment(email);
                        } else {
                            Log.d(TAG, "Token is null");
                            Toast.makeText(RegisterActivity.this, "Không nhận được token", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            // Lấy nội dung errorBody
                            String errorBodyString = response.errorBody() != null ? response.errorBody().string() : "";
                            Log.d(TAG, "Raw error response: " + errorBodyString);

                            if (errorBodyString.isEmpty()) {
                                Log.e(TAG, "Error body is empty");
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: Không nhận được phản hồi lỗi", Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONObject errorObj = new JSONObject(errorBodyString);

                            // Kiểm tra nếu có mảng "errors"
                            if (errorObj.has("errors")) {
                                JSONArray errors = errorObj.getJSONArray("errors");
                                Log.d(TAG, "Number of errors: " + errors.length());

                                for (int i = 0; i < errors.length(); i++) {
                                    JSONObject error = errors.getJSONObject(i);
                                    String msg = error.optString("msg", "Lỗi không xác định");
                                    String path = error.optString("path", "").toLowerCase();

                                    Log.d(TAG, "Error " + i + " - path: " + path + ", msg: " + msg);

                                    // Hiển thị lỗi cho từng trường
                                    switch (path) {
                                        case "username":
                                            tfUsername.setError(msg);
                                            tfUsername.setErrorEnabled(true);
                                            break;
                                        case "fullname":
                                        case "fullName": // Xử lý cả trường hợp "fullName"
                                            tfFullname.setError(msg);
                                            tfFullname.setErrorEnabled(true);
                                            break;
                                        case "email":
                                            tfEmail.setError(msg);
                                            tfEmail.setErrorEnabled(true);
                                            break;
                                        case "phonenumber":
                                        case "phoneNumber": // Xử lý cả trường hợp "phoneNumber"
                                            tfPhonenumber.setError(msg);
                                            tfPhonenumber.setErrorEnabled(true);
                                            break;
                                        case "password":
                                            tfPassword.setError(msg);
                                            tfPassword.setErrorEnabled(true);
                                            break;
                                        case "confirm_password":
                                        case "confirmpassword": // Xử lý cả trường hợp không có dấu
                                            tfConfirmPassword.setError(msg);
                                            tfConfirmPassword.setErrorEnabled(true);
                                            break;
                                        default:
                                            Log.d(TAG, "Unknown path: " + path + ", showing as Toast");
                                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            } else if (errorObj.has("message")) {
                                String message = errorObj.optString("message", "Đăng ký thất bại");
                                Log.d(TAG, "Error message: " + message);
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + message, Toast.LENGTH_LONG).show();
                            } else if (errorObj.has("error")) {
                                String errorMessage = errorObj.optString("error", "Đăng ký thất bại");
                                Log.d(TAG, "Error: " + errorMessage);
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Unknown error format: " + errorBodyString);
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: Lỗi không xác định", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Lỗi phân tích phản hồi API", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Unexpected error: " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "API call failed: " + t.getMessage());
                    Toast.makeText(RegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

        // Thiết lập toggle hiển thị mật khẩu
        setupPasswordToggle(tfPassword, etPassword, R.drawable.ic_visibility_24px, R.drawable.ic_visibility_off_24px);
        setupPasswordToggle(tfConfirmPassword, etConfirmPassword, R.drawable.ic_visibility_24px, R.drawable.ic_visibility_off_24px);
    }

    private void showOtpFragment(String email) {
        Log.d(TAG, "showOtpFragment with email: " + email);

        OTPFragmentRegister otpFragment = OTPFragmentRegister.newInstance(email, "Đăng ký", "register");

        // Hiện FrameLayout chứa OTP Fragment
        findViewById(R.id.otp_fragment).setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.otp_fragment, otpFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupPasswordToggle(TextInputLayout textInputLayout, TextInputEditText editText, int iconShow, int iconHide) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = editText.getCompoundDrawables()[2] != null
                        ? editText.getCompoundDrawables()[2].getBounds().width() : 0;

                if (event.getRawX() >= (editText.getRight() - drawableEnd - editText.getPaddingEnd())) {
                    if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        textInputLayout.setEndIconDrawable(iconShow);
                    } else {
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        textInputLayout.setEndIconDrawable(iconHide);
                    }
                    editText.setSelection(editText.length());
                    return true;
                }
            }
            return false;
        });
    }

    private void clearErrors() {
        tfUsername.setError(null);
        tfUsername.setErrorEnabled(false);
        tfFullname.setError(null);
        tfFullname.setErrorEnabled(false);
        tfEmail.setError(null);
        tfEmail.setErrorEnabled(false);
        tfPhonenumber.setError(null);
        tfPhonenumber.setErrorEnabled(false);
        tfPassword.setError(null);
        tfPassword.setErrorEnabled(false);
        tfConfirmPassword.setError(null);
        tfConfirmPassword.setErrorEnabled(false);
    }
}