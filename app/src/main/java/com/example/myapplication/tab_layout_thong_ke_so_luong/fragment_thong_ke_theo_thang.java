package com.example.myapplication.tab_layout_thong_ke_so_luong;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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


public class fragment_thong_ke_theo_thang extends Fragment {

    BarChart barChart;
    PieChart pieChart;

    // Khởi tạo Map để lưu số lượng sản phẩm bán được theo ngày
    Map<String, Integer> tk_theo_thang = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thong_ke_theo_thang, container, false);
        pieChart = view.findViewById(R.id.bieu_do_thong_ke_theo_thang);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("QuanLiHoaDon")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Date ngayDatHang = doc.getDate("ngayDatHang");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                        String dinhDangThang = dateFormat.format(ngayDatHang);
                        // Lấy số lượng sản phẩm từ dữ liệu
                        int soluongsp = doc.getLong("soluong").intValue();

                        // Kiểm tra xem tháng này đã được thêm vào Map chưa
                        if(tk_theo_thang.containsKey(dinhDangThang)){
                            // Nếu đã có, lấy số lượng hiện tại và cộng thêm số lượng mới
                            int soluongHienTai = tk_theo_thang.get(dinhDangThang);
                            tk_theo_thang.put(dinhDangThang, soluongHienTai + soluongsp );
                        }else{
                            // Nếu chưa có, thêm tháng vào Map với số lượng sản phẩm là số lượng đã bán

                            tk_theo_thang.put(dinhDangThang, soluongsp);
                        }

                    }
                    hienThi();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Loi" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        return view ;
    }

    private void hienThi() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tk_theo_thang.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey())); // Thêm PieEntry với giá trị là số lượng sản phẩm và nhãn là tháng
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueTextSize(15f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Định dạng giá trị thành số nguyên và trả về dưới dạng chuỗi
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // Cập nhật lại biểu đồ
    }
}