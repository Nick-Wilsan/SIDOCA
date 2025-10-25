package com.sidoca.Models.DataBaseClass;

public class Organisasi {
    private int id_organisasi;
    private int id_akun;
    private String nama_organisasi;
    private String deskripsi_organisasi;

    // Getters and Setters
    public int getId_organisasi() {
        return id_organisasi;
    }

    public void setId_organisasi(int id_organisasi) {
        this.id_organisasi = id_organisasi;
    }

    public int getId_akun() {
        return id_akun;
    }

    public void setId_akun(int id_akun) {
        this.id_akun = id_akun;
    }

    public String getNama_organisasi() {
        return nama_organisasi;
    }

    public void setNama_organisasi(String nama_organisasi) {
        this.nama_organisasi = nama_organisasi;
    }

    public String getDeskripsi_organisasi() {
        return deskripsi_organisasi;
    }

    public void setDeskripsi_organisasi(String deskripsi_organisasi) {
        this.deskripsi_organisasi = deskripsi_organisasi;
    }
}