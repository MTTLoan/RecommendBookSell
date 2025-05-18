package com.example.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.OrderAdapter;
import com.example.app.models.Order;
import com.example.app.models.response.OrderHistoryResponse;
import com.example.app.network.ApiService;
import com.example.app.network.RetrofitClient;
import com.example.app.utils.AuthUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment {

    private static final String TAG = "OrderListFragment";
    private String status;
    private RecyclerView rvOrders;
    private TextView tvEmpty;
    private List<Order> orderList;

    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString("status");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchOrderHistory();

        return view;
    }

    private void fetchOrderHistory() {
        String token = AuthUtils.getToken(requireContext());

        if (token == null) {
            tvEmpty.setText("Vui lòng đăng nhập!");
            tvEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "Request sent with token: Bearer " + token.substring(0, 10) + "...");
        ApiService apiService = RetrofitClient.getApiService();
        Call<OrderHistoryResponse> call = apiService.getOrderHistory("Bearer " + token);
        call.enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Map<String, List<Order>> orderHistory = response.body().getData();
                    if (orderHistory == null) {
                        Log.e(TAG, "Order history data is null");
                        tvEmpty.setText("Không có dữ liệu!");
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                        return;
                    }
                    orderList = orderHistory.getOrDefault(status, new ArrayList<>());
                    if (orderList.isEmpty()) {
                        tvEmpty.setText("Không có đơn hàng!");
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        OrderAdapter adapter = new OrderAdapter(orderList);
                        rvOrders.setAdapter(adapter);
                    }
                } else {
                    Log.e(TAG, "Response failed: " + response.code() + " - " + response.message());
                    try {
                        String rawResponse = response.errorBody() != null ? response.errorBody().string() : response.body() != null ? response.body().toString() : "No body";
                        Log.e(TAG, "Raw response: " + rawResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response body", e);
                    }
                    tvEmpty.setText("Lỗi tải dữ liệu! Mã lỗi: " + response.code());
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {
                Log.e(TAG, "Connection failed: " + t.getMessage(), t);
                tvEmpty.setText("Kết nối thất bại! Lỗi: " + t.getMessage());
                tvEmpty.setVisibility(View.VISIBLE);
                rvOrders.setVisibility(View.GONE);
            }
        });
    }
}