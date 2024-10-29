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
import com.example.myapplication.fragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText txtRePasswordSignUp, txtEmail, txtPass, edtNameUser,edtAddressUser;
    private Button btnSignUp;

    private TextView txtSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtEmail = findViewById(R.id.edtEmailAddressSignUp);
        txtPass = findViewById(R.id.edtPasswordSignUp);
        txtRePasswordSignUp = findViewById(R.id.edtRePasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
        edtNameUser = findViewById(R.id.edtNameUser);
        edtAddressUser = findViewById(R.id.edt_dia_chi_dang_ki);



        //chuyển sang màn hình đăng nhập
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        //nút đăng kí
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();

            }
        });


    }




    //hàm lấy dữ liệu từ ô edit text và đẩy lên realtime
    private void onClickSignUp() {

        String email = txtEmail.getText().toString().trim();
        String password = txtPass.getText().toString().trim();
        String repass = txtRePasswordSignUp.getText().toString().trim();
        String nameuser = edtNameUser.getText().toString().trim();
        String diaChi = edtAddressUser.getText().toString().trim();

        // Kiểm tra các ô nhập không được để trống
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repass)) {
            Toast.makeText(SignUpActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email hợp lệ
       if(diaChi == ""){
           edtAddressUser.setError("Địa chỉ không hợp lệ!");
       }
        // Kiểm tra định dạng email hợp lệ
        if (!isValidEmail(email)) {
            txtEmail.setError("Email không hợp lệ");
            return;
        }

        // Kiểm tra mật khẩu không chứa ký tự đặc biệt và có ít nhất 6 ký tự
        if (!isValidPassword(password)) {
            txtPass.setError("Mật khẩu phải có ít nhất 6 ký tự và không chứa ký tự đặc biệt");
            return;
        }

        // Kiểm tra mật khẩu nhập lại khớp với mật khẩu ban đầu
        if (!password.equals(repass)) {
            txtRePasswordSignUp.setError("Mật khẩu nhập lại không khớp");
            return;
        }
        FirebaseAuth mauth =FirebaseAuth.getInstance();
        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                // Lưu thông tin người dùng vào Firestore
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("nameuser", nameuser);
                                userData.put("email", user.getEmail());
                                userData.put("pass", password);
                                userData.put("address", diaChi);
                                userData.put("idUser", userId);
                                userData.put("role", 1);
                                // Thêm các thông tin khác của người dùng nếu cần
                                FirebaseFirestore.getInstance().collection("Users").document(email)
                                        .set(userData);

                            }
                            Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    // Kiểm tra định dạng email hợp lệ
    private boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // Kiểm tra mật khẩu có ít nhất 6 ký tự và không chứa ký tự đặc biệt
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches("[A-Za-z0-9]+");
    }
}