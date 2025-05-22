package com.example.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.R;
import com.example.app.adapters.OrderConfirmItemAdapter;
import com.example.app.models.Order;
import com.example.app.models.OrderItem;
import com.example.app.models.User;
import com.example.app.models.response.UserResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;
import com.google.android.material.textfield.TextInputEditText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextInputEditText etUserFullName, etUserPhoneNumber, etDetailedAddress;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private TextView tvTotalAmount;
    private RecyclerView rvOrderItems;
    private Button btnPlaceOrder;
    private ImageView ivReturn;
    private RadioGroup rgShippingMethods;
    private RadioButton rbFastShipping, rbEconomyShipping;
    private Order order;
    private ApiService apiService;
    private String authToken;

    // Dữ liệu từ API địa chỉ
    private List<String> provinceNames = new ArrayList<>();
    private List<Integer> provinceCodes = new ArrayList<>();
    private List<String> districtNames = new ArrayList<>();
    private List<Integer> districtCodes = new ArrayList<>();
    private List<String> wardNames = new ArrayList<>();
    private List<Integer> wardCodes = new ArrayList<>();
    private HashMap<String, Integer> provinceMap = new HashMap<>();
    private HashMap<String, Integer> districtMap = new HashMap<>();

    // Biến chi phí giao hàng
    private double shippingCost = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Khởi tạo views
        etUserFullName = findViewById(R.id.etUserFullName);
        etUserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        etDetailedAddress = findViewById(R.id.etDetailedAddress);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rvOrderItems = findViewById(R.id.rv_order_items);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        ivReturn = findViewById(R.id.ivReturn);
        rgShippingMethods = findViewById(R.id.rg_shipping_methods);
        rbFastShipping = findViewById(R.id.rb_fast_shipping);
        rbEconomyShipping = findViewById(R.id.rb_economy_shipping);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Lấy token xác thực
        authToken = AuthUtils.getToken(this);
        if (authToken == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nhận dữ liệu order từ Intent
        order = getIntent().getParcelableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Dữ liệu đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập RecyclerView cho danh sách sản phẩm
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        OrderConfirmItemAdapter adapter = new OrderConfirmItemAdapter(order.getItems());
        rvOrderItems.setAdapter(adapter);

        // Tải thông tin người dùng
        loadUserProfile();

        // Lấy danh sách tỉnh/thành phố
        fetchProvinces();

        // Xử lý sự kiện chọn tỉnh/thành phố
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

        // Xử lý sự kiện chọn quận/huyện
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

        // Xử lý sự kiện chọn hình thức giao hàng
        rgShippingMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_fast_shipping) {
                shippingCost = 20000; // Nhanh
            } else if (checkedId == R.id.rb_economy_shipping) {
                shippingCost = 15000; // Tiết kiệm
            }
            updateTotalPrice();
        });

        // Chọn mặc định "Tiết kiệm"
        rbEconomyShipping.setChecked(true);

        // Xử lý nút quay lại
        ivReturn.setOnClickListener(v -> finish());

        // Xử lý nút Đặt hàng
        btnPlaceOrder.setOnClickListener(v -> validateAndPlaceOrder());
    }

    private void loadUserProfile() {
        Call<UserResponse> call = apiService.getUserProfile("Bearer " + authToken);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    etUserFullName.setText(user.getFullName());
                    etUserPhoneNumber.setText(user.getPhoneNumber());
                    etDetailedAddress.setText(user.getAddressDetail());

                    // Cập nhật spinner sau khi lấy danh sách tỉnh/thành phố
                    fetchProvinces(user);
                } else {
                    Toast.makeText(OrderConfirmationActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    Log.e("OrderConfirmation", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(OrderConfirmationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderConfirmation", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchProvinces() {
        fetchProvinces(null);
    }

    private void fetchProvinces(User user) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://provinces.open-api.vn/api/p/")
                    .build();
            try (okhttp3.Response response = client.newCall(request).execute()) {
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

                    // Đặt giá trị mặc định
                    if (user != null && user.getAddressProvince() != -1) {
                        int provinceIndex = provinceCodes.indexOf(user.getAddressProvince());
                        if (provinceIndex != -1) {
                            spinnerProvince.setSelection(provinceIndex);
                        } else {
                            spinnerProvince.setSelection(provinceNames.indexOf("TP. Hồ Chí Minh"));
                        }
                    } else {
                        spinnerProvince.setSelection(provinceNames.indexOf("TP. Hồ Chí Minh"));
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderConfirmationActivity.this, "Lỗi khi lấy danh sách tỉnh/thành phố: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            try (okhttp3.Response response = client.newCall(request).execute()) {
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

                    // Đặt giá trị mặc định
                    if (order.getShippingDistrict() != 0) {
                        int districtIndex = districtCodes.indexOf(order.getShippingDistrict());
                        if (districtIndex != -1) {
                            spinnerDistrict.setSelection(districtIndex);
                        } else {
                            spinnerDistrict.setSelection(districtNames.indexOf("Quận Thủ Đức"));
                        }
                    } else {
                        spinnerDistrict.setSelection(districtNames.indexOf("Quận Thủ Đức"));
                    }
                });

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderConfirmationActivity.this, "Lỗi khi lấy danh sách quận/huyện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            try (okhttp3.Response response = client.newCall(request).execute()) {
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

                    // Đặt giá trị mặc định
                    int defaultWardCode = order.getShippingWard();
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

    private void validateAndPlaceOrder() {
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

        // Chuẩn bị dữ liệu gửi API
        Order newOrder = new Order();
        newOrder.setUserId(order.getUserId());
        newOrder.setTotalAmount(order.getTotalAmount()+shippingCost);
        newOrder.setShippingCost((int) shippingCost);
        newOrder.setShippingProvince(provinceMap.get(newProvince));
        newOrder.setShippingDistrict(districtMap.get(newDistrict));
        newOrder.setShippingWard(wardCodes.get(spinnerWard.getSelectedItemPosition()));
        newOrder.setShippingDetail(newDetailedAddress);
        newOrder.setItems(order.getItems());

        // Gửi yêu cầu tạo đơn hàng
        Call<Order> call = apiService.addOrder("Bearer " + authToken, newOrder);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showConfirmationDialog(response.body());
                } else {
                    Toast.makeText(OrderConfirmationActivity.this, "Lỗi khi đặt hàng: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("OrderConfirmation", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(OrderConfirmationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderConfirmation", "Error: " + t.getMessage());
            }
        });
    }

    private void showConfirmationDialog(Order createdOrder) {
        // Tạo dialog với layout tùy chỉnh
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_confirmation, null);
        builder.setView(dialogView);

        // Ánh xạ các view trong dialog
        Button btnHome = dialogView.findViewById(R.id.btn_dialog_home);
        Button btnOrders = dialogView.findViewById(R.id.btn_dialog_orders);

        // Tạo dialog
        AlertDialog dialog = builder.create();

        // Xử lý nút Trang chủ
        btnHome.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(OrderConfirmationActivity.this, Menu.class);
            intent.putExtra("fragment_to_show", "home");
            startActivity(intent);
            finish();
        });

        // Xử lý nút Đơn mua
        btnOrders.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(OrderConfirmationActivity.this, Menu.class);
            intent.putExtra("fragment_to_show", "orders");
            startActivity(intent);
            finish();
        });

        // Hiển thị dialog
        dialog.setCancelable(false);
        dialog.show();

        // Hiển thị thông báo
        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalPrice() {
        double totalPrice = order.getTotalAmount() + shippingCost;
        tvTotalAmount.setText(String.format("Tổng: %,d VNĐ", (int) totalPrice));
    }
}