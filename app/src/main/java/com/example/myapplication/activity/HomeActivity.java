package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.myapplication.R;
import com.example.myapplication.fragment.CartFragment;
import com.example.myapplication.fragment.HoaDonFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.fragment.FeedbackFragment;
import com.example.myapplication.fragment.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    FrameLayout frameLayout1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        frameLayout1 = findViewById(R.id.frameLayout);


        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if(item.getItemId() == R.id.navigation_home){
                    fragment = new HomeFragment();
                } else if(item.getItemId() == R.id.user){
                    fragment = new UserFragment();
                }else if(item.getItemId() == R.id.nv_cart) {
                    fragment = new CartFragment();
                }else if(item.getItemId() == R.id.navigation_favourite){
                    fragment = new HoaDonFragment();
                } else if(item.getItemId() == R.id.navigation_store){
                    fragment = new FeedbackFragment();
                } else {
                    // Handle the case where no navigation item matches the selected item
                }

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, fragment)
                            .commit();
                }
                return true;
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new HomeFragment())
                .commit();
    }
}