package com.example.myapplication.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.activity.ChangePassActivity;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment {

    CircleImageView img_add_anhsp;
    LinearLayout lnLogout;
    EditText  edt_email;
    FirebaseFirestore db;

    Button changePassword;
    Uri img_URL;
    StorageReference storageRef;
    EditText edt_nameuser,edt_dia_chi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        img_add_anhsp = view.findViewById(R.id.imgAvt);
        lnLogout = view.findViewById(R.id.lnLogout);
        edt_email = view.findViewById(R.id.edtEmail);
        changePassword = view.findViewById(R.id.btnUpdateProfile);
        edt_nameuser = view.findViewById(R.id.edt_name_user);
        edt_dia_chi = view.findViewById(R.id.edt_dia_chi);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getContext(), ChangePassActivity.class);
                startActivity(i);
            }
        });
        setUserInfomation();
        //showUserInfomation();
        Logout();

        getNameUser();
        getDiaChi();


        return view;

    }





    private void setUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        edt_email.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.logoshop).into(img_add_anhsp);
    }


    private void getNameUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userEmail = firebaseAuth.getCurrentUser().getEmail(); // Lấy địa chỉ email của người dùng đang đăng nhập

        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Lấy thông tin của "nameuser" từ tài liệu
                                String userName = document.getString("nameuser");
                                // Hiển thị thông tin "nameuser"
                                edt_nameuser.setText(userName);
                            }
                        }
                    }
                });
    }
    private void getDiaChi() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userEmail = firebaseAuth.getCurrentUser().getEmail(); // Lấy địa chỉ email của người dùng đang đăng nhập

        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Lấy thông tin của "diachi" từ tài liệu
                                String diachi = document.getString("address");
                                // Hiển thị thông tin "diachi"
                                edt_dia_chi.setText(diachi);
                            }
                        }
                    }
                });
    }
    public void Logout(){
        lnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });
    }



//    // Phương thức để lưu URL của hình ảnh vào Firestore
//    private void luuAnhVaoFirestore(String imageUrl) {
//        // Lấy email của người dùng hiện tại
//        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//
//        // Kiểm tra xem email có tồn tại không
//        if (userEmail != null) {
//            // Tạo một tài liệu mới trong Firestore
//            Map<String, Object> product = new HashMap<>();
//            product.put("img_URL", imageUrl); // Lưu URL của hình ảnh vào Firestore
//
//            // Lưu thông tin sản phẩm vào Firestore với ID là email của người dùng
//            db = FirebaseFirestore.getInstance();
//            db.collection("Users").document(userEmail).collection("Avatar")
//                    .add(product) // Thêm tài liệu mới và tự động tạo ID cho nó
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            // Thêm sản phẩm thành công
//                            Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
//                            // Đóng activity hoặc làm bất kỳ điều gì bạn muốn ở đây
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Xử lý khi thêm sản phẩm thất bại
//                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            // Xử lý khi không thể lấy email của người dùng
//            Toast.makeText(getContext(), "Không thể lấy email của người dùng", Toast.LENGTH_SHORT).show();
//        }
//    }

}