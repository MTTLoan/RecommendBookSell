package com.example.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.app.R;
import com.example.app.activities.ChangePasswordActivity;
import com.example.app.activities.LoginActivity;
import com.example.app.activities.PersonalInfoActivity;
import com.example.app.models.User;
import com.example.app.models.response.LogoutResponse;
import com.example.app.models.response.UserResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforUserFragment extends Fragment {

    private static final String TAG = "InforUserFragment";

    private TextView tvEditPersonalInfoLabel;
    private TextView tvChangePasswordLabel;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivAvatar;
    private TextView tvLogOut;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_infor_user, container, false);

        // Initialize views
        tvEditPersonalInfoLabel = view.findViewById(R.id.tvEditPersonalInfoLabel);
        tvChangePasswordLabel = view.findViewById(R.id.tvChangePasswordLabel);
        tvLogOut = view.findViewById(R.id.tvlogOut);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivAvatar = view.findViewById(R.id.ivAvatar);

        // Initialize ApiService
        apiService = RetrofitClient.getApiService();

        // Check token using AuthUtils
        String token = AuthUtils.getToken(requireContext());
        if (token == null) {
            Log.e(TAG, "Token not found in SharedPreferences");
            Toast.makeText(getContext(), "Token không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish(); // Finish the hosting activity
            return view;
        } else {
            Log.d(TAG, "Token retrieved: " + token.substring(0, 10) + "...");
        }

        // Fetch user profile
        fetchUserProfile(token);

        // Set click listeners
        tvEditPersonalInfoLabel.setOnClickListener(v -> {
             Intent intent = new Intent(requireContext(), PersonalInfoActivity.class);
             startActivity(intent);
        });

        tvChangePasswordLabel.setOnClickListener(v -> {
             Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
             startActivity(intent);
        });

        tvLogOut.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> logout(token))
                    .setNegativeButton("Không", null)
                    .show();
        });

        return view;
    }

    private void logout(String token) {
        ProgressDialog dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Đang đăng xuất...");
        dialog.setCancelable(false);
        dialog.show();

        Call<LogoutResponse> call = apiService.logout(token);
        Log.d(TAG, "Logout request URL: " + call.request().url());
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    LogoutResponse logoutResponse = response.body();
                    Log.d(TAG, "Logout response body: " + new String(response.body().toString()));
                    Log.d(TAG, "Logout response - success: " + logoutResponse.isSuccess());
                    Log.d(TAG, "Logout response - message: " + logoutResponse.getMessage());
                    Toast.makeText(getContext(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (logoutResponse.isSuccess()) {
                        AuthUtils.clearToken(requireContext());
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish(); // Finish the hosting activity
                    } else {
                        Log.w(TAG, "Logout not successful despite message: " + logoutResponse.getMessage());
                        Toast.makeText(getContext(), "Đăng xuất không thành công", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Logout failed: " + errorBody);
                        Toast.makeText(getContext(), "Đăng xuất thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing logout response: " + e.getMessage());
                        Toast.makeText(getContext(), "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e(TAG, "Logout API call failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfile(String token) {
        String authHeader = "Bearer " + token;

        Call<UserResponse> call = apiService.getUserProfile(authHeader);
        Log.d(TAG, "URL yêu cầu hồ sơ: " + call.request().url());

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    User user = userResponse.getUser();
                    if (user != null) {
                        Log.d(TAG, "Lấy hồ sơ: email=" + user.getEmail() + ", username=" + user.getUsername() + ", avatar=" + user.getAvatar());

                        // Cập nhật UI
                        tvName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
                        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");

                        // Load avatar với Glide
                        String photoUrl = user.getAvatar();
                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(InforUserFragment.this)
                                    .load(photoUrl)
                                    .placeholder(R.drawable.avt)
                                    .apply(RequestOptions.circleCropTransform())
                                    .error(R.drawable.avt)
                                    .into(ivAvatar);
                        } else {
                            ivAvatar.setImageResource(R.drawable.avt);
                        }
                    } else {
                        Log.e(TAG, "Đối tượng User null trong phản hồi");
                        Toast.makeText(getContext(), "Không tìm thấy thông tin hồ sơ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                        Log.e(TAG, "Lấy hồ sơ thất bại: " + errorBody);
                        Toast.makeText(getContext(), "Không thể lấy thông tin hồ sơ: " + errorBody, Toast.LENGTH_LONG).show();

                        if (response.code() == 401 || response.code() == 403) {
                            AuthUtils.clearToken(getContext());
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi phân tích phản hồi: " + e.getMessage());
                        Toast.makeText(getContext(), "Không thể lấy thông tin hồ sơ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "Gọi API thất bại: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}