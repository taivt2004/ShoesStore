package com.example.myapplication.tab_layout_thong_ke_doanh_thu;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

public class fragment_thong_ke_doanh_thu_theo_ngay extends Fragment {
    BarChart barChart;

    // Khởi tạo Map để lưu số lượng sản phẩm bán được theo ngày
    Map<String, Float> tk_doanh_thu_theo_ngay = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thong_ke_doanh_thu_theo_ngay, container, false);
        barChart = view.findViewById(R.id.bieu_do_thong_ke_doanh_thu_theo_ngay);

        // Khởi tạo Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn dữ liệu từ Firestore
        db.collection("QuanLiHoaDon")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Duyệt qua mỗi tài liệu trong kết quả truy vấn
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Date ngayDatHang = document.getDate("ngayDatHang");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String dinhDangNgay = sdf.format(ngayDatHang);
                        // lấy số lượng sản phẩm từ collection QuanLiHoaDon
                        //int quantity = document.getLong("soluong").intValue();
                        // Tính toán doanh thu và lưu vào Map mới
                        float giaBan = document.getDouble("gia").floatValue();

                        float doanhThu = giaBan;

                        // Tính toán doanh thu và lưu vào Map mới
                        if (tk_doanh_thu_theo_ngay.containsKey(dinhDangNgay)) {
                            float doanhThuHienTai = tk_doanh_thu_theo_ngay.get(dinhDangNgay);
                            tk_doanh_thu_theo_ngay.put(dinhDangNgay, (doanhThuHienTai + doanhThu));
                        } else {
                            tk_doanh_thu_theo_ngay.put(dinhDangNgay, doanhThu);
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
        // Khởi tạo danh sách các BarEntry từ dữ liệu doanh thu
        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Float> entry : tk_doanh_thu_theo_ngay.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue())); // Thêm BarEntry với x là index và y là doanh thu
        }

        // Tạo BarDataSet từ danh sách BarEntry
        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat format = new DecimalFormat("###,###,###");
                return format.format(value) + " VND";
            }
        });

        // Tạo BarData từ BarDataSet
        BarData barData = new BarData(dataSet);

        // Đặt dữ liệu cho biểu đồ
        barChart.setData(barData);

        // Tùy chỉnh các thiết lập của trục Y
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Đặt giá trị tối thiểu là 0
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(35); // Số lượng nhãn trên trục Y
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat format = new DecimalFormat("###,###,###");
                return format.format(value) + " VND";
            }
        });

        // Tùy chỉnh các thiết lập của trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new ArrayList<>(tk_doanh_thu_theo_ngay.keySet())));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1); // Đặt độ chia là 1
        xAxis.setGranularityEnabled(true);
        // Tắt trục Y bên phải
        barChart.getAxisRight().setEnabled(false);
        // Tắt mô tả của biểu đồ
        barChart.getDescription().setEnabled(false);

        // Cập nhật lại biểu đồ
        barChart.invalidate();
    }
}