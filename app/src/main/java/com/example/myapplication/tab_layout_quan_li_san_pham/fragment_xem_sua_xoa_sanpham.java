package com.example.myapplication.tab_layout_quan_li_san_pham;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.HomeProductAdapter;
import com.example.myapplication.adapter_quan_li_san_pham.Adapter_xem_xoa_sua_san_pham;
import com.example.myapplication.model.HomeProducts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragment_xem_sua_xoa_sanpham extends Fragment {

    RecyclerView rcv_xem_xoa_sua_sanpham;
    List<HomeProducts> List;
    Adapter_xem_xoa_sua_san_pham adapterXemXoaSuaSanPham;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_xem_sua_xoa_sanpham, container, false);

        rcv_xem_xoa_sua_sanpham = view.findViewById(R.id.rcv_xoa_sua_sp);


        // Khởi tạo một GridLayoutManager với số cột là 2 cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        //Đặt GridLayoutManager cho RecyclerView để hiển thị dữ liệu sản phẩm theo dạng lưới.
        rcv_xem_xoa_sua_sanpham.setLayoutManager(linearLayoutManager);

        //Khởi tạo danh sách các sản phẩm và adapter để
        // hiển thị dữ liệu sản phẩm trong RecyclerView.
        List = new ArrayList<>();

        adapterXemXoaSuaSanPham = new Adapter_xem_xoa_sua_san_pham(getActivity(),List);


        rcv_xem_xoa_sua_sanpham.setAdapter(adapterXemXoaSuaSanPham);


        getData();



        return view;


    }

    private void getData() {
            db = FirebaseFirestore.getInstance();
            db.collection("HomeProduct") //Lấy dữ liệu từ Firestore collection có tên "HomeProduct".
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) { //Trong onComplete(), nếu việc lấy dữ liệu thành công, mỗi tài liệu Firestore được chuyển đổi thành đối tượng HomeProducts và thêm vào danh sách homeProductList.
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                    List.add(homeProduct);
                                    //cập nhập lại recyclerview
                                    adapterXemXoaSuaSanPham.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Lỗi " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }
}