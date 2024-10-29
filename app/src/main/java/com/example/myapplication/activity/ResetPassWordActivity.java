package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassWordActivity extends AppCompatActivity {


    EditText edt_email;
    Button btn_back, btn_resetpass;
    ProgressBar forgetPasswordProgressbar;
    private FirebaseAuth mAuth;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_word);

        edt_email = findViewById(R.id.edt_dia_chi_email);
        btn_back = findViewById(R.id.btnForgotPasswordBack);
        btn_resetpass = findViewById(R.id.btnReset);
        forgetPasswordProgressbar = findViewById(R.id.forgetPasswordProgressbar);


        mAuth = FirebaseAuth.getInstance();

        btn_resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edt_email.getText().toString().trim();
                if(!TextUtils.isEmpty(email)){
                    ResetPass();
                }else{
                    edt_email.setError("Email không hợp lệ!");
                }
            }
        });






        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void ResetPass() {
        forgetPasswordProgressbar.setVisibility(View.VISIBLE);
        btn_resetpass.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ResetPassWordActivity.this, "Kiểm tra đường dẫn trong email của bạn!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ResetPassWordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResetPassWordActivity.this, "Lỗi" + e.getMessage(), Toast.LENGTH_SHORT).show();
                forgetPasswordProgressbar.setVisibility(View.INVISIBLE);
                btn_resetpass.setVisibility(View.VISIBLE);
            }
        });
    }
}