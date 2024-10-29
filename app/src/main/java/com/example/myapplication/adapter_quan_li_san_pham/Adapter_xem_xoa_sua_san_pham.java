package com.example.myapplication.adapter_quan_li_san_pham;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activity.DetailActivity;
import com.example.myapplication.adapter.HomeProductAdapter;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.HomeProducts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_xem_xoa_sua_san_pham extends RecyclerView.Adapter<Adapter_xem_xoa_sua_san_pham.ViewHolder> {
    private Context c;
    private List<HomeProducts> list;




    public Adapter_xem_xoa_sua_san_pham(Context c, List<HomeProducts> list) {
        this.c = c;
        this.list = list;
    }



    @NonNull
    @Override
    public Adapter_xem_xoa_sua_san_pham.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Adapter_xem_xoa_sua_san_pham.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quan_li_san_pham_admin, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_xem_xoa_sua_san_pham.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HomeProducts product = list.get(position);

        Glide.with(c).load(product.getImg_URL()).into(holder.imgView_sp);
        holder.tv_ten_sp.setText(product.getName());
        holder.tv_gia_sp.setText(formatCurrency(list.get(position).getPrice())); // Hiển thị giá theo định dạng tiền Việt Nam
        holder.tv_thong_tin_sp.setText(product.getDescription());


        holder.on_long_click_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Hiển thị tooltip khi người dùng rê chuột vào phần tử
                dialog_chinh_sua_sanpham();
                return false;
            }

            private void dialog_chinh_sua_sanpham() {
                Dialog dialog = new Dialog(c);
                dialog.setContentView(R.layout.dialog_xoa_sua_sanpham);


                Button btn_Huy,btn_xoa_sanpham, btn_sua_thong_tin;
                EditText edt_ten, edt_mota, edt_gia;
                btn_Huy = dialog.findViewById(R.id.btn_Huy);
                btn_xoa_sanpham = dialog.findViewById(R.id.btn_Xoa_sanpham);
                btn_sua_thong_tin = dialog.findViewById(R.id.btn_Sua_san_pham);
                edt_ten = dialog.findViewById(R.id.edt_ten_sp);
                edt_mota = dialog.findViewById(R.id.edt_mo_ta);
                edt_gia = dialog.findViewById(R.id.edt_gia_san_pham);


                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                }

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                //nut huy
                btn_Huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                String documentId1 = list.get(position).getId(); // list là danh sách các đối tượng HomeProducts, và getId() là phương thức để lấy ID của một đối tượng trong danh sách


                btn_sua_thong_tin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lấy thông tin mới từ các trường nhập liệu
                        String newName = edt_ten.getText().toString().trim();
                        String newPriceStr = edt_gia.getText().toString().trim();
                        String newDescription = edt_mota.getText().toString().trim();

                        // Xác định các trường dữ liệu mà người dùng đã nhập
                        boolean hasNewName = !newName.isEmpty();
                        boolean hasNewPrice = !newPriceStr.isEmpty();
                        boolean hasNewDescription = !newDescription.isEmpty();

                        // Kiểm tra xem có ít nhất một trường nhập liệu được nhập không
                        if (!hasNewName && !hasNewPrice && !hasNewDescription) {
                            // Hiển thị thông báo yêu cầu người dùng nhập ít nhất một trường
                            Toast.makeText(c, "Vui lòng nhập ít nhất một trường", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        // Nếu có ít nhất một trường nhập liệu được nhập, tiếp tục cập nhật dữ liệu
                        int position = holder.getAdapterPosition(); // Lấy vị trí của ViewHolder trong RecyclerView
                        if (position != RecyclerView.NO_POSITION) {
                            // Tham chiếu đến Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            // Xác định ID của tài liệu cần cập nhật
                            db.collection("HomeProduct")
                                    .whereEqualTo("id", documentId1)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                // Tạo một HashMap để chứa các trường dữ liệu mới
                                                Map<String, Object> duLieuMoi = new HashMap<>();
                                                // Nếu người dùng đã nhập tên mới, thêm vào HashMap
                                                if (hasNewName) duLieuMoi.put("name", newName);
                                                // Nếu người dùng đã nhập giá mới, thêm vào HashMap
                                                if (hasNewPrice) duLieuMoi.put("price", Double.parseDouble(newPriceStr));
                                                // Nếu người dùng đã nhập mô tả mới, thêm vào HashMap
                                                if (hasNewDescription) duLieuMoi.put("description", newDescription);

                                                // Thực hiện cập nhật dữ liệu vào Firestore
                                                document.getReference().update(duLieuMoi)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Cập nhật giao diện và thông báo thành công
                                                                // Cập nhật dữ liệu trong danh sách sản phẩm
                                                                if (hasNewName) list.get(position).setName(newName);
                                                                if (hasNewPrice) list.get(position).setPrice(Double.parseDouble(newPriceStr));
                                                                if (hasNewDescription) list.get(position).setDescription(newDescription);
                                                                // Thông báo cho RecyclerView biết dữ liệu đã thay đổi
                                                                notifyItemChanged(position);
                                                                // Hiển thị thông báo thành công
                                                                Toast.makeText(c, "Sửa thành công", Toast.LENGTH_SHORT).show();
                                                                // Đóng dialog
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Xử lý khi cập nhật thất bại
                                                                Toast.makeText(c, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý khi không tìm thấy tài liệu có trường id tương ứng
                                            Toast.makeText(c, "Không tìm thấy tài liệu để cập nhật", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });


                //xoa san pham
                btn_xoa_sanpham.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition(); // Lấy vị trí của ViewHolder trong RecyclerView

                        if (position != RecyclerView.NO_POSITION) {
                            // Lấy ID của mục được nhấn


                            // Tham chiếu đến Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            // Xác định ID của tài liệu cần lấy
                            // Xóa tài liệu từ Firestore
                            db.collection("HomeProduct") // Thay "your_collection_name" bằng tên của collection chứa tài liệu bạn muốn xóa
                                    .whereEqualTo("id",documentId1) // Truy cập trực tiếp đến tài liệu cần xóa bằng ID
                                    .get() // Xóa tài liệu
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                document.getReference().delete(); // Xóa tài liệu
                                                Toast.makeText(c, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                                list.remove(position); // Xóa mục khỏi danh sách



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
                        }
                    }
                });








            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgView_sp;
        RelativeLayout on_long_click_item;
        TextView tv_ten_sp, tv_gia_sp, tv_thong_tin_sp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            c = itemView.getContext();

            tv_ten_sp = itemView.findViewById(R.id.txtTen_sanpham);
            tv_gia_sp = itemView.findViewById(R.id.txtGia_sanpham);
            tv_thong_tin_sp = itemView.findViewById(R.id.txtThong_tin_san_pham);
            imgView_sp = itemView.findViewById(R.id.ivImage_sanpham);
            on_long_click_item = itemView.findViewById(R.id.on_long_click_item);



        }
    }
    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }
}

