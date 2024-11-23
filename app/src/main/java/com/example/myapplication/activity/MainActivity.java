package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        },3000);


    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        } else {
            // Kiểm tra xem người dùng có tồn tại trong Firestore và có vai trò là admin hay không
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(user.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                int role = documentSnapshot.getLong("role").intValue();
                                if (role == 1) {
                                    // Người dùng có vai trò 1, chuyển đến màn hình HomeActivity để mua hàng
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Người dùng không có vai trò 1, thông báo là tài khoản không hợp lệ và đăng xuất
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xảy ra lỗi khi truy cập Firestore, đăng xuất người dùng
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this, "Lỗi nè: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}

