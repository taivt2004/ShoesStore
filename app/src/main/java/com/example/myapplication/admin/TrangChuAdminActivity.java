package com.example.myapplication.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.fragmentAdmin.Fragment_quan_li_hoa_don;
import com.example.myapplication.fragmentAdmin.fragment_thong_ke;
import com.example.myapplication.fragmentAdmin.fragment_thong_ke_doanh_thu;
import com.example.myapplication.fragmentAdmin.Fragment_quan_li_san_pham;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TrangChuAdminActivity extends AppCompatActivity {
    Context c=this;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu_admin);

        //ánh xạ
        drawerLayout=findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView=findViewById(R.id.navigationView);

        //menu
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout2,new Fragment_quan_li_san_pham()).commit();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = new Fragment_quan_li_san_pham();

                if(item.getItemId() == R.id.nv_qlsp){
                    fragment = new Fragment_quan_li_san_pham();
                } else if (item.getItemId() == R.id.nv_qlhd) {
                    fragment = new Fragment_quan_li_hoa_don();
                }else if(item.getItemId() == R.id.nv_qlsl){
                    fragment = new fragment_thong_ke();
                }
                else if(item.getItemId() == R.id.log_out){
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(TrangChuAdminActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                    fragment = new fragment_thong_ke_doanh_thu();

                //đổi title
                getSupportActionBar().setTitle(item.getTitle());

                //nhúng fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout2,fragment).commit();
                //tắc drawer khi nhan xong
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home là icon 3 que (tên mặc định của hệ thống)
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}