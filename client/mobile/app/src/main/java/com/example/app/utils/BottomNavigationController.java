package com.example.app.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.app.R;
import com.example.app.activities.HomeActivity;
import com.example.app.activities.InforUserActivity;
import com.example.app.activities.OrderHistoryActivity;
import com.example.app.activities.SearchActivity;
import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class BottomNavigationController {

    public static void setupBottomNavigation(Activity activity) {
        NafisBottomNavigation bottomNavigation = activity.findViewById(R.id.bottomNavigation);

        if (bottomNavigation != null) {
            // Thêm các mục vào bottom navigation
            bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.ic_home));
            bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.ic_search));
            bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.ic_order));
            bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.ic_account));

            // Xử lý sự kiện khi nhấn vào các mục
            bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
                @Override
                public Unit invoke(NafisBottomNavigation.Model model) {
                    Intent intent = null;

                    if (model.getId() == 1) {
                        if (!(activity instanceof HomeActivity)) {
                            intent = new Intent(activity, HomeActivity.class);
                        }
                    } else if (model.getId() == 2) {
                        if (!(activity instanceof SearchActivity)) {
                            intent = new Intent(activity, SearchActivity.class);
                        }
                    } else if (model.getId() == 3) {
                        if (!(activity instanceof OrderHistoryActivity)) {
                            intent = new Intent(activity, OrderHistoryActivity.class);
                        }
                    } else if (model.getId() == 4) {
                        if (!(activity instanceof InforUserActivity)) {
                            intent = new Intent(activity, InforUserActivity.class);
                        }
                    }

                    if (intent != null) {
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    return null;
                }
            });

            // Tự động chuyển tới HomeActivity khi khởi động nếu không phải HomeActivity
            if (!(activity instanceof HomeActivity)) {
                Intent intent = new Intent(activity, HomeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }
}