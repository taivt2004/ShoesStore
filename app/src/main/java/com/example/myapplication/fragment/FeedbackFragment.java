package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Feedback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedbackFragment extends Fragment {

    EditText edt_ho_ten, edt_sdt, edt_email, edt_binh_luan;
    Button btn_send_feedback;

    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.feedback_fragment, container, false);

        edt_ho_ten = view.findViewById(R.id.edt_ho_va_ten_feedback);
        edt_sdt = view.findViewById(R.id.edt_sdt_feedback);
        edt_email = view.findViewById(R.id.edt_email_feedback);
        edt_binh_luan = view.findViewById(R.id.edt_binh_luan);
        btn_send_feedback = view.findViewById(R.id.btnSend_feedback);



        btn_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback();
            }
        });



        return view;
    }

    private void feedback() {
         String hoten = edt_ho_ten.getText().toString().trim();
         String sdt = edt_sdt.getText().toString().trim();
         String email = edt_email.getText().toString().trim();
         String binhluan = edt_binh_luan.getText().toString().trim();


        // Kiểm tra xem các trường đã được nhập đầy đủ hay không
        if (hoten.isEmpty() || sdt.isEmpty() || email.isEmpty() || binhluan.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra họ tên
        if (!validateTen(hoten)) {
            Toast.makeText(getContext(), "Vui lòng nhập họ tên hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra số điện thoại chỉ chứa số
        if (!validateSDT(sdt)) {
            Toast.makeText(getContext(), "Số điện thoại chỉ được chứa số", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra định dạng email
        if (!validateEmail(email)) {
            Toast.makeText(getContext(), "Vui lòng nhập địa chỉ email hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }


        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (!email.equals(userEmail)) {
            Toast.makeText(getContext(), "Nhập email của bạn", Toast.LENGTH_SHORT).show();
            return;
        }


        db = FirebaseFirestore.getInstance();
        // Kiểm tra xem có các thông tin đã được gửi trước đó không
        db.collection("Feedback").document(email).collection("ChiTietDanhGia")
                .whereEqualTo("ten_khach_hang", hoten)
                .whereEqualTo("sdt_khach_hang", sdt)
                .whereEqualTo("email_khach_hang", email)
                .whereEqualTo("binhluan", binhluan)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Nếu có dữ liệu trùng lặp, hiển thị thông báo cảnh báo
                            Toast.makeText(getContext(), "Thông tin bạn đã gửi, vui lòng nhập nội dung khác", Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu không có dữ liệu trùng lặp, thêm dữ liệu mới vào Firestore
                            Feedback feedback = new Feedback(hoten, sdt, email, binhluan);
                            db.collection("Feedback").document(email).collection("ChiTietDanhGia")
                                    .add(feedback)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getContext(), "Đã gửi ý kiến", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
    // Hàm kiểm tra định dạng email
    private boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Hàm kiểm tra số điện thoại chỉ chứa số
    private boolean validateSDT(String sdt) {
        return android.util.Patterns.PHONE.matcher(sdt).matches();
    }

    // Hàm kiểm tra họ tên
    private boolean validateTen(String name) {
        // Họ tên không được trống và không chứa ký tự đặc biệt, chỉ chấp nhận chữ cái và khoảng trắng
        return !name.isEmpty() && name.matches("[\\p{L}\\s]+");
    }


}