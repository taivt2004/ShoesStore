package com.example.myapplication.model;

public class Feedback {
    String ten_khach_hang;
    String sdt_khach_hang;
    String email_khach_hang;
    String binhluan;


    public Feedback(String ten_khach_hang, String sdt_khach_hang, String email_khach_hang, String binhluan) {
        this.ten_khach_hang = ten_khach_hang;
        this.sdt_khach_hang = sdt_khach_hang;
        this.email_khach_hang = email_khach_hang;
        this.binhluan = binhluan;
    }

    public Feedback() {
    }

    public String getTen_khach_hang() {
        return ten_khach_hang;
    }

    public void setTen_khach_hang(String ten_khach_hang) {
        this.ten_khach_hang = ten_khach_hang;
    }

    public String getSdt_khach_hang() {
        return sdt_khach_hang;
    }

    public void setSdt_khach_hang(String sdt_khach_hang) {
        this.sdt_khach_hang = sdt_khach_hang;
    }

    public String getEmail_khach_hang() {
        return email_khach_hang;
    }

    public void setEmail_khach_hang(String email_khach_hang) {
        this.email_khach_hang = email_khach_hang;
    }

    public String getBinhluan() {
        return binhluan;
    }

    public void setBinhluan(String binhluan) {
        this.binhluan = binhluan;
    }
}

