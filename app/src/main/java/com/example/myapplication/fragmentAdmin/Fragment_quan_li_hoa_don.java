package com.example.myapplication.fragmentAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.viewpager_adapter.ViewpagerAdapter_quan_li_hoa_don;
import com.example.myapplication.viewpager_adapter.ViewpagerAdapter_quan_li_san_pham;
import com.google.android.material.tabs.TabLayout;

public class Fragment_quan_li_hoa_don extends Fragment {


    ViewPager mViewPager;
    TabLayout mTablayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quan_li_hoa_don, container, false);


        mViewPager = view.findViewById(R.id.view_pager_quan_li_hoa_don);
        mTablayout = view.findViewById(R.id.tab_layout_quan_li_hoa_don);

        ViewpagerAdapter_quan_li_hoa_don viewpagerAdapterQuanLiHoaDon = new ViewpagerAdapter_quan_li_hoa_don(getActivity().getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        mViewPager.setAdapter(viewpagerAdapterQuanLiHoaDon);

        mTablayout.setupWithViewPager(mViewPager);

        return view;
    }
}