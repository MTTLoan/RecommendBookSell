package com.example.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.R;
import com.example.app.activities.LoginActivity;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforUserFragment extends Fragment {

    private TextView tvEditPersonalInfoLabel;
    private TextView tvChangePasswordLabel;
    private TextView tvLogOut;
    private SharedPreferences prefs;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_inforuser, container, false);

        // Initialize views
        tvEditPersonalInfoLabel = view.findViewById(R.id.tvEditPersonalInfoLabel);
        tvChangePasswordLabel = view.findViewById(R.id.tvChangePasswordLabel);
        tvLogOut = view.findViewById(R.id.tvlogOut);

        // Initialize SharedPreferences and ApiService
        prefs = requireActivity().getSharedPreferences("MyAppPrefs", requireActivity().MODE_PRIVATE);
        apiService = RetrofitClient.getApiService();

        // Set click listeners
        tvEditPersonalInfoLabel.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chuyển đến màn hình sửa thông tin cá nhân", Toast.LENGTH_SHORT).show();
        });

        tvChangePasswordLabel.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chuyển đến màn hình đổi mật khẩu", Toast.LENGTH_SHORT).show();
        });

        tvLogOut.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> logout())
                    .setNegativeButton("Không", null)
                    .show();
        });

        return view;
    }

    private void logout() {
        String token = prefs.getString("jwt_token", null);
        if (token == null) {
            Toast.makeText(getContext(), "Token không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Đang đăng xuất...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ApiService.LogoutResponse> call = apiService.logout("Bearer " + token);
        call.enqueue(new Callback<ApiService.LogoutResponse>() {
            @Override
            public void onResponse(Call<ApiService.LogoutResponse> call, Response<ApiService.LogoutResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.LogoutResponse logoutResponse = response.body();
                    Toast.makeText(getContext(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (logoutResponse.isSuccess()) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("jwt_token");
                        editor.apply();

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(getContext(), "Đăng xuất thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.LogoutResponse> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}