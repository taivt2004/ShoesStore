package com.example.myapplication.viewpager_adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.tab_layout_quan_li_san_pham.fragment_them_san_pham;
import com.example.myapplication.tab_layout_quan_li_san_pham.fragment_xem_sua_xoa_sanpham;

public class ViewpagerAdapter_quan_li_san_pham extends FragmentStatePagerAdapter {



    public ViewpagerAdapter_quan_li_san_pham(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new fragment_xem_sua_xoa_sanpham();

            case 1:
                return new fragment_them_san_pham();

            default: new fragment_xem_sua_xoa_sanpham();
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
                title = "Tùy chỉnh sản phẩm";
                break;
            case 1:
                title = "Thêm sản phẩm";
                break;

        }


        return title;
    }
}
