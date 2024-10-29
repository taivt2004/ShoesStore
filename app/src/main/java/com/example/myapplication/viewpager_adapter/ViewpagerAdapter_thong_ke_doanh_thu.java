package com.example.myapplication.viewpager_adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.tab_layout_thong_ke_doanh_thu.fragment_thong_ke_doanh_thu_theo_ngay;
import com.example.myapplication.tab_layout_thong_ke_doanh_thu.fragment_thong_ke_doanh_thu_theo_thang;

public class ViewpagerAdapter_thong_ke_doanh_thu extends FragmentStatePagerAdapter {



    public ViewpagerAdapter_thong_ke_doanh_thu(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new fragment_thong_ke_doanh_thu_theo_ngay();

            case 1:
                return new fragment_thong_ke_doanh_thu_theo_thang();

            default: new fragment_thong_ke_doanh_thu_theo_ngay();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";

        switch (position){
            case 0:
                title = "Thống kê theo ngày";
                break;
            case 1:
                title = "Thống kê theo tháng";
                break;

        }


        return title;
    }

}
