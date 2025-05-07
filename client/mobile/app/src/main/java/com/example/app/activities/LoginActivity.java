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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.models.User;
import com.example.app.network.ApiService;
import com.example.app.network.ApiService.UserLoginRequest;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private EditText etEmail, etPassword;
    private TextView tvForgotPassword, tvSignUp;
    private Button btnLogin;
    private ImageView ivReturn;
    private ApiService apiService;
    private GoogleSignInClient mGoogleSignInClient;

    private GoogleApiClient mGoogleApiClient;

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
//        btnLoginViaGG = findViewById(R.id.btnLoginViaGG);
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Cấu hình và đăng xuất Google khi mở lại ứng dụng
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Khởi tạo GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this) // this phải implement OnConnectionFailedListener
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> Log.d(TAG, "User logged out from Google"));

        // Sự kiện nhấn nút "Quay lại"
        ivReturn.setOnClickListener(v -> finish());

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
                        startActivity(new Intent(LoginActivity.this, NotificationActivity.class));
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

        // Sự kiện "Quên mật khẩu"
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });


        // Sự kiện đăng nhập qua Google
//        btnLoginViaGG.setOnClickListener(v -> signIn());
        findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener) this);

        //
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

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Xử lý kết quả đăng nhập thành công qua Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(Object o) {
        if (o != null) {
            GoogleSignInAccount account = (GoogleSignInAccount) o;
            String idToken = account.getIdToken();
            String email = account.getEmail();

            // Tạo request cho đăng nhập qua Google
            UserLoginRequest request = new UserLoginRequest(email, idToken);
            Log.d(TAG, "Request Body: email=" + email + ", idToken=" + idToken);

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
                        startActivity(new Intent(LoginActivity.this, NotificationActivity.class));
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
    }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google API Connection Failed: " + connectionResult.getErrorMessage());
        Toast.makeText(this, "Kết nối Google thất bại", Toast.LENGTH_SHORT).show();
    }

//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_in_button:
//                signIn();
//                break;
//            // ...
//        }
//    }

}