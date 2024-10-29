package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;

import java.util.List;

public class ThanhToanAdapter extends RecyclerView.Adapter<ThanhToanAdapter.ViewHolder> {
    private List<CartItem> products;
    private Context context;

    public ThanhToanAdapter(Context context, List<CartItem> products) {
        this.context = context;
        this.products = products;
    }

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem product = products.get(position);

        // Set thông tin sản phẩm cho ViewHolder
        holder.tv_ten_sp.setText("Tên: "+ product.getProductName());
        holder.tv_gia_sp.setText("Giá: "+formatCurrency(product.getProductPrice())); // Hiển thị giá theo định dạng tiền Việt Nam
        holder.tv_soluong.setText(String.valueOf("X"+product.getSoluong()));
        holder.tv_size.setText(String.valueOf("Size: "+product.getSize()));
        Glide.with(context).load(product.getImg_URL()).into(holder.imageViewProduct_thanh_toan);

    // Load hình ảnh sản phẩm từ URL (sử dụng thư viện Picasso hoặc Glide)
        // Ví dụ:
        // Picasso.get().load(product.getImg_URL()).into(holder.imageViewProduct);
        }

@Override
public int getItemCount() {
        return products.size();
        }

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tv_ten_sp;
    TextView tv_gia_sp;
    TextView tv_soluong, tv_size;
    ImageView imageViewProduct_thanh_toan;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_ten_sp = itemView.findViewById(R.id.txt_ProductName);
        tv_gia_sp = itemView.findViewById(R.id.txt_Product_Price);
        tv_soluong = itemView.findViewById(R.id.txt_So_luong);
        tv_size = itemView.findViewById(R.id.txt_Size);
        imageViewProduct_thanh_toan = itemView.findViewById(R.id.imageViewProduct_thanh_toan);

    }
}
    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }
}
