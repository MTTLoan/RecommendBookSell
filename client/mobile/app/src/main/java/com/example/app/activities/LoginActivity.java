package com.example.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.R;
import com.example.app.models.User;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private TextInputLayout tfEmail, tfPassword;
    private TextView tvForgotPassword, tvSignUp;
    private android.widget.Button btnLogin;
    private SignInButton signInButton;
    private ImageView ivReturn;
    private ApiService apiService;
    private GoogleSignInClient mGoogleSignInClient;

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
        signInButton = findViewById(R.id.sign_in_button);
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Kiểm tra yêu cầu đăng xuất từ Menu
        if (getIntent().getBooleanExtra("logout", false)) {
            signOutGoogle();
        }

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
                        startActivity(new Intent(LoginActivity.this, Menu.class));
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
                                // Lỗi từ controller
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

        // Sự kiện đăng nhập Google
        signInButton.setOnClickListener(v -> signInWithGoogle());

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

    private void signInWithGoogle() {
        // Đảm bảo đăng xuất trước để hiển thị Account Picker
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Log.d(TAG, "Pre-sign-in sign-out completed");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void signOutGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Log.d(TAG, "Google Sign-Out completed");
            Toast.makeText(LoginActivity.this, "Đã đăng xuất Google", Toast.LENGTH_SHORT).show();
            AuthUtils.clearToken(LoginActivity.this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignInResult(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed, status code: " + e.getStatusCode(), e);
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleGoogleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            String googleId = account.getId();
            String email = account.getEmail();
            String fullName = account.getDisplayName();
            String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

            // Log dữ liệu gửi đi
            Log.d(TAG, "Google Sign-In Data: googleId=" + googleId + ", email=" + email + ", fullName=" + fullName + ", photoUrl=" + photoUrl);

            // Kiểm tra dữ liệu hợp lệ
            if (googleId == null || googleId.isEmpty() || email == null || email.isEmpty()) {
                Log.e(TAG, "Invalid Google Sign-In data: googleId or email is null/empty");
                Toast.makeText(this, "Dữ liệu Google không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API googleauth
            ApiService.GoogleAuthRequest request = new ApiService.GoogleAuthRequest(googleId, email, fullName, photoUrl);
            Call<User> call = apiService.loginWithGoogle(request);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "Google Auth Request URL: " + call.request().url());
                    Log.d(TAG, "Google Auth Response Code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        AuthUtils.saveToken(LoginActivity.this, user.getToken());
                        Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, Menu.class));
                        finish();
                    } else {
                        String errorMessage = "Đăng nhập Google thất bại";
                        if (response.code() == 404) {
                            errorMessage = "Không tìm thấy endpoint /api/auth/googleauth. Vui lòng kiểm tra server.";
                            Log.e(TAG, "Google Auth Error: Endpoint not found");
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Google Auth Error: " + errorBody);
                                if (errorBody.contains("<!DOCTYPE html")) {
                                    errorMessage = "Server trả về HTML thay vì JSON. Kiểm tra cấu hình endpoint.";
                                } else {
                                    JSONObject errorObj = new JSONObject(errorBody);
                                    errorMessage = errorObj.optString("msg", "Đăng nhập Google thất bại");
                                    String errorDetail = errorObj.optString("error", "");
                                    if (!errorDetail.isEmpty()) {
                                        errorMessage += " (" + errorDetail + ")";
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing Google Auth response: " + e.getMessage());
                            }
                        }
                        Toast.makeText(LoginActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "Google Auth API call failed: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e(TAG, "GoogleSignInAccount is null");
            Toast.makeText(this, "Không lấy được thông tin tài khoản Google", Toast.LENGTH_SHORT).show();
        }
    }
}
