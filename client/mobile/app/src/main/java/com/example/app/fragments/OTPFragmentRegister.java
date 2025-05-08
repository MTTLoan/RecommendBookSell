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
import com.example.app.activities.CongratulationActivity;
import com.example.app.activities.ResetPasswordActivity;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPFragmentRegister extends Fragment {
    private PinView pinView;
    private Button btnContinue, btnCancel;
    private TextView tvResendOTP;
    private String email;


    public static OTPFragmentRegister newInstance(String email, String title, String flow) {
        OTPFragmentRegister fragment = new OTPFragmentRegister();
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

            ApiService apiService = RetrofitClient.getApiService();
            Call<ApiService.OtpResponse> call = apiService.verifyOtp(new ApiService.OtpRequest(email, otp));
            call.enqueue(new Callback<ApiService.OtpResponse>() {
                @Override
                public void onResponse(Call<ApiService.OtpResponse> call, Response<ApiService.OtpResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Intent intent = new Intent(getContext(), CongratulationActivity.class);
                        startActivity(intent);
                    } else {
                        try {
                            String error = response.errorBody() != null ? response.errorBody().string() : "Xác minh OTP thất bại";
                            Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Xác minh OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiService.OtpResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Sự kiện nhấn nút "Hủy"
        btnCancel.setOnClickListener(v -> getActivity().finish());

        // Sự kiện nhấn "Gửi lại OTP"
        tvResendOTP.setOnClickListener(v -> {
            ApiService apiService = RetrofitClient.getApiService();
            Call<ApiService.OtpResponse> call = apiService.resendOtp(new ApiService.ResendOtpRequest(email));
            call.enqueue(new Callback<ApiService.OtpResponse>() {
                @Override
                public void onResponse(Call<ApiService.OtpResponse> call, Response<ApiService.OtpResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
                public void onFailure(Call<ApiService.OtpResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}