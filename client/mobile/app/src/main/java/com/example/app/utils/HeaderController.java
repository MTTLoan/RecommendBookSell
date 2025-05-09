package com.example.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.app.R;
import com.example.app.activities.CartActivity;
import com.example.app.activities.NotificationActivity;

public class HeaderController {

    // Phương thức cho Activity
    public static void setupHeader(Activity activity) {
        if (activity == null) {
            Log.e("HeaderController", "Activity là null");
            return;
        }

        ImageView cartIcon = activity.findViewById(R.id.cartIcon);
        ImageView bellIcon = activity.findViewById(R.id.notificationIcon);

        setupClickListeners(activity, cartIcon, bellIcon);
    }

    // Phương thức cho Fragment
    public static void setupHeader(View fragmentView, Activity activity) {
        if (fragmentView == null) {
            Log.e("HeaderController", "fragmentView là null");
            return;
        }
        if (activity == null) {
            Log.e("HeaderController", "Activity là null");
            return;
        }

        ImageView cartIcon = fragmentView.findViewById(R.id.cartIcon);
        ImageView bellIcon = fragmentView.findViewById(R.id.notificationIcon);

        setupClickListeners(activity, cartIcon, bellIcon);
    }

    // Phương thức chung để gắn sự kiện click
    private static void setupClickListeners(Activity activity, ImageView cartIcon, ImageView bellIcon) {
        if (cartIcon != null) {
            cartIcon.setEnabled(true);
            cartIcon.setVisibility(View.VISIBLE);
            cartIcon.setOnClickListener(v -> {
                Log.d("HeaderController", "Đã click vào cartIcon");
                activity.startActivity(new Intent(activity, CartActivity.class));
            });
        } else {
            Log.e("HeaderController", "cartIcon không được tìm thấy");
        }

        if (bellIcon != null) {
            bellIcon.setEnabled(true);
            bellIcon.setVisibility(View.VISIBLE);
            bellIcon.setOnClickListener(v -> {
                Log.d("HeaderController", "Đã click vào notificationIcon");
                activity.startActivity(new Intent(activity, NotificationActivity.class));
            });
        } else {
            Log.e("HeaderController", "notificationIcon không được tìm thấy");
        }
    }
}