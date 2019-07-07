package com.example.androideatitserver.Model;

public class ModelKategori {
    private String Nama;
    private String Gambar;

    public ModelKategori() {
    }

    public ModelKategori(String nama, String gambar) {
        Nama = nama;
        Gambar = gambar;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getGambar() {
        return Gambar;
    }

    public void setGambar(String gambar) {
        Gambar = gambar;
    }
}
