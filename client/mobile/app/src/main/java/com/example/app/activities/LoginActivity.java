package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.models.User;
import com.example.app.network.ApiService.UserLoginRequest;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    TextView tvForgotPassword, tvSignUp;
    Button btnLogin, btnLoginViaGG;
    ImageView ivReturn;
    TextView tbtnLogin;
    private EditText etEmail, etPassword;
    private ApiService apiService;
    final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

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
        btnLoginViaGG = findViewById(R.id.btnLoginViaGG);
        ivReturn = findViewById(R.id.ivReturn);
        tbtnLogin = findViewById(R.id.tbtnSignup);

        // Đăng xuất khỏi Google khi mở lại ứng dụng
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                .addOnCompleteListener(this, task -> Log.d(TAG, "User logged out from Google"));

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> {
            Log.d(TAG, "Return button clicked");
            OnBackPressedDispatcher onBackPressedDispatcher = null;
            onBackPressedDispatcher.onBackPressed();  // Sử dụng dispatcher thay vì onBackPressed()
        });

        btnLogin.setOnClickListener(v -> {
            // Hardcode credentials
            String email = "ngoclinhruby711@gmail.com";
            String password = "Password123!";

            // Tạo request body
            UserLoginRequest request = new UserLoginRequest(email, password);

            // Log request body
            Log.d(TAG, "Request Body: email=" + email + ", password=" + password);
            Log.d(TAG, "UserLoginRequest: email=" + request.getEmail() + ", password=" + request.getPassword());

            // Gọi API đăng nhập
            apiService.login(request).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "Request URL: " + call.request().url());
                    Log.d(TAG, "Response Code: " + response.code());
                    Log.d(TAG, "Response Headers: " + response.headers());
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công: " + user.getFullName(), Toast.LENGTH_SHORT).show();

                        // Lưu token vào SharedPreferences
                        AuthUtils.saveToken(LoginActivity.this, user.getToken());

                        // Chuyển hướng đến NotificationActivity
                        Intent intent = new Intent(LoginActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = "Lỗi đăng nhập (Mã: " + response.code() + "): ";
                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                errorMessage += errorBody;
                                Log.e(TAG, "Error Body: " + errorBody);
                            } catch (Exception e) {
                                errorMessage += "Không thể đọc lỗi từ server";
                                Log.e(TAG, "Error parsing errorBody", e);
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

        // Xử lý sự kiện cho "Quên mật khẩu"
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Khởi tạo GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Khởi tạo nút đăng nhập qua Google
        btnLoginViaGG = findViewById(R.id.btnLoginViaGG);
        btnLoginViaGG.setOnClickListener(v -> signIn());

        setupPasswordToggle(etPassword, R.drawable.visibility_24px, R.drawable.visibility_off_24px);
    }

    // Bắt đầu tiến trình đăng nhập qua Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Xử lý kết quả trả về từ Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Đăng nhập thành công
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công với Google", Toast.LENGTH_SHORT).show();
                handleSignInResult(account);
            } catch (ApiException e) {
                // Xử lý lỗi khi đăng nhập
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập Google: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Xử lý kết quả đăng nhập thành công
    private void handleSignInResult(GoogleSignInAccount account) {
        String email = account.getEmail();
        String idToken = account.getIdToken();

        // Gửi ID Token để đăng nhập vào API backend
        UserLoginRequest request = new UserLoginRequest(email, idToken);
        apiService.loginWithGoogle(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Toast.makeText(LoginActivity.this, "Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();

                    // Lưu Token vào SharedPreferences hoặc dùng cho các API yêu cầu xác thực
                    AuthUtils.saveToken(LoginActivity.this, user.getToken());

                    Intent intent = new Intent(LoginActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
