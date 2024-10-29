package com.example.myapplication.activity;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.admin.LoginAdminActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText edtemail, edtpass;
    TextView txtSignUp, tv_quen_mat_khau;
    Button btnLogin, btnLoginGG,btnLoginAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtemail = findViewById(R.id.edtEmailAddress);
        edtpass = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignup);
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
        tv_quen_mat_khau = findViewById(R.id.tv_quen_mat_khau);
        tv_quen_mat_khau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ResetPassWordActivity.class);
                startActivity(i);
            }
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithEmailAndPass();
            }
        });
        btnLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, LoginAdminActivity.class);
                startActivity(i);
            }
        });


    }


    private void loginWithEmailAndPass() {
        String email = edtemail.getText().toString().trim();
        String pass = edtpass.getText().toString().trim();



        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            // Hiển thị thông báo lỗi hoặc thực hiện các hành động khác khi người dùng không nhập đủ thông tin
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        //lấy ra một thể hiện của lớp FirebaseAuth, sử dụng nó để xác thực người dùng
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Nếu đăng nhập thành công, kiểm tra vai trò của người dùng thông qua dữ liệu được lưu trữ trong Firestore.
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Kiểm tra vai trò của người dùng từ Firestore
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
                                                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        // Người dùng không có vai trò 1, thông báo là tài khoản không hợp lệ
                                                        Toast.makeText(LoginActivity.this, "Tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    // Không tìm thấy thông tin người dùng trong Firestore
                                                    Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xảy ra lỗi khi truy cập Firestore
                                                Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Đăng nhập không thành công, hiển thị thông báo lỗi
                            Toast.makeText(LoginActivity.this, "Đăng nhập không thành công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}