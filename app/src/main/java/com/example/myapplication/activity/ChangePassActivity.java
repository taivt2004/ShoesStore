package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {
    TextInputLayout tipOldPass, tipNewPass;
    TextInputEditText edtOldPass, edtNewPass;
    Button btnSubmit;
    ImageView btn_back_to_user_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        edtOldPass = findViewById(R.id.edtOldPass);
        edtNewPass = findViewById(R.id.edtNewPass);
        tipOldPass = findViewById(R.id.tipOldPass);
        tipNewPass = findViewById(R.id.tipNewPass);
        btnSubmit = findViewById(R.id.btnSubmit);
        btn_back_to_user_fragment = findViewById(R.id.back_to_user_fragment);

        btn_back_to_user_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePass();
            }
        });

    }

    private void ChangePass() {
        String oldPass = edtOldPass.getText().toString();
        String newPass = edtNewPass.getText().toString();
        boolean isValid = true;
        //        if (oldPass.isEmpty() || newPass.isEmpty()) {
        //            Toast.makeText(ChangePassActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        //            return;
        //        }
        if (oldPass.isEmpty()) {
            tipOldPass.setError("Nhập mật khẩu cũ");
            isValid = false;
        } else {
            tipOldPass.setError(null);
        }
        if (newPass.isEmpty()) {
            tipNewPass.setError("Nhập mật khẩu mới");
            isValid = false;
        } else {
            tipNewPass.setError(null);
        }

        if (isValid) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), oldPass))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePassActivity.this, "Mật khẩu đã được thay đổi, bạn có thể đăng nhập lại để kiểm tra!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangePassActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(ChangePassActivity.this, "Vui lòng nhập đúng mật khẩu cũ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }

    }
}