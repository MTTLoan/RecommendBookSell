package com.example.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.app.fragments.OrderListFragment;

public class OrderListAdapter extends FragmentStateAdapter {
    private final String[] statuses;
    private final OrderListFragment[] fragments;
    private final Runnable onStatusChanged;

    public OrderListAdapter(@NonNull Fragment fragment, String[] statuses, Runnable onStatusChanged) {
        super(fragment);
        this.statuses = statuses;
        this.fragments = new OrderListFragment[statuses.length];
        this.onStatusChanged = onStatusChanged;
        for (int i = 0; i < statuses.length; i++) {
            fragments[i] = OrderListFragment.newInstance(statuses[i]);
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return statuses.length;
    }

    public OrderListFragment getFragment(int position) {
        return fragments[position];
    }
}