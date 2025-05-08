package com.example.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app.R;
import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Menu extends AppCompatActivity {

    protected final int home=1;
    protected final int search=2;
    protected final int orders=3;
    protected final int account=4;

    NafisBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.ic_search));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.ic_order));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.ic_account));

        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                String name = "";

                if (model.getId() == 1) {
                     startActivity(new Intent(Menu.this, HomeActivity.class));
                } else if (model.getId() == 2) {
                     startActivity(new Intent(Menu.this, SearchActivity.class));
                } else if (model.getId() == 3) {
                     startActivity(new Intent(Menu.this, OrderHistoryActivity.class));
                } else if (model.getId() == 4) {
//                     startActivity(new Intent(Menu.this, Inf.class));
                }
                return null;
            }
        });
    }
}
