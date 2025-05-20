package com.example.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.app.fragments.OrderListFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {
    private final String[] statuses = {"Đang đóng gói", "Chờ giao hàng", "Đã giao", "Trả hàng", "Đã hủy"};

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public androidx.fragment.app.Fragment createFragment(int position) {
        return OrderListFragment.newInstance(statuses[position]);
    }

    @Override
    public int getItemCount() {
        return statuses.length;
    }

    public String getTabTitle(int position) {
        return statuses[position];
    }
}