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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        List<Order> orderList = new ArrayList<>();

        DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (status.equals("Đang đóng gói")) {
            List<OrderItem> items1 = new ArrayList<>();
            items1.add(new OrderItem(1, 1, 250000));
            LocalDateTime orderDate1 = LocalDateTime.parse("2024-12-10 22:30:48", formatterFull);
            orderList.add(new Order(
                    12345, // id
                    1, // userId
                    orderDate1, // orderDate
                    250000, // totalAmount
                    "Đang đóng gói", // status
                    83, // shippingProvince
                    829, // shippingDistrict
                    28756, // shippingWard
                    "123 Đường ABC", // shippingDetail
                    items1, // items
                    LocalDateTime.now(), // createdAt
                    LocalDateTime.now(), // updatedAt
                    "Nguyễn Văn A", // userFullName
                    "0901234567" // userPhoneNumber
            ));
        } else if (status.equals("Chờ giao hàng")) {
            List<OrderItem> items2 = new ArrayList<>();
            items2.add(new OrderItem(106, 3, 156000));
            items2.add(new OrderItem(9, 2, 299000));
            items2.add(new OrderItem(240, 2, 235000));
            LocalDateTime orderDate2 = LocalDateTime.parse("2024-12-10 22:30:48", formatterFull);
            orderList.add(new Order(
                    1, // id
                    1, // userId
                    orderDate2, // orderDate
                    1536000, // totalAmount
                    "Chờ giao hàng", // status
                    83, // shippingProvince
                    829, // shippingDistrict
                    28756, // shippingWard
                    "291 Pasteur", // shippingDetail
                    items2, // items
                    LocalDateTime.now(), // createdAt
                    LocalDateTime.now(), // updatedAt
                    "Mai Thị Thanh Loan", // userFullName
                    "0123456789" // userPhoneNumber
            ));
        } else if (status.equals("Đã giao")) {
            List<OrderItem> items3 = new ArrayList<>();
            items3.add(new OrderItem(3, 1, 150000));
            LocalDateTime orderDate3 = LocalDateTime.parse("2024-12-10 22:30:48", formatterFull);
            orderList.add(new Order(
                    12347, // id
                    3, // userId
                    orderDate3, // orderDate
                    150000, // totalAmount
                    "Đã giao", // status
                    83, // shippingProvince
                    829, // shippingDistrict
                    28756, // shippingWard
                    "789 Đường GHI", // shippingDetail
                    items3, // items
                    LocalDateTime.now(), // createdAt
                    LocalDateTime.now(), // updatedAt
                    "Lê Văn C", // userFullName
                    "0923456789" // userPhoneNumber
            ));
        } else if (status.equals("Trả hàng")) {
            List<OrderItem> items4 = new ArrayList<>();
            items4.add(new OrderItem(4, 1, 50000));
            LocalDateTime orderDate4 = LocalDateTime.parse("2024-12-10 22:30:48", formatterFull);
            orderList.add(new Order(
                    12348, // id
                    4, // userId
                    orderDate4, // orderDate
                    50000, // totalAmount
                    "Trả hàng", // status
                    83, // shippingProvince
                    829, // shippingDistrict
                    28756, // shippingWard
                    "101 Đường JKL", // shippingDetail
                    items4, // items
                    LocalDateTime.now(), // createdAt
                    LocalDateTime.now(), // updatedAt
                    "Phạm Thị D", // userFullName
                    "0934567890" // userPhoneNumber
            ));
        } else if (status.equals("Đã hủy")) {
            List<OrderItem> items5 = new ArrayList<>();
            items5.add(new OrderItem(5, 1, 80000));
            LocalDateTime orderDate5 = LocalDateTime.parse("2024-12-10 22:30:48", formatterFull);
            orderList.add(new Order(
                    12349, // id
                    5, // userId
                    orderDate5, // orderDate
                    80000, // totalAmount
                    "Đã hủy", // status
                    83, // shippingProvince
                    829, // shippingDistrict
                    28756, // shippingWard
                    "202 Đường MNO", // shippingDetail
                    items5, // items
                    LocalDateTime.now(), // createdAt
                    LocalDateTime.now(), // updatedAt
                    "Hoàng Văn E", // userPhoneNumber
                    "0945678901" // userPhoneNumber
            ));
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