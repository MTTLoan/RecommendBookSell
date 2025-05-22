package com.example.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.models.User;
import com.example.app.models.response.UserResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class PersonalInfoActivity extends AppCompatActivity {
    private TextInputEditText etFullName, etPhoneNumber, etBirthDate, etAddress;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private Button btnSave;
    private ImageView ivReturn;
    private ApiService apiService;
    private String token;
    private User currentUser;

    // Dữ liệu từ API provinces.open-api.vn
    private List<String> provinceNames = new ArrayList<>();
    private List<Integer> provinceCodes = new ArrayList<>();
    private List<String> districtNames = new ArrayList<>();
    private List<Integer> districtCodes = new ArrayList<>();
    private List<String> wardNames = new ArrayList<>();
    private List<Integer> wardCodes = new ArrayList<>();
    private HashMap<String, Integer> provinceMap = new HashMap<>();
    private HashMap<String, Integer> districtMap = new HashMap<>();
    private HashMap<String, Integer> wardMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Ánh xạ view
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBirthDate = findViewById(R.id.etBirthDate);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        ivReturn = findViewById(R.id.ivReturn);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Lấy token
        token = AuthUtils.getToken(this);
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Thiết lập DatePicker cho ngày sinh
        etBirthDate.setOnClickListener(v -> showDatePickerDialog());

        // Lấy danh sách tỉnh/thành phố từ API
        fetchProvinces();

        // Xử lý khi chọn tỉnh/thành phố
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = provinceNames.get(position);
                if (!selectedProvince.equals("Chọn tỉnh/thành phố")) {
                    Integer provinceCode = provinceMap.get(selectedProvince);
                    if (provinceCode != null) {
                        fetchDistricts(provinceCode);
                    }
                } else {
                    districtNames.clear();
                    districtNames.add("Chọn quận/huyện");
                    districtCodes.clear();
                    districtMap.clear();
                    wardNames.clear();
                    wardNames.add("Chọn phường/xã");
                    wardCodes.clear();
                    wardMap.clear();
                    updateDistrictSpinner();
                    updateWardSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                districtNames.clear();
                districtNames.add("Chọn quận/huyện");
                districtCodes.clear();
                districtMap.clear();
                wardNames.clear();
                wardNames.add("Chọn phường/xã");
                wardCodes.clear();
                wardMap.clear();
                updateDistrictSpinner();
                updateWardSpinner();
            }
        });

        // Xử lý khi chọn quận/huyện
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = districtNames.get(position);
                if (!selectedDistrict.equals("Chọn quận/huyện")) {
                    Integer districtCode = districtMap.get(selectedDistrict);
                    if (districtCode != null) {
                        fetchWards(districtCode);
                    }
                } else {
                    wardNames.clear();
                    wardNames.add("Chọn phường/xã");
                    wardCodes.clear();
                    wardMap.clear();
                    updateWardSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                wardNames.clear();
                wardNames.add("Chọn phường/xã");
                wardCodes.clear();
                wardMap.clear();
                updateWardSpinner();
            }
        });

        // Xử lý khi chọn phường/xã
        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Không cần xử lý thêm, code được lấy khi lưu
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> savePersonalInfo());

        // Xử lý nút Quay lại
        ivReturn.setOnClickListener(v -> finish());

        // Lấy thông tin người dùng từ API
        fetchUserProfile();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    etBirthDate.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void fetchProvinces() {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://provinces.open-api.vn/api/p/")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String jsonData = response.body().string();
                JSONArray provincesArray = new JSONArray(jsonData);

                provinceNames.clear();
                provinceCodes.clear();
                provinceMap.clear();

                // Thêm tùy chọn mặc định
                provinceNames.add("Chọn tỉnh/thành phố");

                for (int i = 0; i < provincesArray.length(); i++) {
                    JSONObject province = provincesArray.getJSONObject(i);
                    String name = province.getString("name");
                    int code = province.getInt("code");
                    provinceNames.add(name);
                    provinceCodes.add(code);
                    provinceMap.put(name, code);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceNames);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvince.setAdapter(provinceAdapter);

                    // Đặt giá trị mặc định từ currentUser nếu có
                    if (currentUser != null && currentUser.getAddressProvince() != -1) {
                        Integer provinceCode = currentUser.getAddressProvince();
                        for (int i = 0; i < provinceCodes.size(); i++) {
                            if (provinceCodes.get(i).equals(provinceCode)) {
                                spinnerProvince.setSelection(i + 1); // +1 vì có "Chọn tỉnh/thành phố"
                                break;
                            }
                        }
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lấy danh sách tỉnh/thành phố: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    provinceNames.clear();
                    provinceNames.add("Chọn tỉnh/thành phố");
                    provinceNames.add("TP. Hồ Chí Minh");
                    provinceCodes.clear();
                    provinceCodes.add(79);
                    provinceMap.put("TP. Hồ Chí Minh", 79);
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceNames);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvince.setAdapter(provinceAdapter);
                });
            }
        }).start();
    }

    private void fetchDistricts(int provinceCode) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://provinces.open-api.vn/api/p/" + provinceCode + "?depth=2")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String jsonData = response.body().string();
                JSONObject provinceData = new JSONObject(jsonData);
                JSONArray districtsArray = provinceData.getJSONArray("districts");

                districtNames.clear();
                districtCodes.clear();
                districtMap.clear();

                // Thêm tùy chọn mặc định
                districtNames.add("Chọn quận/huyện");

                for (int i = 0; i < districtsArray.length(); i++) {
                    JSONObject district = districtsArray.getJSONObject(i);
                    String name = district.getString("name");
                    int code = district.getInt("code");
                    districtNames.add(name);
                    districtCodes.add(code);
                    districtMap.put(name, code);
                }

                runOnUiThread(() -> {
                    updateDistrictSpinner();

                    // Đặt giá trị mặc định từ currentUser nếu có
                    if (currentUser != null && currentUser.getAddressDistrict() != -1) {
                        Integer districtCode = currentUser.getAddressDistrict();
                        for (int i = 0; i < districtCodes.size(); i++) {
                            if (districtCodes.get(i).equals(districtCode)) {
                                spinnerDistrict.setSelection(i + 1); // +1 vì có "Chọn quận/huyện"
                                break;
                            }
                        }
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lấy danh sách quận/huyện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    districtNames.clear();
                    districtNames.add("Chọn quận/huyện");
                    districtNames.add("TP. Thủ Đức");
                    districtCodes.clear();
                    districtCodes.add(769);
                    districtMap.put("TP. Thủ Đức", 769);
                    updateDistrictSpinner();
                });
            }
        }).start();
    }

    private void fetchWards(int districtCode) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://provinces.open-api.vn/api/d/" + districtCode + "?depth=2")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String jsonData = response.body().string();
                JSONObject districtData = new JSONObject(jsonData);
                JSONArray wardsArray = districtData.getJSONArray("wards");

                wardNames.clear();
                wardCodes.clear();
                wardMap.clear();

                // Thêm tùy chọn mặc định
                wardNames.add("Chọn phường/xã");

                for (int i = 0; i < wardsArray.length(); i++) {
                    JSONObject ward = wardsArray.getJSONObject(i);
                    String name = ward.getString("name");
                    int code = ward.getInt("code");
                    wardNames.add(name);
                    wardCodes.add(code);
                    wardMap.put(name, code);
                }

                runOnUiThread(() -> {
                    updateWardSpinner();

                    // Đặt giá trị mặc định từ currentUser nếu có
                    if (currentUser != null && currentUser.getAddressWard() != -1) {
                        Integer wardCode = currentUser.getAddressWard();
                        for (int i = 0; i < wardCodes.size(); i++) {
                            if (wardCodes.get(i).equals(wardCode)) {
                                spinnerWard.setSelection(i + 1); // +1 vì có "Chọn phường/xã"
                                break;
                            }
                        }
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lấy danh sách phường/xã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    wardNames.clear();
                    wardNames.add("Chọn phường/xã");
                    wardNames.add("Phường Linh Trung");
                    wardCodes.clear();
                    wardCodes.add(26124);
                    wardMap.put("Phường Linh Trung", 26124);
                    updateWardSpinner();
                });
            }
        }).start();
    }

    private void updateDistrictSpinner() {
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtNames);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);
    }

    private void updateWardSpinner() {
        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wardNames);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);
    }

    private void fetchUserProfile() {
        Call<UserResponse> call = apiService.getUserProfile("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body().getUser();
                    if (currentUser != null) {
                        populateUserData();
                    } else {
                        Toast.makeText(PersonalInfoActivity.this, "Không lấy được thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Lỗi lấy thông tin người dùng.";
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("PersonalInfoActivity", "Error body: " + errorBody);
                        JSONObject errorJson = new JSONObject(errorBody);
                        if (errorJson.has("msg")) {
                            errorMessage = errorJson.getString("msg");
                        }
                    } catch (Exception e) {
                        Log.e("PersonalInfoActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(PersonalInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                String errorMessage = t instanceof IOException ? "Lỗi mạng" : "Lỗi không xác định";
                Toast.makeText(PersonalInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("PersonalInfoActivity", "API failure", t);
            }
        });
    }

    private void populateUserData() {
        etFullName.setText(currentUser.getFullName());
        etPhoneNumber.setText(currentUser.getPhoneNumber());
        etAddress.setText(currentUser.getAddressDetail());
        // Handle birthday formatting
        if (currentUser.getBirthday() != null && !currentUser.getBirthday().isEmpty()) {
            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                java.util.Date date = apiFormat.parse(currentUser.getBirthday());
                if (date != null) {
                    etBirthDate.setText(displayFormat.format(date));
                } else {
                    etBirthDate.setText("");
                    Log.w("PersonalInfoActivity", "Parsed date is null for birthday: " + currentUser.getBirthday());
                }
            } catch (Exception e) {
                Log.e("PersonalInfoActivity", "Error parsing birthday: " + currentUser.getBirthday(), e);
                etBirthDate.setText(currentUser.getBirthday()); // Fallback to raw value
            }
        } else {
            etBirthDate.setText("");
        }
    }

    private void savePersonalInfo() {
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String addressDetail = etAddress.getText().toString().trim();

        // Lấy code từ spinner
        String selectedProvince = spinnerProvince.getSelectedItem() != null ? spinnerProvince.getSelectedItem().toString() : "";
        String selectedDistrict = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
        String selectedWard = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";
        Integer provinceCode = selectedProvince.equals("Chọn tỉnh/thành phố") ? null : provinceMap.get(selectedProvince);
        Integer districtCode = selectedDistrict.equals("Chọn quận/huyện") ? null : districtMap.get(selectedDistrict);
        Integer wardCode = selectedWard.equals("Chọn phường/xã") ? null : wardMap.get(selectedWard);

        // Kiểm tra dữ liệu
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ họ tên và số điện thoại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số điện thoại (10 chữ số, bắt đầu bằng 0)
        if (!Pattern.matches("0[0-9]{9}", phoneNumber)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10 chữ số bắt đầu bằng 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xử lý birthday
        String formattedBirthDate = null;
        if (!TextUtils.isEmpty(birthDate)) {
            // Kiểm tra định dạng DD/MM/YYYY
            if (!Pattern.matches("\\d{2}/\\d{2}/\\d{4}", birthDate)) {
                Toast.makeText(this, "Ngày sinh không hợp lệ! Vui lòng nhập định dạng DD/MM/YYYY.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // Chuyển từ DD/MM/YYYY sang YYYY-MM-DD
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                java.util.Date date = inputFormat.parse(birthDate);
                formattedBirthDate = outputFormat.format(date);
            } catch (ParseException e) {
                Toast.makeText(this, "Ngày sinh không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Tạo User object để gửi
        User updatedUser = new User();
        updatedUser.setFullName(fullName);
        updatedUser.setPhoneNumber(phoneNumber);
        updatedUser.setBirthday(formattedBirthDate);
        updatedUser.setAddressDetail(TextUtils.isEmpty(addressDetail) ? null : addressDetail);
        updatedUser.setAddressProvince(provinceCode);
        updatedUser.setAddressDistrict(districtCode);
        updatedUser.setAddressWard(wardCode);

        // Log dữ liệu gửi đi để debug
        Log.d("PersonalInfoActivity", "Updated user: " + new com.google.gson.Gson().toJson(updatedUser));

        // Gọi API cập nhật hồ sơ
        btnSave.setEnabled(false);
        Call<UserResponse> call = apiService.updateUserProfile("Bearer " + token, updatedUser);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PersonalInfoActivity.this, "Cập nhật hồ sơ thành công.", Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("update_profile_success", true);
                    setResult(RESULT_OK, resultIntent);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 2000);
                } else {
                    String errorMessage = "Lỗi cập nhật hồ sơ.";
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("PersonalInfoActivity", "Error body: " + errorBody);
                        JSONObject errorJson = new JSONObject(errorBody);
                        if (errorJson.has("msg")) {
                            errorMessage = errorJson.getString("msg");
                        }
                    } catch (Exception e) {
                        Log.e("PersonalInfoActivity", "Error parsing error body", e);
                    }
                    Toast.makeText(PersonalInfoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                String errorMessage = t instanceof IOException ? "Lỗi mạng" : "Lỗi không xác định";
                Toast.makeText(PersonalInfoActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("PersonalInfoActivity", "API failure", t);
            }
        });
    }
}