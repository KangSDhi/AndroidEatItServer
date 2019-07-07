package com.example.androideatitserver.Model;

public class ModelPengguna {
    private String Nama_Depan;
    private String Nama_Belakang;
    private String Sandi;
    private String Telepon;
    private String AdalahStaff;

    public ModelPengguna() {
    }

    public ModelPengguna(String nama_Depan, String nama_Belakang, String sandi, String telepon, String adalahStaff) {
        Nama_Depan = nama_Depan;
        Nama_Belakang = nama_Belakang;
        Sandi = sandi;
        Telepon = telepon;
        AdalahStaff = adalahStaff;
    }

    public String getNama_Depan() {
        return Nama_Depan;
    }

    public void setNama_Depan(String nama_Depan) {
        Nama_Depan = nama_Depan;
    }

    public String getNama_Belakang() {
        return Nama_Belakang;
    }

    public void setNama_Belakang(String nama_Belakang) {
        Nama_Belakang = nama_Belakang;
    }

    public String getSandi() {
        return Sandi;
    }

    public void setSandi(String sandi) {
        Sandi = sandi;
    }

    public String getTelepon() {
        return Telepon;
    }

    public void setTelepon(String telepon) {
        Telepon = telepon;
    }

    public String getAdalahStaff() {
        return AdalahStaff;
    }

    public void setAdalahStaff(String adalahStaff) {
        AdalahStaff = adalahStaff;
    }
}
