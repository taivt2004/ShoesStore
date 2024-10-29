package com.example.myapplication.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activity.HomeActivity;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginAdminActivity extends AppCompatActivity {

    EditText TaiKhoan, Matkhau;
    ImageView back_to_login;
    Button btnLoginAdmin;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        TaiKhoan = findViewById(R.id.edtTaiKhoan);
        Matkhau = findViewById(R.id.edtMatKhau);
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
        back_to_login = findViewById(R.id.back_to_login);
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginwithAdmin();

            }
        });



    }

    private void taotkAdmin() {

    }

    private void loginwithAdmin() {

        // Người dùng đã đăng nhập, tiếp tục kiểm tra thông tin admin
        String email = TaiKhoan.getText().toString().trim();
        String matkhau = Matkhau.getText().toString().trim();

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email,matkhau)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, kiểm tra vai trò của người dùng
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Kiểm tra vai trò của người dùng từ Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Admin").document(user.getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    int role = documentSnapshot.getLong("role").intValue();
                                                    if (role == 0) {
                                                        // Người dùng có vai trò 1, chuyển đến màn hình HomeActivity để mua hàng
                                                        Intent i = new Intent(LoginAdminActivity.this, TrangChuAdminActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        // Người dùng không có vai trò 1, thông báo là tài khoản không hợp lệ
                                                        Toast.makeText(LoginAdminActivity.this, "Tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    // Không tìm thấy thông tin người dùng trong Firestore
                                                    Toast.makeText(LoginAdminActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xảy ra lỗi khi truy cập Firestore
                                                Toast.makeText(LoginAdminActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Đăng nhập không thành công, hiển thị thông báo lỗi
                            Toast.makeText(LoginAdminActivity.this, "Đăng nhập không thành công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}