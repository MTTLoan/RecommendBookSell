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
            items1.add(new OrderItem(1, 1, 250000, "Bộ ba phép thuật - Úm ba la ánh sáng hiện ra", "https://salt.tikicdn.com/ts/product/18/08/cb/f09cdf8650d2f2a3f397ef968a312783.jpg"));
            orderList.add(new Order(12345, 1, "01/01/2025", 250000, "Đang đóng gói",
                    28756, 829, 83, "123 Đường ABC", items1, "Nguyễn Văn A", "0901234567"));
        } else if (status.equals("Chờ giao hàng")) {
            List<OrderItem> items2 = new ArrayList<>();
            items2.add(new OrderItem(106, 3, 156000, "Cùng con trưởng thành - Mình không thích bị cô lập - Bài học về sự dũng cảm - Dạy trẻ chống lại bạo lực tinh thần - Tránh xa tổn thương", "https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png"));
            items2.add(new OrderItem(9, 2, 299000, "Chuyện Con Mèo Dạy Hải Âu Bay", "https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg"));
            items2.add(new OrderItem(240, 2, 235000, "Những Con Mèo Sau Bức Tường Hoa", "https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg"));
            orderList.add(new Order(1, // id
                    1, // user_id
                    "2024-12-10 22:30:48", // order_date
                    1536000, // total_amount
                    "Chờ giao hàng", // status
                    28756, // shipping_address_ward
                    829, // shipping_address_district
                    83, // shipping_address_province
                    "291 Pasteur", // shipping_address
                    items2,
                    "Mai Thị Thanh Loan", // user_full_name (User)
                    "0123 456 789" ));
        } else if (status.equals("Đã giao")) {
            List<OrderItem> items3 = new ArrayList<>();
            items3.add(new OrderItem(3, 1, 150000, "Sách lật tương tác song ngữ 0 - 3 tuổi - Ú òa", "https://salt.tikicdn.com/ts/product/0f/5d/df/fc5a649af095f77857b807d0a470226b.jpg"));
            orderList.add(new Order(12347, 3, "03/01/2025", 150000, "Đã giao",
                    28756, 829, 83, "789 Đường GHI", items3, "Lê Văn C", "0923456789"));
        } else if (status.equals("Trả hàng")) {
            List<OrderItem> items4 = new ArrayList<>();
            items4.add(new OrderItem(4, 1, 50000, "Truyện Ehon bé 1-2-3 tuổi - Bộ 4 cuốn Em bé biết yêu thương", "https://salt.tikicdn.com/ts/product/74/b4/8f/14ba04275d39e79b15541843bb704064.png"));
            orderList.add(new Order(12348, 4, "04/01/2025", 50000, "Trả hàng",
                    28756, 829, 83, "101 Đường JKL", items4, "Phạm Thị D", "0934567890"));
        } else if (status.equals("Đã hủy")) {
            List<OrderItem> items5 = new ArrayList<>();
            items5.add(new OrderItem(5, 1, 80000, "Tuổi Thơ Dữ Dội - Tập 2", "https://salt.tikicdn.com/ts/product/56/bc/59/f63f4561ee47a86e1843e671fc6355e5.jpg"));
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