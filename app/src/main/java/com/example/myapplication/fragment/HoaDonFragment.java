package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.HoaDonAdapter;
import com.example.myapplication.adapter.HomeProductAdapter;
import com.example.myapplication.model.ChiTietHoaDon;
import com.example.myapplication.model.HomeProducts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HoaDonFragment extends Fragment {

    RecyclerView recyclerView ;
    HoaDonAdapter hoaDonAdapter;
    List<ChiTietHoaDon> ds_cthd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.historyorder_fragment, container, false);

        recyclerView = view.findViewById(R.id.rcv_products_hoadon);

        // Khởi tạo một GridLayoutManager với số cột là 2 cho RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1);
        //Đặt GridLayoutManager cho RecyclerView để hiển thị dữ liệu sản phẩm theo dạng lưới.
        recyclerView.setLayoutManager(gridLayoutManager);

        //Khởi tạo danh sách các sản phẩm và adapter để
        // hiển thị dữ liệu sản phẩm trong RecyclerView.
        ds_cthd = new ArrayList<>();



        hoaDonAdapter = new HoaDonAdapter(getActivity(),ds_cthd);


        recyclerView.setAdapter(hoaDonAdapter);

        getData();



        return view;
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("HoaDon").document(userEmail).collection("ChiTietHoaDon") //Lấy dữ liệu từ Firestore collection có tên "HomeProduct".
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) { //Trong onComplete(), nếu việc lấy dữ liệu thành công, mỗi tài liệu Firestore được chuyển đổi thành đối tượng HomeProducts và thêm vào danh sách homeProductList.
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ChiTietHoaDon cthd = document.toObject(ChiTietHoaDon.class);
                                Date time =  document.getDate("ngayDatHang");
                                cthd.setNgayDatHang(time);
                                ds_cthd.add(cthd);
                                //cập nhập lại recyclerview
                                hoaDonAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Lỗi " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}