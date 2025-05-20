package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.chaos.view.PinView;
import com.example.app.R;
import com.example.app.activities.ResetPasswordActivity;
import com.example.app.models.request.ForgotPasswordRequest;
import com.example.app.models.response.ForgotPasswordResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPFragment extends Fragment {
    private PinView pinView;
    private Button btnContinue, btnCancel;
    private TextView tvResendOTP;
    private String email;

    public static OTPFragment newInstance(String email, String title, String flow) {
        OTPFragment fragment = new OTPFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("title", title);
        args.putString("flow", flow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp, container, false);

        // Ánh xạ view
        pinView = view.findViewById(R.id.firstPinView);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvResendOTP = view.findViewById(R.id.tvResendOTP);

        // Lấy email từ Bundle
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        // Sự kiện nhấn nút "Tiếp tục"
        btnContinue.setOnClickListener(v -> {
            String otp = pinView.getText().toString().trim();
            if (otp.length() != 4) {
                Toast.makeText(getContext(), "Vui lòng nhập OTP 4 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạm thời lưu OTP để gửi sang ResetPasswordActivity
            Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("otp", otp);
            startActivity(intent);
            getActivity().finish();
        });

        // Sự kiện nhấn nút "Hủy"
        btnCancel.setOnClickListener(v -> getActivity().finish());

        // Sự kiện nhấn "Gửi lại OTP"
        tvResendOTP.setOnClickListener(v -> {
            ApiService apiService = RetrofitClient.getApiService();
            Call<ForgotPasswordResponse> call = apiService.sendPasswordResetOTP(new ForgotPasswordRequest(email));
            call.enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String message = response.body().getMessage();
                        Toast.makeText(getContext(), message != null ? message : "OTP đã được gửi lại", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Gửi lại OTP thất bại";
                            Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Gửi lại OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}