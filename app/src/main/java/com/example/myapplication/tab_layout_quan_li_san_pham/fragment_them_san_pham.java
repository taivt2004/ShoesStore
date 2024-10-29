package com.example.myapplication.tab_layout_quan_li_san_pham;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class fragment_them_san_pham extends Fragment {
    EditText edt_add_tensp, edt_add_mota, edt_add_gia;
    ImageView img_add_anhsp;
    Button btn_them_sp;
    Uri img_URL;


    FirebaseFirestore db;
    StorageReference storageRef;
    FirebaseStorage firebaseStorage;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
        if(result.getResultCode() == RESULT_OK){
            if (result.getData() != null) {
                img_add_anhsp.setEnabled(true);
                img_URL = result.getData().getData();
                Glide.with(requireContext()).load(img_URL).into(img_add_anhsp);
            }
        }else {
            Toast.makeText(requireContext(), "Bạn đã không chọn ảnh nào", Toast.LENGTH_SHORT).show();
        }
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_them_san_pham, container, false);

        edt_add_tensp = view.findViewById(R.id.edt_them_ten_sp);
        edt_add_mota = view.findViewById(R.id.edt_them_mo_ta);
        edt_add_gia = view.findViewById(R.id.edt_them_gia_san_pham);
        img_add_anhsp = view.findViewById(R.id.img_View_them_anh);
        btn_them_sp = view.findViewById(R.id.btn_them_san_pham);


        img_add_anhsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                i.setAction(i.ACTION_GET_CONTENT);
                activityResultLauncher.launch(i);
            }
        });




        btn_them_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem img_URL có null hay không
                if (img_URL == null) {
                    Toast.makeText(getContext(), "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                    return; // Thoát khỏi phương thức nếu img_URL là null
                }
                db = FirebaseFirestore.getInstance();
                storageRef = FirebaseStorage.getInstance().getReference();
                // Tải hình ảnh lên Firebase Storage
                if (img_URL != null) {
                StorageReference imageRef = storageRef.child("Yasuo/" + UUID.randomUUID().toString());
                imageRef.putFile(img_URL)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Lấy URL của hình ảnh đã tải lên
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = uri.toString();
                                        // Lưu URL hình ảnh vào Firestore hoặc sử dụng nó theo nhu cầu của bạn
                                        luuAnhVaoFirestore(imageURL);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý khi tải hình ảnh lên thất bại
                                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(requireContext(), "Vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });





        return view;
    }



// Phương thức để lưu URL của hình ảnh vào Firestore
        private void luuAnhVaoFirestore(String imageUrl) {
            // Lấy thông tin từ các EditText
            String ten = edt_add_tensp.getText().toString().trim();
            String moTa = edt_add_mota.getText().toString().trim();
            String newPriceStr = edt_add_gia.getText().toString().trim();
            double gia = 0; // Mặc định giá bằng 0 (bạn có thể thay đổi logic của mình tại đây)

            // Kiểm tra xem các trường có được nhập đầy đủ không
            if (!TextUtils.isEmpty(newPriceStr)) {
                // Chuyển đổi giá trị thành kiểu double
                try {
                    gia = Double.parseDouble(newPriceStr);
                } catch (NumberFormatException e) {
                    // Xử lý ngoại lệ khi không thể chuyển đổi chuỗi thành double
                    Toast.makeText(getContext(), "Giá sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                    return; // Thoát khỏi phương thức nếu giá trị không hợp lệ
                }
            } else {
                // Xử lý khi giá trị là rỗng
                Toast.makeText(getContext(), "Vui lòng nhập giá sản phẩm", Toast.LENGTH_SHORT).show();
                return; // Thoát khỏi phương thức để không thực hiện lưu dữ liệu vào Firestore
            }

            // Tạo một ID mới cho sản phẩm
            String idSanPham = UUID.randomUUID().toString();

            // Tạo một tài liệu mới trong Firestore
            Map<String, Object> product = new HashMap<>();
            product.put("name", ten);
            product.put("description", moTa);
            product.put("price", gia); // Lưu giá dưới dạng kiểu number
            product.put("img_URL", imageUrl); // Lưu URL của hình ảnh vào Firestore

            // Lưu thông tin sản phẩm vào Firestore
            db.collection("HomeProduct")
                    .add(product) // Thêm tài liệu mới và tự động tạo ID cho nó
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Lấy ID của tài liệu mới được thêm vào
                            String docId = documentReference.getId();
                            // Thêm ID vào dữ liệu của sản phẩm trước khi lưu vào Firestore
                            product.put("id", docId);
                            // Lưu thông tin sản phẩm vào Firestore với trường ID
                            db.collection("HomeProduct").document(docId).set(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Thêm sản phẩm thành công
                                            Toast.makeText(getContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                            // Đóng activity hoặc làm bất kỳ điều gì bạn muốn ở đây
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý khi thêm sản phẩm thất bại
                                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý khi không thể thêm tài liệu mới
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

}