package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.fragment.CartFragment;
import com.example.myapplication.model.CartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context c;
    private List<CartItem> mlist;
    FirebaseFirestore db;
    private int selectedItem = -1; // Biến lưu vị trí item được chọn

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    // Constructor để nhận mListener
    public CartAdapter(Context c, List<CartItem> mlist) {
        this.c = c;
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItem item = mlist.get(position);
        // Set data to views in the ViewHolder

        Glide.with(c).load(mlist.get(position).getImg_URL()).into(holder.ivImage);
        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(formatCurrency(mlist.get(position).getProductPrice())); // Hiển thị giá theo định dạng tiền Việt Nam
        holder.txtSize.setText("Size: " +item.getSize());
        holder.tv_soluong.setText(""+ item.getSoluong());



        // Xử lý sự kiện khi người dùng thay đổi trạng thái của checkbox
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked); // Cập nhật trạng thái của checkbox vào đối tượng CartItem
                setSelectedItem(position);
            }
        });

        // Set giá trị mặc định cho số lượng là 1

        holder.btn_tang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soluong = item.getSoluong();
                soluong++;
                item.setSoluong(soluong);
                holder.tv_soluong.setText("" + soluong); // Cập nhật số lượng ngay sau khi tăng
            }
        });


        holder.btn_giam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soluong = item.getSoluong();
                if(soluong>1){
                    soluong--;
                    item.setSoluong(soluong);
                    holder.tv_soluong.setText("" + soluong); // Cập nhật số lượng ngay sau khi giảm

                }
            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition(); // Lấy vị trí của ViewHolder trong RecyclerView
                //Kiểm tra xem vị trí có hợp lệ không. Nếu vị trí không tồn tại trong danh sách, không thực hiện bất kỳ thao tác nào.
                if (position != RecyclerView.NO_POSITION) {
                    // Lấy ID của mục được nhấn
                    String itemId = mlist.get(position).getId();

                    // Tham chiếu đến Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    // Thực hiện xóa mục theo ID trong CartProduct
                    db.collection("CartUsers").document(userEmail).collection("CartProduct")
                            .whereEqualTo("id", itemId) // Chỉ định điều kiện xóa
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        document.getReference().delete(); // Xóa tài liệu
                                    }
                                    //Toast.makeText(c, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                    mlist.remove(position); // Xóa mục khỏi danh sách


                                    notifyItemRemoved(position); // Cập nhật RecyclerView
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Xảy ra lỗi trong quá trình xóa
                                    Toast.makeText(c, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


    }

    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvPrice, txtSize,tv_soluong;
        ImageView ivImage, btnDelete,btn_tang,btn_giam;
        CardView cartView;
        CheckBox checkbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imageViewProduct);
            tvName = itemView.findViewById(R.id.txtProductName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvPrice = itemView.findViewById(R.id.txtProductPrice);
            txtSize = itemView.findViewById(R.id.txtSize);
            cartView = itemView.findViewById(R.id.cartView);
            checkbox = itemView.findViewById(R.id.checkbox);

            tv_soluong = itemView.findViewById(R.id.tv_so_luong);
            btn_tang = itemView.findViewById(R.id.btn_tang);
            btn_giam = itemView.findViewById(R.id.btn_giam);

        }
    }


}

