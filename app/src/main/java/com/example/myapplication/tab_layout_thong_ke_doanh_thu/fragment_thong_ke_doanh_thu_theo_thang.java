package com.example.myapplication.tab_layout_thong_ke_doanh_thu;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class fragment_thong_ke_doanh_thu_theo_thang extends Fragment {

    PieChart pieChart;

    Map<String, Float> tk_doanh_thu_theo_thang = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thong_ke_doanh_thu_theo_thang, container, false);
        pieChart = view.findViewById(R.id.bieu_do_tron_thong_ke_doanh_thu_theo_thang);

        // Khởi tạo Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn dữ liệu từ Firestore
        db.collection("QuanLiHoaDon")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Duyệt qua mỗi tài liệu trong kết quả truy vấn
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Date ngayDatHang = document.getDate("ngayDatHang");
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                        String dinhDangThang = sdf.format(ngayDatHang);


                        // Tính doanh thu từ số lượng và giá bán
                        float giaBan = document.getDouble("gia").floatValue();
                        float doanhThu = giaBan;

                        // Tính toán doanh thu và lưu vào Map
                        if (tk_doanh_thu_theo_thang.containsKey(dinhDangThang)) {
                            float doanhThuHienTai = tk_doanh_thu_theo_thang.get(dinhDangThang);
                            tk_doanh_thu_theo_thang.put(dinhDangThang, doanhThuHienTai + doanhThu);
                        } else {
                            tk_doanh_thu_theo_thang.put(dinhDangThang, doanhThu);
                        }
                    }

                    // Sau khi lấy và xử lý dữ liệu, bạn có thể hiển thị trên biểu đồ ở đây
                    hienThi();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi truy vấn dữ liệu
                });

        return view;

    }

    private void hienThi() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Float> entry : tk_doanh_thu_theo_thang.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey())); // Thêm dữ liệu vào danh sách
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(15f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat format = new DecimalFormat("###,###,###");
                return format.format(value) + " VND";
            }
        });

        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false); // Tắt mô tả
        pieChart.setDrawHoleEnabled(true); // Vẽ lỗ ở giữa
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f); // Đường kính của lỗ
        pieChart.setHoleRadius(58f); // Đường kính của lỗ
        pieChart.animateY(1000);
        pieChart.invalidate(); // Refresh biểu đồ
    }
}