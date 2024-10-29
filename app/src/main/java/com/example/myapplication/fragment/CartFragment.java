package com.example.myapplication.fragment;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.ThanhToanActivity;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.ChiTietHoaDon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CartFragment extends Fragment {
    FrameLayout cart;

    RecyclerView rcv_cart;
    // Phương thức để thêm một mục vào giỏ hàng
    private List<CartItem> cartItems = new ArrayList<>();

    CartAdapter cartAdapter;
    Context c;
    Button btnCheckOut;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        rcv_cart = view.findViewById(R.id.rcv_cart);
        btnCheckOut = view.findViewById(R.id.btnThanhToan);

        // Khởi tạo một GridLayoutManager với số cột là 2 cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //Đặt GridLayoutManager cho RecyclerView để hiển thị dữ liệu sản phẩm theo dạng lưới.
        rcv_cart.setLayoutManager(linearLayoutManager);
        cartAdapter = new CartAdapter(getActivity(), cartItems);
        // Gắn Adapter vào RecyclerView
        rcv_cart.setAdapter(cartAdapter);
        //nút thanh toán
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedItem = cartAdapter.getSelectedItem();
                if (selectedItem != -1) {
                    // Nếu có sản phẩm được chọn, gửi danh sách sản phẩm đó sang activity thanh toán
                    ArrayList<CartItem> selectedProducts = new ArrayList<>();
                    for (int i = 0; i < cartItems.size(); i++) {
                        if (cartItems.get(i).isChecked()) {
                            selectedProducts.add(cartItems.get(i));
                        }
                    }
                    Intent intent = new Intent(getContext(), ThanhToanActivity.class);
                    intent.putExtra("sanPhamDuocChon", selectedProducts);
                    Log.d("Gửi dữ liệu", "Số lượng sản phẩm được gửi: " + selectedProducts.size());
                    startActivity(intent);
                } else {
                    // Nếu không có sản phẩm nào được chọn, hiển thị thông báo lỗi
                    Toast.makeText(getContext(), "Vui lòng chọn sản phẩm trước khi thanh toán", Toast.LENGTH_SHORT).show();
                }
                }
        });




            layDuLieuTuFireStore();

            return view;


        }

 

    private void thanhToanBangMomo() {

    }


    public void layDuLieuTuFireStore() {
        // Truy vấn dữ liệu từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("CartUsers").document(userEmail).collection("CartProduct")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Đọc dữ liệu từ Firestore và thêm vào danh sách cartItems
                                CartItem cartItem = document.toObject(CartItem.class);
                                cartItems.add(cartItem);

                                // Cập nhật tổng tiền lên TextView trên giao diện
                            }
                            // Thông báo cho Adapter rằng dữ liệu đã thay đổi và cần cập nhật

                            cartAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }
    // Phương thức kiểm tra số điện thoại có đúng định dạng không
    private boolean validateSDT(String phoneNumber) {
        // Bạn có thể thay đổi điều kiện kiểm tra phù hợp với định dạng số điện thoại của quốc gia bạn đang làm việc
        String phonePattern = "^[+]?[0-9]{10,13}$"; // Ví dụ: Định dạng cho số điện thoại Việt Nam
        return phoneNumber.matches(phonePattern);
    }
    private void getDiaChi() {

    }
}