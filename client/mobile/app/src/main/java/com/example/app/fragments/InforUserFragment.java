package com.example.app.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InforUserFragment extends Fragment {

    private static final String TAG = "InforUserFragment";
    private static final int REQUEST_PERMISSIONS = 3;

    private TextView tvEditPersonalInfoLabel;
    private TextView tvChangePasswordLabel;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivAvatar;
    private TextView tvLogOut;
    private ApiService apiService;
    private String token;
    private Uri imageUri;

    // ActivityResultLauncher để xử lý kết quả từ camera và gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    Log.d(TAG, "Selected image URI: " + imageUri);
                    uploadImage(imageUri);
                    Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
                }
            });

    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "Captured image URI: " + imageUri);
                    uploadImage(imageUri);
                    Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
                }
            });

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
        token = AuthUtils.getToken(requireContext());
        if (token == null) {
            Log.e(TAG, "Token not found in SharedPreferences");
            Toast.makeText(getContext(), "Token không tồn tại. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
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

        // Click vào ivAvatar để chọn ảnh
        ivAvatar.setOnClickListener(v -> {
            Log.d(TAG, "Clicked on ivAvatar");
            showImagePickerDialog();
        });

        return view;
    }

    private void showImagePickerDialog() {
        if (!checkPermissions()) {
            Log.d(TAG, "Permissions not granted, requesting...");
            requestPermissions();
            return;
        }

        Log.d(TAG, "Permissions granted, showing dialog");
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn ảnh")
                .setItems(new CharSequence[]{"Chụp ảnh", "Chọn từ thư viện"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openCamera();
                            break;
                        case 1:
                            openGallery();
                            break;
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private boolean checkPermissions() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storagePermission;

        // Android 15 (API 36) yêu cầu quyền chi tiết hơn
        storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;

        Log.d(TAG, "Camera permission: " + cameraPermission + ", Storage permission: " + storagePermission);
        return cameraPermission && storagePermission;
    }

    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions granted after request, showing dialog");
                showImagePickerDialog();
            } else {
                Log.w(TAG, "Permissions denied");
                Toast.makeText(getContext(), "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file: " + ex.getMessage());
                Toast.makeText(getContext(), "Lỗi tạo file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                captureImageLauncher.launch(cameraIntent);
            }
        } else {
            Toast.makeText(getContext(), "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Log.d(TAG, "Opening gallery");
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImageLauncher.launch(pickPhoto);
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null");
            Toast.makeText(getContext(), "Không có ảnh để tải lên", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Đang tải ảnh lên...");
        dialog.setCancelable(false);
        dialog.show();

        try {
            // Chuyển đổi URI thành File bằng ContentResolver
            ContentResolver contentResolver = requireContext().getContentResolver();
            File file = createTempFileFromUri(imageUri, contentResolver);
            RequestBody requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(imageUri)), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            // Sử dụng token trong header
            String authHeader = "Bearer " + token;
            Call<UserResponse> call = apiService.uploadAvatar(authHeader, body);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse userResponse = response.body();
                        if (userResponse.isSuccess()) {
                            String msg = userResponse.getMessage() != null ? userResponse.getMessage() : "Cập nhật avatar thành công!";
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            String newAvatarUrl = userResponse.getAvatar();
                            if (newAvatarUrl != null && !newAvatarUrl.isEmpty()) {
                                Glide.with(InforUserFragment.this)
                                        .load(newAvatarUrl)
                                        .placeholder(R.drawable.avt)
                                        .apply(RequestOptions.circleCropTransform())
                                        .error(R.drawable.avt)
                                        .into(ivAvatar);
                            }
                            fetchUserProfile(token); // Đồng bộ lại profile
                        } else {
                            String msg = userResponse.getMessage() != null ? userResponse.getMessage() : "Cập nhật avatar thất bại!";
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                            Log.e(TAG, "Cập nhật ảnh thất bại: " + errorBody);
                            Toast.makeText(getContext(), "Cập nhật ảnh thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi phân tích phản hồi: " + e.getMessage());
                            Toast.makeText(getContext(), "Cập nhật ảnh thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e(TAG, "Gọi API cập nhật ảnh thất bại: " + t.getMessage());
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            dialog.dismiss();
            Log.e(TAG, "Lỗi chuyển đổi URI thành file: " + e.getMessage());
            Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createTempFileFromUri(Uri uri, ContentResolver contentResolver) throws IOException {
        File tempFile = File.createTempFile("temp_image", ".jpg", requireContext().getExternalFilesDir(null));
        try (InputStream inputStream = contentResolver.openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            if (inputStream != null) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                Log.d(TAG, "Temp file created: " + tempFile.getAbsolutePath());
            } else {
                throw new IOException("Không thể mở InputStream từ URI");
            }
        }
        return tempFile;
    }

    private void logout(String token) {
        ProgressDialog dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Đang đăng xuất...");
        dialog.setCancelable(false);
        dialog.show();

        Call<LogoutResponse> call = apiService.logout("Bearer " + token);
        Log.d(TAG, "Logout request URL: " + call.request().url());
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                dialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    LogoutResponse logoutResponse = response.body();
                    String msg = logoutResponse.getMessage() != null ? logoutResponse.getMessage() : "Đăng xuất thành công!";
                    Log.d(TAG, "Logout response body: " + new String(response.body().toString()));
                    Log.d(TAG, "Logout response - success: " + logoutResponse.isSuccess());
                    Log.d(TAG, "Logout response - message: " + logoutResponse.getMessage());
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                    if (logoutResponse.isSuccess()) {
                        AuthUtils.clearToken(requireContext());
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
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

                        tvName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
                        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");

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
                        String msg = userResponse.getMessage() != null ? userResponse.getMessage() : "Không tìm thấy thông tin hồ sơ";
                        Log.e(TAG, "Đối tượng User null trong phản hồi");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(), "Không thể lấy thông tin hồ sơ", Toast.LENGTH_SHORT).show();
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