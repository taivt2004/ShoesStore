package com.example.myapplication.fragmentAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.viewpager_adapter.ViewpagerAdapter_quan_li_san_pham;
import com.example.myapplication.viewpager_adapter.ViewpagerAdapter_thong_ke_doanh_thu;
import com.google.android.material.tabs.TabLayout;


public class fragment_thong_ke_doanh_thu extends Fragment {
    ViewPager mViewPager;
    TabLayout mTablayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thong_ke_doanh_thu, container, false);

        mViewPager = view.findViewById(R.id.view_pager_thong_ke_doanh_thu);
        mTablayout = view.findViewById(R.id.tab_layout_thong_ke_doanh_thu);

        ViewpagerAdapter_thong_ke_doanh_thu viewpagerAdapterThongKeDoanhThu = new ViewpagerAdapter_thong_ke_doanh_thu(getActivity().getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        mViewPager.setAdapter(viewpagerAdapterThongKeDoanhThu);

        mTablayout.setupWithViewPager(mViewPager);

        return view;
    }
}