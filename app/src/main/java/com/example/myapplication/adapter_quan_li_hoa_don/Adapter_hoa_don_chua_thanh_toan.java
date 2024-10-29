package com.example.myapplication.adapter_quan_li_hoa_don;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

public class Adapter_hoa_don_chua_thanh_toan extends RecyclerView.Adapter<Adapter_hoa_don_chua_thanh_toan.ViewHolder>  {


    private List<ChiTietHoaDon> list_cthd;
    private Context c;
    Adapter_hoa_don_chua_thanh_toan adapterHoaDonChuaThanhToan;

    public Adapter_hoa_don_chua_thanh_toan(Context c, List<ChiTietHoaDon> list_cthd) {
        this.c = c;
        this.list_cthd = list_cthd;
        adapterHoaDonChuaThanhToan = this;
    }

    @NonNull
    @Override
    public Adapter_hoa_don_chua_thanh_toan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Adapter_hoa_don_chua_thanh_toan.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quan_li_hoa_don_chua_thanh_toan, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_hoa_don_chua_thanh_toan.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChiTietHoaDon chiTietHoaDon = list_cthd.get(position);
        // Kiểm tra xem NgayDatHang có null không trước khi định dạng thành chuỗi
        if (chiTietHoaDon.getNgayDatHang() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String ngayDatHang = sdf.format(chiTietHoaDon.getNgayDatHang());
            holder.tv_ngay_dat_hang.setText("Ngày đặt hàng: "+ngayDatHang);
        } else {
            holder.tv_ngay_dat_hang.setText("Ngày không xác định");
        }

        holder.tv_ma_don_hang.setText("Mã đơn hàng: "+chiTietHoaDon.getMaDonHang());
        holder.tv_ten_don_hang.setText(chiTietHoaDon.getTenSP());
        holder.tv_ten_khach_hang.setText("Họ tên khách hàng: "+chiTietHoaDon.getHoTen());
        holder.tv_dia_chi_don_hang.setText("Địa chỉ:  "+chiTietHoaDon.getDiaChi());
        holder.tv_sdt_khach_hang.setText("Số điện thoại: "+chiTietHoaDon.getSDT());
        holder.tv_trang_thai.setText("Trạng thái: "+chiTietHoaDon.getTrangThai());
        holder.tv_soluong.setText("Số lượng: "+chiTietHoaDon.getSoluong());
        holder.tv_tong_tien.setText("Tổng tiền: "+formatCurrency(chiTietHoaDon.getGia()));
        holder.tv_thanh_toan.setText("Phương thức thanh toán: "+chiTietHoaDon.getPttt());

        holder.ln_hoa_don_chua_thanh_toan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xacNhanThanhToanDialog(chiTietHoaDon);
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Xác nhận thanh toán");
                builder.setMessage("Bạn có chắc chắn muốn xác nhận thanh toán cho hóa đơn này?");
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xác nhận thanh toán và cập nhật trạng thái hóa đơn
                        // capNhapTrangThai
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("QuanLiHoaDon")
                                .whereEqualTo("idSanPham", chiTietHoaDon.getIdSanPham()) // Lọc theo trường idsanpham
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String docId = documentSnapshot.getId(); // Lấy ID của tài liệu
                                            db.collection("QuanLiHoaDon").document(docId)
                                                    .update("trangThai", "Đã thanh toán")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(c, "Hóa đơn đã được xác nhận thanh toán", Toast.LENGTH_SHORT).show();
                                                            capNhapGiaoDien(chiTietHoaDon);
                                                            notifyItemChanged(position);
                                                            adapterHoaDonChuaThanhToan.notifyItemChanged(position);
                                                            adapterHoaDonChuaThanhToan.notifyDataSetChanged();

                                                            // Tùy chỉnh xử lý sau khi cập nhật trạng thái
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(c, "Xác nhận thanh toán thất bại", Toast.LENGTH_SHORT).show();
                                                            Log.e("Xác nhận thanh toán", e.getMessage());
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(c, "Không tìm thấy hóa đơn trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                                        Log.e("Tìm tài liệu", e.getMessage());
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

    // Hàm cập nhật dữ liệu trực tiếp trong RecyclerView
private void capNhapGiaoDien(ChiTietHoaDon cthd) {
    int index = -1;
    // Tìm index của chiTietHoaDon trong list_cthd
    for (int i = 0; i < list_cthd.size(); i++) {
        if (list_cthd.get(i).getIdSanPham().equals(cthd.getIdSanPham())) {
            index = i;
            break;
        }
    }
    // Nếu tìm thấy index, cập nhật trạng thái và gọi notifyItemChanged
    if (index != -1) {
        cthd.setTrangThai("Đã thanh toán");
        list_cthd.set(index, cthd);
        notifyItemChanged(index);
    }
}

    @Override
    public int getItemCount() {
        return list_cthd.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_ma_don_hang, tv_ten_don_hang,
                tv_size_don_hang,tv_ten_khach_hang,tv_dia_chi_don_hang,
                tv_sdt_khach_hang,tv_ngay_dat_hang,tv_trang_thai,
                tv_tong_tien,tv_thanh_toan, tv_ma_san_pham_admin, tv_soluong;
        private LinearLayout ln_hoa_don_chua_thanh_toan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ma_don_hang = itemView.findViewById(R.id.tv_ma_don_hang_admin);
            tv_ten_don_hang = itemView.findViewById(R.id.tv_ten_don_hang_admin);
            tv_ten_khach_hang = itemView.findViewById(R.id.tv_ten_khach_hang_admin);
            tv_dia_chi_don_hang = itemView.findViewById(R.id.tv_dia_chi_don_hang_admin);
            tv_sdt_khach_hang = itemView.findViewById(R.id.tv_sdt_khach_hang_admin);
            tv_ngay_dat_hang = itemView.findViewById(R.id.tv_ngay_dat_hang_admin);
            tv_trang_thai = itemView.findViewById(R.id.tv_trang_thai_admin);
            tv_tong_tien = itemView.findViewById(R.id.tv_tong_tien_admin);
            tv_thanh_toan = itemView.findViewById(R.id.tv_thanh_toan_admin);
            ln_hoa_don_chua_thanh_toan = itemView.findViewById(R.id.ln_hoa_don_chua_thanh_toan);
            tv_soluong = itemView.findViewById(R.id.tv_so_luong_san_pham_admin_chua_thanh_toan);
        }

    }
    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }
}
