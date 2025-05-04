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
        
        // Sự kiện nhấn "Đăng ký"
        tbtnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Signup link clicked");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Xử lý sự kiện cho nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Kiểm tra các trường có rỗng không
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu đăng nhập
            UserLoginRequest request = new UserLoginRequest(username, password);
            apiService.login(request).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        String token = user.getToken();
                        if (token != null) {
                            Log.d(TAG, "Token received: " + token);
                            AuthUtils.saveToken(LoginActivity.this, token);
                            Toast.makeText(LoginActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Log.d(TAG, "Token is null");
                            Toast.makeText(LoginActivity.this, "Không nhận được token", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Thông tin đăng nhập không chính xác", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
