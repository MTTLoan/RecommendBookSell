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
        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.ic_search));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.ic_order));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.ic_account));

        // Đặt Fragment mặc định là HomeFragment khi khởi động
        replaceFragment(new HomeFragment());

        // Xử lý sự kiện khi nhấn vào các mục
        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                Fragment fragment = null;

                if (model.getId() == 1) {
                    fragment = new HomeFragment();
                    Log.d("Menu", "Navigating to Home");
                } else if (model.getId() == 2) {
                    fragment = new SearchFragment();
                    Log.d("Menu", "Navigating to Search");
                } else if (model.getId() == 3) {
                    fragment = new OrderHistoryFragment();
                    Log.d("Menu", "Navigating to Orders");
                } else if (model.getId() == 4) {
                    fragment = new InforUserFragment();
                    Log.d("Menu", "Navigating to Account");
                }

                if (fragment != null) {
                    replaceFragment(fragment);
                }
                return null;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}