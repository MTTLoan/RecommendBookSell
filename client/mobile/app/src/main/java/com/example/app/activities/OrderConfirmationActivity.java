package com.example.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.adapters.OrderConfirmItemAdapter;
import com.example.app.models.Order;
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

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextInputEditText etUserFullName, etUserPhoneNumber, etDetailedAddress;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private TextView tvTotalAmount;
    private RecyclerView rvOrderItems;
    private Button btnPlaceOrder;
    private RadioGroup rgShippingMethods;
    private RadioButton rbFastShipping, rbEconomyShipping;
    private Order order;

    // Biến lưu chi phí giao hàng
    private double shippingCost = 15000; // Mặc định: Tiết kiệm

    // Dữ liệu từ API
    private List<String> provinceNames = new ArrayList<>();
    private List<Integer> provinceCodes = new ArrayList<>();
    private List<String> districtNames = new ArrayList<>();
    private List<Integer> districtCodes = new ArrayList<>();
    private List<String> wardNames = new ArrayList<>();
    private List<Integer> wardCodes = new ArrayList<>();
    private HashMap<String, Integer> provinceMap = new HashMap<>();
    private HashMap<String, Integer> districtMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Ánh xạ view
        etUserFullName = findViewById(R.id.etUserFullName);
        etUserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        etDetailedAddress = findViewById(R.id.etDetailedAddress);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rvOrderItems = findViewById(R.id.rv_order_items);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        rgShippingMethods = findViewById(R.id.rg_shipping_methods);
        rbFastShipping = findViewById(R.id.rb_fast_shipping);
        rbEconomyShipping = findViewById(R.id.rb_economy_shipping);

        // Lấy dữ liệu Order từ Intent
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                order = (Order) bundle.getSerializable("order");
            }
            if (order == null) {
                throw new IllegalStateException("Order không được tìm thấy trong Intent");
            }

            // Đặt giá trị mặc định từ Order
            etUserFullName.setText(order.getUserFullName() != null ? order.getUserFullName() : "");
            etUserPhoneNumber.setText(order.getUserPhoneNumber() != null ? order.getUserPhoneNumber() : "");
            etDetailedAddress.setText(order.getShippingDetailedAddress() != null ? order.getShippingDetailedAddress() : "37 đường số 8");
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi nhận dữ liệu đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Hiển thị danh sách sản phẩm
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderConfirmItemAdapter adapter = new OrderConfirmItemAdapter(order.getItems());
        rvOrderItems.setAdapter(adapter);

        // Hiển thị tổng giá trị (bao gồm chi phí giao hàng mặc định)
        updateTotalPrice();

        // Lấy danh sách tỉnh/thành phố từ API
        fetchProvinces();

        // Chọn mặc định "Tiết kiệm"
        rbEconomyShipping.setChecked(true);

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

        // Xử lý thay đổi lựa chọn hình thức giao hàng
        rgShippingMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_fast_shipping) {
                shippingCost = 20000; // Nhanh
            } else if (checkedId == R.id.rb_economy_shipping) {
                shippingCost = 15000; // Tiết kiệm
            }
            updateTotalPrice();
        });

        // Xử lý nút Đặt hàng
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
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
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(OrderConfirmationActivity.this, android.R.layout.simple_spinner_item, provinceNames);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvince.setAdapter(provinceAdapter);

                    // Đặt giá trị mặc định dựa trên Order
                    int defaultProvinceCode = order.getShippingAddressProvinceCode(); // 79 (TP. Hồ Chí Minh)
                    int provinceIndex = provinceCodes.indexOf(defaultProvinceCode);
                    if (provinceIndex != -1) {
                        spinnerProvince.setSelection(provinceIndex);
                    } else {
                        spinnerProvince.setSelection(provinceNames.indexOf("TP. Hồ Chí Minh"));
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderConfirmationActivity.this, "Lỗi khi lấy danh sách tỉnh/thành phố: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Dữ liệu mặc định nếu API thất bại
                    provinceNames.clear();
                    provinceNames.add("TP. Hồ Chí Minh");
                    provinceMap.put("TP. Hồ Chí Minh", 79);
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(OrderConfirmationActivity.this, android.R.layout.simple_spinner_item, provinceNames);
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
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(OrderConfirmationActivity.this, android.R.layout.simple_spinner_item, districtNames);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);

                    // Đặt giá trị mặc định dựa trên Order
                    int defaultDistrictCode = order.getShippingAddressDistrictCode(); // 774 (Quận Thủ Đức)
                    int districtIndex = districtCodes.indexOf(defaultDistrictCode);
                    if (districtIndex != -1) {
                        spinnerDistrict.setSelection(districtIndex);
                    } else {
                        spinnerDistrict.setSelection(districtNames.indexOf("Quận Thủ Đức"));
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderConfirmationActivity.this, "Lỗi khi lấy danh sách quận/huyện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Dữ liệu mặc định nếu API thất bại
                    districtNames.clear();
                    districtNames.add("Quận Thủ Đức");
                    districtMap.put("Quận Thủ Đức", 774);
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(OrderConfirmationActivity.this, android.R.layout.simple_spinner_item, districtNames);
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
                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(OrderConfirmationActivity.this, android.R.layout.simple_spinner_item, wardNames);
                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerWard.setAdapter(wardAdapter);

                    // Đặt giá trị mặc định dựa trên Order
                    int defaultWardCode = order.getShippingAddressWardCode(); // 26124 (Phường Linh Trung)
                    int wardIndex = wardCodes.indexOf(defaultWardCode);
                    if (wardIndex != -1) {
                        spinnerWard.setSelection(wardIndex);
                    } else {
                        spinnerWard.setSelection(wardNames.indexOf("Phường Linh Trung"));
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderConfirmationActivity.this, "Lỗi khi lấy danh sách phường/xã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Dữ liệu mặc định nếu API thất bại
                    wardNames.clear();
                    wardNames.add("Phường Linh Trung");
                    wardCodes.add(26124);
                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(OrderConfirmationActivity.this, android.R.layout.simple_spinner_item, wardNames);
                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerWard.setAdapter(wardAdapter);
                });
            }
        }).start();
    }

    private void updateTotalPrice() {
        double totalPrice = order.getTotalAmount() + shippingCost;
        tvTotalAmount.setText(String.format("Tổng: %,d VNĐ", (int) totalPrice));
    }

    private void placeOrder() {
        // Lấy dữ liệu đã chỉnh sửa
        String newFullName = etUserFullName.getText().toString().trim();
        String newPhoneNumber = etUserPhoneNumber.getText().toString().trim();
        String newProvince = spinnerProvince.getSelectedItem() != null ? spinnerProvince.getSelectedItem().toString() : "";
        String newDistrict = spinnerDistrict.getSelectedItem() != null ? spinnerDistrict.getSelectedItem().toString() : "";
        String newWard = spinnerWard.getSelectedItem() != null ? spinnerWard.getSelectedItem().toString() : "";
        String newDetailedAddress = etDetailedAddress.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (newFullName.isEmpty() || newPhoneNumber.isEmpty() || newDetailedAddress.isEmpty() || newProvince.isEmpty() || newDistrict.isEmpty() || newWard.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số điện thoại (10 chữ số, bắt đầu bằng 0)
        if (!Pattern.matches("0[0-9]{9}", newPhoneNumber)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10 chữ số bắt đầu bằng 0.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật order với dữ liệu mới
        order.setUserFullName(newFullName);
        order.setUserPhoneNumber(newPhoneNumber);
        order.setShippingDetailedAddress(newDetailedAddress + ", " + newWard + ", " + newDistrict + ", " + newProvince);
        order.setTotalAmount(order.getTotalAmount() + shippingCost); // Cập nhật tổng giá

        Toast.makeText(this, "Đặt hàng thành công với địa chỉ mới!", Toast.LENGTH_SHORT).show();
        finish();
    }
}