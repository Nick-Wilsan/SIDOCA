package com.sidoca.Models.DataBaseClass;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Kampanye {
    private int id_kampanye;
    private int id_akun;
    private String judul_kampanye;
    private String deskripsi_kampanye;
    private BigDecimal target_dana;
    private BigDecimal dana_terkumpul;
    private Date batas_waktu;
    private String status_kampanye;
    private String alasan_penolakan;
    private Timestamp tgl_pengajuan;
    private Timestamp tgl_verifikasi;


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

    public BigDecimal getDana_terkumpul() {
        return dana_terkumpul;
    }

    public void setDana_terkumpul(BigDecimal dana_terkumpul) {
        this.dana_terkumpul = dana_terkumpul;
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

    public String getAlasan_penolakan() {
        return alasan_penolakan;
    }

    public void setAlasan_penolakan(String alasan_penolakan) {
        this.alasan_penolakan = alasan_penolakan;
    }

    public Timestamp getTgl_pengajuan() {
        return tgl_pengajuan;
    }

    public void setTgl_pengajuan(Timestamp tgl_pengajuan) {
        this.tgl_pengajuan = tgl_pengajuan;
    }

    public Timestamp getTgl_verifikasi() {
        return tgl_verifikasi;
    }

    public void setTgl_verifikasi(Timestamp tgl_verifikasi) {
        this.tgl_verifikasi = tgl_verifikasi;
    }

    public int getSisaHari(){
        if (batas_waktu == null) {
            return 0;
        }

        // Konversi batas_waktu ke LocalDate
        LocalDate batas = batas_waktu.toLocalDate();
        LocalDate sekarang = LocalDate.now();

        // Hitung selisih hari
        return (int) ChronoUnit.DAYS.between(sekarang, batas);
    }
}
