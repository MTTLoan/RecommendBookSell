package com.example.app.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app.R;
import com.example.app.fragments.HomeFragment;
import com.example.app.fragments.InforUserFragment;
import com.example.app.fragments.OrderHistoryFragment;
import com.example.app.fragments.SearchFragment;
import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Menu extends AppCompatActivity {

    protected final int home = 1;
    protected final int search = 2;
    protected final int orders = 3;
    protected final int account = 4;

    NafisBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Thêm các mục vào bottom navigation
        bottomNavigation.add(new NafisBottomNavigation.Model(home, R.drawable.ic_home));
        bottomNavigation.add(new NafisBottomNavigation.Model(search, R.drawable.ic_search));
        bottomNavigation.add(new NafisBottomNavigation.Model(orders, R.drawable.ic_order));
        bottomNavigation.add(new NafisBottomNavigation.Model(account, R.drawable.ic_account));

        // Kiểm tra Intent để hiển thị Fragment tương ứng
        String fragmentToShow = getIntent().getStringExtra("fragment_to_show");
        Fragment initialFragment;
        int selectedNavItem;

        if ("orders".equals(fragmentToShow)) {
            initialFragment = new OrderHistoryFragment();
            selectedNavItem = orders;
            Log.d("Menu", "Showing OrderHistoryFragment from Intent");
        } else if ("home".equals(fragmentToShow)) {
            initialFragment = new HomeFragment();
            selectedNavItem = home;
            Log.d("Menu", "Showing HomeFragment from Intent");
        } else {
            // Mặc định hiển thị HomeFragment và chọn nút Home
            initialFragment = new HomeFragment();
            selectedNavItem = home;
            Log.d("Menu", "Showing default HomeFragment");
        }

        // Hiển thị Fragment ban đầu và chọn mục tương ứng trong bottom navigation
        replaceFragment(initialFragment);
        bottomNavigation.show(selectedNavItem, false);

        // Xử lý sự kiện khi nhấn vào các mục
        bottomNavigation.setOnClickMenuListener(model -> {
            Fragment fragment = null;
            String logMessage = "";

            if (model.getId() == home) {
                fragment = new HomeFragment();
                logMessage = "Navigating to Home";
            } else if (model.getId() == search) {
                fragment = new SearchFragment();
                logMessage = "Navigating to Search";
            } else if (model.getId() == orders) {
                fragment = new OrderHistoryFragment();
                logMessage = "Navigating to Orders";
            } else if (model.getId() == account) {
                fragment = new InforUserFragment();
                logMessage = "Navigating to Account";
            }

            if (fragment != null) {
                replaceFragment(fragment);
                Log.d("Menu", logMessage);
            }
            return null;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}