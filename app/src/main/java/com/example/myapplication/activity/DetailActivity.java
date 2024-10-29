package com.example.myapplication.activity;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.fragment.CartFragment;
import com.example.myapplication.fragment.HomeFragment;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.HomeProducts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    ImageView ivImage, ivBack;
    TextView tv_Name_product, txtPrice, txtDescription;
    TextView[] sizeTextViews;
    TextView selectedSizeTextView;
    FirebaseFirestore db;
    Button btnAddToCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ivImage = findViewById(R.id.ivImage);
        tv_Name_product = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        ivBack = findViewById(R.id.ivBack);

        // Khởi tạo danh sách TextViews
        sizeTextViews = new TextView[]{
                findViewById(R.id.tv_Size36),
                findViewById(R.id.tv_Size37),
                findViewById(R.id.tv_Size38),
                findViewById(R.id.tv_Size39),
                findViewById(R.id.tv_Size40),
                findViewById(R.id.tv_Size41)
        };

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Nhận dữ liệu của sản phẩm HomeProduct từ intent
        Intent intent = getIntent();


        String productName = intent.getStringExtra("productName");
        Double productPrice = intent.getDoubleExtra("productPrice", 0.0);
        String productDescription = intent.getStringExtra("productDescription");
        String imageUrl = intent.getStringExtra("imageUrl");
        String id = intent.getStringExtra("id");
        int soluong = intent.getIntExtra("soluong", 0);


        // Hiển thị thông tin chi tiết của sản phẩm trên giao diện
        tv_Name_product.setText(productName);
        txtPrice.setText((formatCurrency(productPrice)));
        txtDescription.setText(productDescription);

        Glide.with(this)
                .load(imageUrl)
                .into(ivImage);

        // Xử lý sự kiện click cho mỗi TextView size giày
        for (final TextView textView : sizeTextViews) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Nếu đã có một TextView được chọn trước đó
                    if (selectedSizeTextView != null) {
                        // Đặt màu nền của textview trước đó
                        selectedSizeTextView.setBackgroundResource(R.drawable.circle_background);
                    }
                    // Thiết lập màu nền của textview hiện tại
                    textView.setBackgroundResource(R.drawable.circle_bg2);
                    // Lưu trữ TextView được chọn vào biến selectedSizeTextView
                    selectedSizeTextView = textView;
                }
            });
        }


        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSizeTextView == null) {
                    Toast.makeText(DetailActivity.this, "Vui lòng chọn size giày", Toast.LENGTH_SHORT).show();
                    return; // Kết thúc phương thức onClick
                }
                // Lấy giá trị size giày đã chọn
                String selectedSize = selectedSizeTextView.getText().toString();


                // Truy vấn tất cả các sản phẩm đã có trong giỏ hàng
                db = FirebaseFirestore.getInstance();
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                db.collection("CartUsers").document(userEmail).collection("CartProduct")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Lấy danh sách các sản phẩm đã có trong giỏ hàng
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Lấy thông tin của sản phẩm từ Firestore
                                        String ProductName = document.getString("productName");
                                        Double ProductPrice = document.getDouble("productPrice");
                                        String ProductImageUrl = document.getString("img_URL");

                                        // So sánh thông tin của sản phẩm đã có với thông tin của sản phẩm mới
                                        if (ProductName.equals(productName)) {
                                            // Hiển thị thông báo cho người dùng rằng sản phẩm đã có trong giỏ hàng
                                            Toast.makeText(DetailActivity.this, "Sản phẩm đã có trong giỏ hàng", Toast.LENGTH_SHORT).show();
                                            return; // Dừng vòng lặp và kết thúc phương thức onComplete
                                        }
                                    }

                                    // Nếu không tìm thấy sản phẩm có thông tin giống với sản phẩm mới, thêm sản phẩm mới vào giỏ hàng
                                    Map<String, Object> cartItem = new HashMap<>();
                                    cartItem.put("productName", productName);
                                    cartItem.put("productPrice", productPrice);
                                    cartItem.put("img_URL", imageUrl);
                                    cartItem.put("id", id);
                                    cartItem.put("size", selectedSize);
                                    cartItem.put("soluong", soluong);

                                    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();


                                    // Thêm tài liệu mới vào collection CartProduct
                                    db.collection("CartUsers").document(userEmail).collection("CartProduct")
                                            .add(cartItem)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // Thông báo cho người dùng rằng sản phẩm đã được thêm vào giỏ hàng thành công
                                                    Toast.makeText(DetailActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }
                        });
            }
        });


    }

    private String formatCurrency(double price) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("#,###,###,###");
        return format.format(price) + " VND";
    }


}