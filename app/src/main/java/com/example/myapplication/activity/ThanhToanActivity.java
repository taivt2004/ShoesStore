package com.example.myapplication.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.adapter.ThanhToanAdapter;
import com.example.myapplication.fragment.HomeFragment;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class ThanhToanActivity extends AppCompatActivity {

    RecyclerView rcv_thanh_toan;
    CartAdapter cartAdapter;
    EditText edt_sdt, edt_dia_chi, edt_ten_nguoi_nhan;
    TextView tv_tong_tien, tv_tong_so_luong, tv_tensp, tv_size;
    Button btn_Thanh_Toan,btn_huy_thanh_toan;
    String ma_nguoi_dung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        rcv_thanh_toan = findViewById(R.id.rcv_thanh_toan);
        edt_sdt = findViewById(R.id.edt_sdt_dat_hang);
        edt_dia_chi = findViewById(R.id.edt_dia_chi_giao_hang);
        edt_ten_nguoi_nhan = findViewById(R.id.edt_ten_nguoi_nhan);
        tv_tong_tien = findViewById(R.id.tv_tong_tien_thanh_toan);
        tv_tong_so_luong = findViewById(R.id.tv_tong_sl_thanh_toan);
        btn_Thanh_Toan = findViewById(R.id.btn_Thanh_Toan);
        btn_huy_thanh_toan = findViewById(R.id.btn_huy_thanh_toan);
        tv_tensp = findViewById(R.id.tv_ten_sp);
      //  tv_size = findViewById(R.id.tv_size);

        // Nhận dữ liệu từ Intent gửi từ Fragment
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<CartItem> selectedProducts = (ArrayList<CartItem>) intent.getSerializableExtra("sanPhamDuocChon");
            // Tạo và thiết lập Adapter
            ThanhToanAdapter adapter = new ThanhToanAdapter(this ,selectedProducts);
            cartAdapter = new CartAdapter(this, selectedProducts);

            // Khởi tạo một GridLayoutManager với số cột là 2 cho RecyclerView
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            //Đặt GridLayoutManager cho RecyclerView để hiển thị dữ liệu sản phẩm theo dạng lưới.
            rcv_thanh_toan.setLayoutManager(linearLayoutManager);
            rcv_thanh_toan.setAdapter(adapter);

            int tongSoluong = 0;
            double TongTien = 0.0;
            String id = "";
            StringBuilder ten_sp = new StringBuilder();
            StringBuilder sizeBuilder = new StringBuilder();
            final StringBuilder ten_sp_size_gia = new StringBuilder();
            for (CartItem sanpham : selectedProducts){
                tongSoluong += sanpham.getSoluong();
                TongTien += sanpham.getProductPrice() * sanpham.getSoluong();
                ten_sp.append(sanpham.getProductName()).append("\n");
                id = sanpham.getId();
                String size = sanpham.getSize(); // Lấy kích thước của từng sản phẩm
                sizeBuilder.append(size).append("\n"); // Thêm kích thước vào StringBuilder
                ten_sp_size_gia.append(sanpham.getProductName())
                        .append(" - Size ").append(size)
                        .append(" - x").append(sanpham.getSoluong())
                        .append("\n");
            }

            tv_tensp.setText(ten_sp_size_gia.toString());
            //tv_size.setText(sizeBuilder.toString()+"\n");
            tv_tong_so_luong.setText("Tổng số lượng: "+ tongSoluong);
            tv_tong_tien.setText("Tổng tiền: " + formatCurrency(TongTien));
            adapter.notifyDataSetChanged();



            //lấy tên người dùng hiển thị lên edt
            FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();
            String userEmail2 = firebaseAuth1.getCurrentUser().getEmail(); // Lấy địa chỉ email của người dùng đang đăng nhập
            FirebaseFirestore db2;
            db2 = FirebaseFirestore.getInstance();
            db2.collection("Users").document(userEmail2)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Lấy thông tin của "nameuser" từ tài liệu
                                    String userName = document.getString("nameuser");
                                    // Hiển thị thông tin "nameuser"
                                    edt_ten_nguoi_nhan.setText(userName);
                                }
                            }
                        }
                    });


            //lấy địa chỉ hiển thị lên edt
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db1;
            String userEmail1 = firebaseAuth.getCurrentUser().getEmail(); // Lấy địa chỉ email của người dùng đang đăng nhập
            db1 = FirebaseFirestore.getInstance();
            db1.collection("Users").document(userEmail1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Lấy thông tin của "diachi" từ tài liệu
                                    String diachi = document.getString("address");
                                    // Hiển thị thông tin "diachi"
                                    edt_dia_chi.setText(diachi);
                                }
                            }
                        }
                    });

            //lấy địa chỉ hiển thị lên edt
            FirebaseAuth firebaseAuth2 = FirebaseAuth.getInstance();
            FirebaseFirestore db3;
            String userEmail3 = firebaseAuth.getCurrentUser().getEmail(); // Lấy địa chỉ email của người dùng đang đăng nhập
            db3 = FirebaseFirestore.getInstance();
            db3.collection("Users").document(userEmail1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Lấy thông tin của "diachi" từ tài liệu
                                    ma_nguoi_dung = document.getString("idUser");
                                }
                            }
                        }
                    });


            final int so_luong_sp = tongSoluong;
            final double finalTongTien = TongTien;
            final String tensp_size = String.valueOf(ten_sp_size_gia);
            final String id_sp = id;
            btn_Thanh_Toan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Kiểm tra thông tin nhập vào
                    String ten_nguoi_dung = edt_ten_nguoi_nhan.getText().toString().trim();
                    String dia_chi = edt_dia_chi.getText().toString().trim();
                    String sdt = edt_sdt.getText().toString().trim();
                    Random random = new Random();
                    // Tạo số nguyên ngẫu nhiên
                    String maHoaDon = String.valueOf(random.nextInt(999999999));


                    if (ten_nguoi_dung.isEmpty() || dia_chi.isEmpty() || sdt.isEmpty()) {
                        // Hiển thị thông báo lỗi nếu thông tin nhập vào không hợp lệ
                        Toast.makeText(ThanhToanActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ChiTietHoaDon cthd = new ChiTietHoaDon();
                    Date ngayDatHang = new Date();
                    // Đặt giá trị ngày và giờ vào đối tượng ChiTietHoaDon
                    cthd.setNgayDatHang(ngayDatHang);

                    cthd.setTenSP(String.valueOf(tensp_size));
                    cthd.setSoluong(so_luong_sp);
                    cthd.setGia(finalTongTien);
                    cthd.setIdSanPham(id_sp);
                    cthd.setHoTen(ten_nguoi_dung);
                    cthd.setDiaChi(dia_chi);
                    cthd.setSDT(sdt);
                    cthd.setMaDonHang(maHoaDon);
                    cthd.setNgayDatHang(ngayDatHang);
                    cthd.setPttt("Thanh toán khi nhận hàng");
                    cthd.setTrangThai("Chưa thanh toán");


                    // Đẩy đối tượng ChiTietHoaDon lên Firebase Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    db.collection("HoaDon").document(userEmail).collection("ChiTietHoaDon")
                            .add(cthd)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    cartAdapter.notifyDataSetChanged();
                                    dialogShowMessage();
                                    Intent i = new Intent(ThanhToanActivity.this, HomeActivity.class);
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ThanhToanActivity.this, "Lỗi khi thêm hóa đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                    ChiTietHoaDon cthd1 = new ChiTietHoaDon();

                    cthd1.setTenSP(String.valueOf(tensp_size));
                    cthd1.setSoluong(so_luong_sp);
                    cthd1.setGia(finalTongTien);
                    cthd1.setIdSanPham(id_sp);
                    cthd1.setHoTen(ten_nguoi_dung);
                    cthd1.setDiaChi(dia_chi);
                    cthd1.setSDT(sdt);
                    cthd1.setMaDonHang(maHoaDon);
                    cthd1.setNgayDatHang(ngayDatHang);
                    cthd1.setPttt("Thanh toán khi nhận hàng");
                    cthd1.setTrangThai("Chưa thanh toán");
                    cthd1.setMa_khach_hang(ma_nguoi_dung);

                    FirebaseFirestore db2 = FirebaseFirestore.getInstance();

                    db2.collection("QuanLiHoaDon")
                            .add(cthd1)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ThanhToanActivity.this, "Lỗi khi thêm hóa đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });



                }
            });





        }
        btn_huy_thanh_toan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void dialogShowMessage() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_message);


        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },2000);


    }

    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }
}