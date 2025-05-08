package com.example.app.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;

import com.example.app.activities.CartActivity;
import com.example.app.activities.InforUserActivity;
import com.example.app.activities.NotificationActivity;
import com.example.app.R;

public class HeaderController {

    public static void setupHeader(Activity activity) {
        ImageView cartIcon = activity.findViewById(R.id.cartIcon);
        ImageView bellIcon = activity.findViewById(R.id.notificationIcon);

        if (cartIcon != null) {
            cartIcon.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, CartActivity.class));
            });
        }

        if (bellIcon != null) {
            bellIcon.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, NotificationActivity.class));
            });
        }
    }
}

