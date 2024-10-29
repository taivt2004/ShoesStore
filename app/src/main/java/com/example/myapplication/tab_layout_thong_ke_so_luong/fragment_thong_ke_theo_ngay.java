package com.example.myapplication.tab_layout_thong_ke_so_luong;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class fragment_thong_ke_theo_ngay extends Fragment {


    BarChart barChart;

    // Khởi tạo Map để lưu số lượng sản phẩm bán được theo ngày
    Map<String, Integer> tk_theo_ngay = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_quan_li_nguoi_dung, container, false);
         barChart = view.findViewById(R.id.bieu_do_thong_ke);

        // Khởi tạo Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn dữ liệu từ Firestore
        db.collection("QuanLiHoaDon")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Duyệt qua mỗi tài liệu trong kết quả truy vấn
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Lấy ngày đặt hàng từ dữ liệu và chuyển định dạng sang ngày/tháng/năm
                        Date ngayDatHang = document.getDate("ngayDatHang");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String dinhDangNgay = sdf.format(ngayDatHang);

                        // lấy số lượng sản phẩm từ collection QuanLiHoaDon
                        int quantity = document.getLong("soluong").intValue();

                        // Kiểm tra xem ngày này đã được thêm vào Map chưa
                        if (tk_theo_ngay.containsKey(dinhDangNgay)) {
                            // Nếu đã có, lấy số lượng hiện tại và cộng thêm số lượng mới
                            int currentQuantity = tk_theo_ngay.get(dinhDangNgay);
                            tk_theo_ngay.put(dinhDangNgay, currentQuantity + quantity);
                        } else {
                            // Nếu chưa có, thêm ngày vào Map với số lượng sản phẩm là số lượng đã bán
                            tk_theo_ngay.put(dinhDangNgay, quantity);
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
        barChart.getAxisRight().setDrawLabels(false);

        // Khởi tạo danh sách các BarEntry từ dữ liệu salesByDay
        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : tk_theo_ngay.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue())); // Thêm BarEntry với x là index và y là số lượng sản phẩm bán được
        }

        // Tạo BarDataSet từ danh sách BarEntry
        BarDataSet dataSet = new BarDataSet(entries, "Số lượng đơn hàng được đặt");
        dataSet.setValueTextSize(15f);

        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Định dạng giá trị thành số nguyên và trả về dưới dạng chuỗi
            }
        });

        // Tạo BarData từ BarDataSet
        BarData barData = new BarData(dataSet);

        // Đặt dữ liệu cho biểu đồ
        barChart.setData(barData);

        // Tùy chỉnh các thiết lập của trục Y
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Đặt giá trị tối thiểu là 0
        yAxis.setAxisMaximum(20f); // Đặt giá trị tối đa là 20
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(20);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                    return String.valueOf((int) value); // Nếu giá trị là số nguyên, trả về số nguyên
            }
        });

        // Tùy chỉnh các thiết lập của trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new ArrayList<>(tk_theo_ngay.keySet())));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1); // Đặt độ chia là 1
        xAxis.setGranularityEnabled(true);

        // Tắt mô tả của biểu đồ
        barChart.getDescription().setEnabled(false);

        // Cập nhật lại biểu đồ
        barChart.invalidate();
    }
}