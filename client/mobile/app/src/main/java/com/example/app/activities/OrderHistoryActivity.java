package com.example.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.adapters.OrderPagerAdapter;
import com.example.app.adapters.StatusOrderAdapter;
import com.example.app.models.StatusOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView rvStatusBar;
    private ViewPager2 viewPager;
    private OrderPagerAdapter pagerAdapter;
    private StatusOrderAdapter statusAdapter;
    private List<StatusOrder> statusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvStatusBar = findViewById(R.id.rvStatusBar);
        viewPager = findViewById(R.id.viewPager);

        if (rvStatusBar == null) {
            throw new IllegalStateException("rvStatusBar is null. Check layout file or ID.");
        }

        // Khởi tạo danh sách trạng thái
        statusList = new ArrayList<>();
        String[] statuses = {"Đang đóng gói", "Chờ giao hàng", "Đã giao", "Trả hàng", "Đã hủy"};
        for (int i = 0; i < statuses.length; i++) {
            statusList.add(new StatusOrder(statuses[i], i == 0));
        }

        // Thiết lập RecyclerView cho thanh trạng thái
        rvStatusBar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        statusAdapter = new StatusOrderAdapter(statusList, position -> {
            viewPager.setCurrentItem(position, true);
            statusAdapter.setSelectedPosition(position);
        });
        rvStatusBar.setAdapter(statusAdapter);

        // Thiết lập ViewPager2
        pagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Đồng bộ ViewPager2 với thanh trạng thái
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                statusAdapter.setSelectedPosition(position);
                rvStatusBar.scrollToPosition(position);
            }
        });

        // Đặt trang đầu tiên
        viewPager.setCurrentItem(0);
        statusAdapter.setSelectedPosition(0); // Đảm bảo trạng thái đầu tiên được chọn
    }
}