package com.example.myapplication.tab_layout_quan_li_hoa_don;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter_quan_li_hoa_don.Adapter_hoa_don_chua_thanh_toan;
import com.example.myapplication.adapter_quan_li_hoa_don.Adapter_hoa_don_da_thanh_toan;
import com.example.myapplication.model.ChiTietHoaDon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class fragment_hoa_don_da_thanh_toan extends Fragment {

    RecyclerView rcv_hoa_don_da_thanh_toan;
    Adapter_hoa_don_da_thanh_toan adapter_hoa_don_da_thanh_toan;
    List<ChiTietHoaDon> ds_cthd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hoa_don_da_thanh_toan, container, false);

        rcv_hoa_don_da_thanh_toan = view.findViewById(R.id.rcv_hoa_don_da_thanh_toan);



        // Khởi tạo một GridLayoutManager với số cột là 2 cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        //Đặt GridLayoutManager cho RecyclerView để hiển thị dữ liệu sản phẩm theo dạng lưới.
        rcv_hoa_don_da_thanh_toan.setLayoutManager(linearLayoutManager);
        ds_cthd = new ArrayList<>();

        adapter_hoa_don_da_thanh_toan = new Adapter_hoa_don_da_thanh_toan(getActivity(),ds_cthd);


        rcv_hoa_don_da_thanh_toan.setAdapter(adapter_hoa_don_da_thanh_toan);

        getHoaDon();

        return view;
    }

    private void getHoaDon() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("QuanLiHoaDon") //Lấy dữ liệu từ Firestore collection có tên "HomeProduct".
                .whereEqualTo("trangThai","Đã thanh toán")
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
                                adapter_hoa_don_da_thanh_toan.notifyDataSetChanged();
                                Log.d("ChiTietHoaDon", cthd.toString());

                            }
                        } else {
                            Toast.makeText(getActivity(), "Lỗi " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}