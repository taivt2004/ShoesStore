package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activity.DetailActivity;
import com.example.myapplication.model.HomeProducts;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {
    private Context c;
    private List<HomeProducts> list;




    public HomeProductAdapter(Context c, List<HomeProducts> list) {
        this.c = c;
        this.list = list;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        HomeProducts product = list.get(position);
        Glide.with(c).load(list.get(position).getImg_URL()).into(holder.img);
        holder.tv_Name.setText(list.get(position).getName());
        holder.tv_Gia.setText(formatCurrency(list.get(position).getPrice())); // Hiển thị giá theo định dạng tiền Việt Nam






        holder.click_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển sang DetailActivity và truyền dữ liệu sản phẩm sang detail activity
                Intent intent = new Intent(c, DetailActivity.class);
                intent.putExtra("productName", product.getName());
                intent.putExtra("productPrice", product.getPrice());
                intent.putExtra("productDescription", product.getDescription());
                intent.putExtra("imageUrl", product.getImg_URL());
                intent.putExtra("id", product.getId());
                intent.putExtra("soluong", product.getSoluong());
                c.startActivity(intent);
            }
        });
    }
    // Phương thức để định dạng giá tiền sang định dạng tiền Việt Nam
    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img,addToCartImage, imgFavourite, ivImage,addToCartImage2;
        TextView tv_Name, tv_Gia;
        RelativeLayout click_RL;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            c = itemView.getContext();
            ivImage = itemView.findViewById(R.id.ivImage);

            tv_Name = itemView.findViewById(R.id.txtName);
            img = itemView.findViewById(R.id.ivImage);
            tv_Gia = itemView.findViewById(R.id.txtGia);

            click_RL = itemView.findViewById(R.id.RL);




        }
    }
}
