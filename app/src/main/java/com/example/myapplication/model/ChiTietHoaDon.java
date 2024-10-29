package com.example.myapplication.model;

import java.util.Date;

public class ChiTietHoaDon {

    private String tenSP;
    private double gia;
    private String hoTen;
    private String SDT;
    private String diaChi;
    private String pttt;
    private String idSanPham;
    private String trangThai;
    private String maDonHang;
    private Date ngayDatHang;
    private int soluong;
    String ma_khach_hang;

    public ChiTietHoaDon(String tenSP, double gia, String hoTen, String SDT, String diaChi, String pttt, String idSanPham, String trangThai, String maDonHang, Date ngayDatHang, int soluong, String ma_khach_hang) {
        this.tenSP = tenSP;
        this.gia = gia;
        this.hoTen = hoTen;
        this.SDT = SDT;
        this.diaChi = diaChi;
        this.pttt = pttt;
        this.idSanPham = idSanPham;
        this.trangThai = trangThai;
        this.maDonHang = maDonHang;
        this.ngayDatHang = ngayDatHang;
        this.soluong = soluong;
        this.ma_khach_hang = ma_khach_hang;
    }

    public String getMa_khach_hang() {
        return ma_khach_hang;
    }

    public void setMa_khach_hang(String ma_khach_hang) {
        this.ma_khach_hang = ma_khach_hang;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public ChiTietHoaDon(String tenSP, double gia, String size, String hoTen, String SDT, String diaChi, String pttt, String idSanPham, String trangThai, String maDonHang, Date ngayDatHang, int soluong) {
        this.tenSP = tenSP;
        this.gia = gia;
        this.hoTen = hoTen;
        this.SDT = SDT;
        this.diaChi = diaChi;
        this.pttt = pttt;
        this.idSanPham = idSanPham;
        this.trangThai = trangThai;
        this.maDonHang = maDonHang;
        this.ngayDatHang = ngayDatHang;
        this.soluong = soluong;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    public ChiTietHoaDon() {
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public Date getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(Date ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

//    public ChiTietHoaDon(String tenSP, String gia, String size, String hoTen, String SDT, String diaChi, String pttt, String trangThai, String maDonHang, Date ngayDatHang) {
//        this.tenSP = tenSP;
//        this.gia = gia;
//        this.size = size;
//        this.hoTen = hoTen;
//        this.SDT = SDT;
//        this.diaChi = diaChi;
//        this.pttt = pttt;
//        this.trangThai = trangThai;
//        this.maDonHang = maDonHang;
//        this.ngayDatHang = ngayDatHang;
//    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public ChiTietHoaDon(String tenSP, double gia, String size, String hoTen, String SDT, String diaChi, String pttt, String trangThai) {
        this.tenSP = tenSP;
        this.gia = gia;
        this.hoTen = hoTen;
        this.SDT = SDT;
        this.diaChi = diaChi;
        this.pttt = pttt;
        this.trangThai = trangThai;
    }

    public ChiTietHoaDon(String sp1, String gia, String size, String voTanTai, String sdt, String diaChi, String tienMat, String chuaThanhToan, String maDonHang, String s) {
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getPttt() {
        return pttt;
    }

    public void setPttt(String pttt) {
        this.pttt = pttt;
    }
}
