package com.example.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.app.R;
import com.google.android.material.textfield.TextInputEditText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonalInfoActivity extends AppCompatActivity {
    private TextInputEditText etFullName, etEmail, etPhoneNumber, etBirthDate, etAddress;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private RadioButton rbMale, rbFemale;
    private Button btnSave;

    // Dữ liệu từ API
    private List<String> provinceNames = new ArrayList<>();
    private List<Integer> provinceCodes = new ArrayList<>();
    private List<String> districtNames = new ArrayList<>();
    private List<Integer> districtCodes = new ArrayList<>();
    private List<String> wardNames = new ArrayList<>();
    private List<Integer> wardCodes = new ArrayList<>();
    private HashMap<String, Integer> provinceMap = new HashMap<>();
    private HashMap<String, Integer> districtMap = new HashMap<>();
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Ánh xạ view
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBirthDate = findViewById(R.id.etBirthDate);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        etAddress = findViewById(R.id.etAddress);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSave);
        ivReturn = findViewById(R.id.ivReturn);

        // Lấy dữ liệu người dùng từ Intent (giả định)
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            etFullName.setText(bundle.getString("fullName", ""));
            etEmail.setText(bundle.getString("email", ""));
            etPhoneNumber.setText(bundle.getString("phoneNumber", ""));
            etBirthDate.setText(bundle.getString("birthDate", ""));
            etAddress.setText(bundle.getString("address", ""));
            boolean isMale = bundle.getBoolean("gender", true);
            rbMale.setChecked(isMale);
            rbFemale.setChecked(!isMale);
        }

        // Lấy danh sách tỉnh/thành phố từ API
        fetchProvinces();

        // Xử lý khi chọn tỉnh/thành phố
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = provinceNames.get(position);
                int provinceCode = provinceMap.get(selectedProvince);
                fetchDistricts(provinceCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Xử lý khi chọn quận/huyện
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = districtNames.get(position);
                int districtCode = districtMap.get(selectedDistrict);
                fetchWards(districtCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> savePersonalInfo());
        ivReturn.setOnClickListener(v -> finish());
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

                    // Đặt giá trị mặc định từ Intent nếu có
                    String defaultProvince = getIntent().getStringExtra("province");
                    if (defaultProvince != null) {
                        int provinceIndex = provinceNames.indexOf(defaultProvince);
                        if (provinceIndex != -1) spinnerProvince.setSelection(provinceIndex);
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lấy danh sách tỉnh/thành phố: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    provinceNames.add("TP. Hồ Chí Minh");
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

                for (int i = 0; i < districtsArray.length(); i++) {
                    JSONObject district = districtsArray.getJSONObject(i);
                    String name = district.getString("name");
                    int code = district.getInt("code");
                    districtNames.add(name);
                    districtCodes.add(code);
                    districtMap.put(name, code);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtNames);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);

                    // Đặt giá trị mặc định từ Intent nếu có
                    String defaultDistrict = getIntent().getStringExtra("district");
                    if (defaultDistrict != null) {
                        int districtIndex = districtNames.indexOf(defaultDistrict);
                        if (districtIndex != -1) spinnerDistrict.setSelection(districtIndex);
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lấy danh sách quận/huyện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    districtNames.add("Quận Thủ Đức");
                    districtMap.put("Quận Thủ Đức", 774);
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtNames);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);
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

                for (int i = 0; i < wardsArray.length(); i++) {
                    JSONObject ward = wardsArray.getJSONObject(i);
                    String name = ward.getString("name");
                    int code = ward.getInt("code");
                    wardNames.add(name);
                    wardCodes.add(code);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wardNames);
                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerWard.setAdapter(wardAdapter);

                    // Đặt giá trị mặc định từ Intent nếu có
                    String defaultWard = getIntent().getStringExtra("ward");
                    if (defaultWard != null) {
                        int wardIndex = wardNames.indexOf(defaultWard);
                        if (wardIndex != -1) spinnerWard.setSelection(wardIndex);
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lấy danh sách phường/xã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    wardNames.add("Phường Linh Trung");
                    wardCodes.add(26124);
                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, wardNames);
                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerWard.setAdapter(wardAdapter);
                });
            }
        }).start();
    }

    private void savePersonalInfo() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String province = spinnerProvince.getSelectedItem() != null ? spinnerProvince.getSelectedItem().toString() : "";
        String district = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
        String ward = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";
        String address = etAddress.getText().toString().trim();
        String gender = rbMale.isChecked() ? "Nam" : "Nữ";

        // Kiểm tra dữ liệu
        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || birthDate.isEmpty() ||
                province.isEmpty() || district.isEmpty() || ward.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số điện thoại (10 chữ số, bắt đầu bằng 0)
        if (!Pattern.matches("0[0-9]{9}", phoneNumber)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10 chữ số bắt đầu bằng 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu thông tin (giả định)
        String fullAddress = address + ", " + ward + ", " + district + ", " + province;
        Toast.makeText(this, "Lưu thông tin thành công!\nHọ tên: " + fullName + "\nĐịa chỉ: " + fullAddress, Toast.LENGTH_LONG).show();
        finish(); // Quay lại Activity trước
    }
}