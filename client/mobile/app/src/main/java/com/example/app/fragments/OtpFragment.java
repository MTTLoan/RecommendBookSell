package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class OtpFragment extends Fragment {

    private static final String TAG = "OtpFragment";
    private TextView tvTitle, tvResendOtp, tvSubtitleOtp;
    private Button btnContinue, btnCancel;
    private PinView pinView;
    private ImageView ivReturn;
    private String userEmail;
    private String flow; // "registration" or "forgot_password"
    private CountDownTimer countDownTimer;

    public OtpFragment() {}

    public static OtpFragment newInstance(String email, String title, String flow) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("title", title);
        args.putString("flow", flow);
        Log.d(TAG, "Email set in bundle: " + email + ", Flow: " + flow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp, container, false);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvResendOtp = view.findViewById(R.id.tvResendOTP);
        tvSubtitleOtp = view.findViewById(R.id.tvSubtitleOTP);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnCancel = view.findViewById(R.id.btnCancel);
        pinView = view.findViewById(R.id.firstPinView);
        ivReturn = view.findViewById(R.id.ivReturn);

        // Retrieve email, title, and flow from Bundle
        if (getArguments() != null) {
            userEmail = getArguments().getString("email");
            String title = getArguments().getString("title");
            flow = getArguments().getString("flow", "registration");
            tvTitle.setText(title);
            Log.d(TAG, "Arguments - email: " + userEmail + ", title: " + title + ", flow: " + flow);
        }

        tvSubtitleOtp.setText("Mã xác nhận đã được gửi đến email: " + userEmail);

        // Single btnContinue listener
        btnContinue.setOnClickListener(v -> {
            String otp = pinView.getText().toString().trim();
            if (otp.length() == 4) {
                verifyOtp(otp);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ mã OTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());

        // Return button
        ivReturn.setOnClickListener(v -> requireActivity().onBackPressed());

        // Resend OTP
        tvResendOtp.setOnClickListener(v -> resendOtp());

        startResendCooldown();

        return view;
    }

    private void verifyOtp(String otp) {
        ApiService apiService = RetrofitClient.getApiService();
        ApiService.OtpRequest request = new ApiService.OtpRequest(userEmail, otp);

        apiService.verifyOtp(request).enqueue(new Callback<ApiService.OtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.OtpResponse> call, @NonNull Response<ApiService.OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(getContext(), "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                        // Navigate based on flow
                        Intent intent;
                        if ("forgot_password".equals(flow)) {
                            intent = new Intent(getContext(), ResetPasswordActivity.class);
                            intent.putExtra("email", userEmail);
                            intent.putExtra("otp", otp); // Pass the OTP
                        } else {
                            intent = new Intent(getContext(), CongratulationActivity.class);
                            intent.putExtra("title", "Đăng ký");
                        }
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Xác minh OTP thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.OtpResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        ApiService apiService = RetrofitClient.getApiService();
        ApiService.ResendOtpRequest request = new ApiService.ResendOtpRequest(userEmail);

        apiService.resendOtp(request).enqueue(new Callback<ApiService.OtpResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiService.OtpResponse> call, @NonNull Response<ApiService.OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gửi lại OTP thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiService.OtpResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi gửi lại OTP", Toast.LENGTH_SHORT).show();
            }
        });

        startResendCooldown();
    }

    private void startResendCooldown() {
        tvResendOtp.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvResendOtp.setText("Gửi lại OTP (" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                tvResendOtp.setEnabled(true);
                tvResendOtp.setText(getString(R.string.resend));
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}