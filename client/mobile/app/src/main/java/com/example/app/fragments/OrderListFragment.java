package com.example.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.OrderAdapter;
import com.example.app.models.Order;
import com.example.app.models.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {
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
        View view = inflater.inflate(com.example.app.R.layout.fragment_order_list, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        // Dữ liệu giả lập
        orderList = new ArrayList<>();
        if (status.equals("Đang đóng gói")) {
            List<OrderItem> items1 = new ArrayList<>();
            items1.add(new OrderItem(1, 1, 250000, "Sách Mindset", "https://example.com/mindset.jpg"));
            orderList.add(new Order(12345, 1, "01/01/2025", 250000, "Đang đóng gói",
                    28756, 829, 83, "123 Đường ABC", items1, "Nguyễn Văn A", "0901234567"));
        } else if (status.equals("Chờ giao hàng")) {
            List<OrderItem> items2 = new ArrayList<>();
            items2.add(new OrderItem(2, 2, 300000, "Sách Tâm Lý Học", "https://example.com/tamly.jpg"));
            orderList.add(new Order(12346, 2, "02/01/2025", 600000, "Chờ giao hàng",
                    28756, 829, 83, "456 Đường DEF", items2, "Trần Thị B", "0912345678"));
        } else if (status.equals("Đã giao")) {
            List<OrderItem> items3 = new ArrayList<>();
            items3.add(new OrderItem(3, 1, 150000, "Sách Lập Trình", "https://example.com/laptrinh.jpg"));
            orderList.add(new Order(12347, 3, "03/01/2025", 150000, "Đã giao",
                    28756, 829, 83, "789 Đường GHI", items3, "Lê Văn C", "0923456789"));
        } else if (status.equals("Trả hàng")) {
            List<OrderItem> items4 = new ArrayList<>();
            items4.add(new OrderItem(4, 1, 50000, "Sách Kỹ Năng", "https://example.com/kynang.jpg"));
            orderList.add(new Order(12348, 4, "04/01/2025", 50000, "Trả hàng",
                    28756, 829, 83, "101 Đường JKL", items4, "Phạm Thị D", "0934567890"));
        } else if (status.equals("Đã hủy")) {
            List<OrderItem> items5 = new ArrayList<>();
            items5.add(new OrderItem(5, 1, 80000, "Sách Kinh Tế", "https://example.com/kinhte.jpg"));
            orderList.add(new Order(12349, 5, "05/01/2025", 80000, "Đã hủy",
                    28756, 829, 83, "202 Đường MNO", items5, "Hoàng Văn E", "0945678901"));
        }

        // Thiết lập RecyclerView
        if (orderList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            OrderAdapter adapter = new OrderAdapter(orderList);
            rvOrders.setAdapter(adapter);
        }

        return view;
    }
}