package com.sidoca.Models.DataBaseClass;

import java.math.BigDecimal;
import java.sql.Date;

public class Kampanye {
    private int id_kampanye;
    private int id_akun;
    private String judul_kampanye;
    private String deskripsi_kampanye;
    private BigDecimal target_dana;
    private Date batas_waktu;
    private String status_kampanye;
    private String gambar_kampanye;

    // Getters and Setters
    public int getId_kampanye() {
        return id_kampanye;
    }

    public void setId_kampanye(int id_kampanye) {
        this.id_kampanye = id_kampanye;
    }

    public int getId_akun() {
        return id_akun;
    }

    public void setId_akun(int id_akun) {
        this.id_akun = id_akun;
    }

    public String getJudul_kampanye() {
        return judul_kampanye;
    }

    public void setJudul_kampanye(String judul_kampanye) {
        this.judul_kampanye = judul_kampanye;
    }

    public String getDeskripsi_kampanye() {
        return deskripsi_kampanye;
    }

    public void setDeskripsi_kampanye(String deskripsi_kampanye) {
        this.deskripsi_kampanye = deskripsi_kampanye;
    }

    public BigDecimal getTarget_dana() {
        return target_dana;
    }

    public void setTarget_dana(BigDecimal target_dana) {
        this.target_dana = target_dana;
    }

    public Date getBatas_waktu() {
        return batas_waktu;
    }

    public void setBatas_waktu(Date batas_waktu) {
        this.batas_waktu = batas_waktu;
    }

    public String getStatus_kampanye() {
        return status_kampanye;
    }

    public void setStatus_kampanye(String status_kampanye) {
        this.status_kampanye = status_kampanye;
    }

    public String getGambar_kampanye() {
        return gambar_kampanye;
    }

    public void setGambar_kampanye(String gambar_kampanye) {
        this.gambar_kampanye = gambar_kampanye;
    }
}