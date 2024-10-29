package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChiTietHoaDon;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

public class HoaDonAdapter extends RecyclerView.Adapter<HoaDonAdapter.ViewHolder> {


    private List<ChiTietHoaDon> list_cthd;
    private Context c;

    public HoaDonAdapter(Context c, List<ChiTietHoaDon> list_cthd) {
        this.c = c;
        this.list_cthd = list_cthd;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chi_tiet_hoa_don, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChiTietHoaDon chiTietHoaDon = list_cthd.get(position);
        // Kiểm tra xem NgayDatHang có null không trước khi định dạng thành chuỗi
        if (chiTietHoaDon.getNgayDatHang() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngayDatHang = sdf.format(chiTietHoaDon.getNgayDatHang());
            holder.tv_ngay_dat_hang.setText("Ngày đặt hàng: " + ngayDatHang);
        } else {
            holder.tv_ngay_dat_hang.setText("Ngày không xác định");
        }

        holder.tv_ma_don_hang.setText("Mã đơn hàng: " + chiTietHoaDon.getMaDonHang());
        holder.tv_ten_don_hang.setText(chiTietHoaDon.getTenSP());
        holder.tv_ten_khach_hang.setText("Họ tên khách hàng: " + chiTietHoaDon.getHoTen());
        holder.tv_soluong.setText("Tổng số lượng: " + chiTietHoaDon.getSoluong());
        holder.tv_tong_tien.setText("Tổng tiền: " + formatCurrency(chiTietHoaDon.getGia())); // Hiển thị giá theo định dạng tiền Việt Nam
        holder.tv_thanh_toan.setText("Phương thức thanh toán: " + chiTietHoaDon.getPttt());


        holder.ln_cthd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xacNhanThanhToanDialog(chiTietHoaDon);
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Xác nhận xóa hóa đơn");
                builder.setMessage("Bạn có chắc chắn muốn xác nhận huỷ đơn hàng này?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xác nhận thanh toán và cập nhật trạng thái hóa đơn
                        // capNhapTrangThai
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        // Xóa tài liệu từ Firestore
                        db.collection("HoaDon").document(userEmail).collection("ChiTietHoaDon") // Thay "your_collection_name" bằng tên của collection chứa tài liệu bạn muốn xóa
                                .whereEqualTo("maDonHang", chiTietHoaDon.getMaDonHang()) // Truy cập trực tiếp đến tài liệu cần xóa bằng ma don hang
                                .get() // Xóa tài liệu
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            document.getReference().delete(); // Xóa tài liệu
                                            list_cthd.remove(position); // Xóa mục khỏi danh sách

                                            notifyItemRemoved(position); // Cập nhật RecyclerView
                                            dialog.dismiss();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xảy ra lỗi trong quá trình xóa
                                        Toast.makeText(c, "Failed to delete document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                        // Xóa tài liệu từ Firestore
                        db2.collection("QuanLiHoaDon") // Thay "your_collection_name" bằng tên của collection chứa tài liệu bạn muốn xóa
                                .whereEqualTo("maDonHang", chiTietHoaDon.getMaDonHang()) // Truy cập trực tiếp đến tài liệu cần xóa bằng ma don hang
                                .get() // Xóa tài liệu
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            document.getReference().delete(); // Xóa tài liệu
                                            Toast.makeText(c, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                            list_cthd.remove(position); // Xóa mục khỏi danh sách

                                            // notifyItemRemoved(position); // Cập nhật RecyclerView
                                            //  dialog.dismiss();
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xảy ra lỗi trong quá trình xóa
                                        Toast.makeText(c, "Failed to delete document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list_cthd.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_ma_don_hang, tv_ten_don_hang,
                tv_ten_khach_hang, tv_ngay_dat_hang,
                tv_tong_tien, tv_thanh_toan, tv_soluong;
        LinearLayout ln_cthd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ma_don_hang = itemView.findViewById(R.id.tv_ma_don_hang);
            tv_ten_don_hang = itemView.findViewById(R.id.tv_ten_don_hang);
            tv_ten_khach_hang = itemView.findViewById(R.id.tv_ten_khach_hang);
            tv_ngay_dat_hang = itemView.findViewById(R.id.tv_ngay_dat_hang);
            tv_tong_tien = itemView.findViewById(R.id.tv_tong_tien);
            tv_thanh_toan = itemView.findViewById(R.id.tv_thanh_toan);
            tv_soluong = itemView.findViewById(R.id.tv_so_luong_hoa_don);
            ln_cthd = itemView.findViewById(R.id.ln_cthd);
        }

    }

    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }

}
