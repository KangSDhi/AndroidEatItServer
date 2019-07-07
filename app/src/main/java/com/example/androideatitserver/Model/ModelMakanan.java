package com.example.androideatitserver.Model;

public class ModelMakanan {

    private String Nama;
    private String MenuId;
    private String HargaNormal;
    private String HargaDiskon;
    private String Gambar;
    private String Deskripsi;
    private String Diskon;

    public ModelMakanan() {
    }

    public ModelMakanan(String nama, String menuId, String hargaNormal, String hargaDiskon, String gambar, String deskripsi, String diskon) {
        Nama = nama;
        MenuId = menuId;
        HargaNormal = hargaNormal;
        HargaDiskon = hargaDiskon;
        Gambar = gambar;
        Deskripsi = deskripsi;
        Diskon = diskon;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }



    public String getGambar() {
        return Gambar;
    }

    public void setGambar(String gambar) {
        Gambar = gambar;
    }

    public String getDeskripsi() {
        return Deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        Deskripsi = deskripsi;
    }

    public String getDiskon() {
        return Diskon;
    }

    public void setDiskon(String diskon) {
        Diskon = diskon;
    }

    public String getHargaNormal() {
        return HargaNormal;
    }

    public void setHargaNormal(String hargaNormal) {
        HargaNormal = hargaNormal;
    }

    public String getHargaDiskon() {
        return HargaDiskon;
    }

    public void setHargaDiskon(String hargaDiskon) {
        HargaDiskon = hargaDiskon;
    }
}
