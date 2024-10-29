package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.myapplication.R;
import com.example.myapplication.activity.DetailActivity;
import com.example.myapplication.adapter.HomeProductAdapter;
import com.example.myapplication.adapter.SearchAdapter;
import com.example.myapplication.model.HomeProducts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView rcvProducts, rcv_search;
    HomeProductAdapter homeProductAdapter;
    List<HomeProducts> homeProductList;
    List<HomeProducts> filterList;
    FirebaseFirestore db;
    SearchAdapter searchAdapter;

    TextView txtNameUser, textView, txtTitle, txtnikeBrand, txtVansBrand, txtShowAllProduct, txtMLBBrand,txtbrandAdidas, txtbrandReebok;
    ImageView ivCart;
    ImageSlider imageSlider;

    androidx.appcompat.widget.SearchView svSearch;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtNameUser = view.findViewById(R.id.txtNameUser);
        rcvProducts = view.findViewById(R.id.rcv_products);
        textView = view.findViewById(R.id.txtNameUser);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtnikeBrand = view.findViewById(R.id.nikeBrand);
        txtVansBrand = view.findViewById(R.id.txtVansBrand);
        txtShowAllProduct = view.findViewById(R.id.txtShowAll);
        txtMLBBrand = view.findViewById(R.id.brandMLB);
        txtbrandAdidas = view.findViewById(R.id.txtbrandAdidas);
        txtbrandReebok = view.findViewById(R.id.txtbrandReebok);
        svSearch = view.findViewById(R.id.svSearch);
        rcv_search = view.findViewById(R.id.rcv_search);
        imageSlider = view.findViewById(R.id.image_slide);

        //slider
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.imageslide1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.imageslide2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.imageslide3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.imageslide4, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);




        db = FirebaseFirestore.getInstance();

        // Khởi tạo một GridLayoutManager với số cột là 2 cho RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        //Đặt GridLayoutManager cho RecyclerView để hiển thị dữ liệu sản phẩm theo dạng lưới.
        rcvProducts.setLayoutManager(gridLayoutManager);

        //Khởi tạo danh sách các sản phẩm và adapter để
        // hiển thị dữ liệu sản phẩm trong RecyclerView.
        homeProductList = new ArrayList<>();

        homeProductAdapter = new HomeProductAdapter(getActivity(),homeProductList);


        rcvProducts.setAdapter(homeProductAdapter);

        //lọc sản phẩm theo hãng
        locSanPhamAdidas();
        locSanPhamNike();
        locSanPhamVans();
        locSanPhamMLB();
        locSanPhamReebok();
        showAllProduct();
        getDataFromFireStore();

        getNameUser();



        //search
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getActivity(),2);
        rcv_search.setLayoutManager(gridLayoutManager2);
        filterList = new ArrayList<>();
        searchAdapter = new SearchAdapter(getActivity(),filterList);
        rcv_search.setAdapter(searchAdapter);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    // Hiển thị lại danh sách sản phẩm chính và ẩn đi kết quả tìm kiếm
                    rcvProducts.setVisibility(View.VISIBLE);
                    rcv_search.setVisibility(View.GONE);
                }else {
                    // Nếu có nhập từ khóa tìm kiếm, ẩn danh sách sản phẩm chính và hiển thị kết quả tìm kiếm
                    rcvProducts.setVisibility(View.GONE);
                    rcv_search.setVisibility(View.VISIBLE);
                    // Gọi phương thức để thực hiện tìm kiếm
                    searchFB(newText);
                }
                return true;
            }
        });


        return view;
    }

    private void searchFB(String keyword) {
        db.collection("HomeProduct")
                .whereGreaterThanOrEqualTo("brand", keyword)
                .whereLessThanOrEqualTo("brand", keyword + "\uf8ff") // Sử dụng một ký tự kết thúc của unicode để tìm kiếm gần đúng
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            filterList.clear(); // Xóa dữ liệu hiện tại của filterList
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                filterList.add(homeProduct); // Thêm sản phẩm phù hợp vào filterList
                            }
                            // Cập nhật Adapter của RecyclerView mới với kết quả tìm kiếm từ Firebase
                            searchAdapter.notifyDataSetChanged();
                        } else {
                            // Xử lý trường hợp không thành công nếu cần
                        }
                    }
                });
    }

    private void getNameUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userEmail = firebaseAuth.getCurrentUser().getEmail(); // Lấy địa chỉ email của người dùng đang đăng nhập

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
                                txtNameUser.setText(userName);
                            }
                        }
                    }
                });
    }



    private void getDataFromFireStore() {
        db.collection("HomeProduct") //Lấy dữ liệu từ Firestore collection có tên "HomeProduct".
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Trong onComplete(), nếu việc lấy dữ liệu thành công, mỗi tài liệu Firestore được chuyển đổi thành đối tượng HomeProducts và thêm vào danh sách homeProductList.
                                HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                homeProductList.add(homeProduct);
                                //cập nhập lại recyclerview
                                homeProductAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Lỗi " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void locSanPhamReebok() {
        txtbrandReebok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("HomeProduct")
                        .whereEqualTo("brand", "Reebok")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    homeProductList.clear(); // Xóa danh sách sản phẩm hiện tại
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                        homeProductList.add(homeProduct);
                                    }
                                    homeProductAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void locSanPhamAdidas() {
        txtbrandAdidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("HomeProduct")
                        .whereEqualTo("brand", "Adidas")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    homeProductList.clear(); // Xóa danh sách sản phẩm hiện tại
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                        homeProductList.add(homeProduct);
                                    }
                                    homeProductAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void locSanPhamMLB() {
        txtMLBBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("HomeProduct")
                        .whereEqualTo("brand", "MLB")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    homeProductList.clear(); // Xóa danh sách sản phẩm hiện tại
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                        homeProductList.add(homeProduct);
                                    }
                                    homeProductAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void showAllProduct() {
        txtShowAllProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("HomeProduct")
                        .whereEqualTo("search", "viewall")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    homeProductList.clear(); // Xóa danh sách sản phẩm hiện tại
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                        homeProductList.add(homeProduct);
                                    }
                                    homeProductAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                }
                            }
                        });
            }
        });
    }

    private void locSanPhamVans() {
        txtVansBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("HomeProduct")
                        .whereEqualTo("brand", "Vans")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    homeProductList.clear(); // Xóa danh sách sản phẩm hiện tại
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                        homeProductList.add(homeProduct);
                                    }
                                    homeProductAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void locSanPhamNike() {
        txtnikeBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("HomeProduct")
                        .whereEqualTo("brand", "Nike")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    homeProductList.clear(); // Xóa danh sách sản phẩm hiện tại
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        HomeProducts homeProduct = document.toObject(HomeProducts.class);
                                        homeProductList.add(homeProduct);
                                    }
                                    homeProductAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                                } else {
                                    Toast.makeText(getActivity(), "Lỗi" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
};
