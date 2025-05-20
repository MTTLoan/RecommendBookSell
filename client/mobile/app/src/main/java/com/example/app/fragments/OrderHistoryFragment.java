package com.example.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.adapters.OrderPagerAdapter;
import com.example.app.adapters.StatusOrderAdapter;
import com.example.app.models.StatusOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvStatusBar;
    private ViewPager2 viewPager;
    private OrderPagerAdapter pagerAdapter;
    private StatusOrderAdapter statusAdapter;
    private List<StatusOrder> statusList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_history, container, false);

        rvStatusBar = view.findViewById(R.id.rvStatusBar);
        viewPager = view.findViewById(R.id.viewPager);

        // Khởi tạo danh sách trạng thái
        statusList = new ArrayList<>();
        String[] statuses = {"Đang đóng gói", "Chờ giao hàng", "Đã giao", "Trả hàng", "Đã hủy"};
        for (int i = 0; i < statuses.length; i++) {
            statusList.add(new StatusOrder(statuses[i], i == 0));
        }

        // Thiết lập RecyclerView cho thanh trạng thái
        rvStatusBar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        statusAdapter = new StatusOrderAdapter(statusList, position -> {
            viewPager.setCurrentItem(position, true);
            statusAdapter.setSelectedPosition(position);
        });
        rvStatusBar.setAdapter(statusAdapter);

        // Thiết lập ViewPager cho danh sách đơn hàng
        pagerAdapter = new OrderPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        // Đồng bộ ViewPager và thanh trạng thái
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                statusAdapter.setSelectedPosition(position);
                rvStatusBar.scrollToPosition(position);
            }
        });

        viewPager.setCurrentItem(0);
        statusAdapter.setSelectedPosition(0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới tất cả các OrderListFragment khi quay lại
        for (int i = 0; i < statusList.size(); i++) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + i);
            if (fragment instanceof OrderListFragment) {
                ((OrderListFragment) fragment).fetchOrderHistory();
            }
        }
    }
}