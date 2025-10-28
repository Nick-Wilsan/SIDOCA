package com.sidoca.Models.DataBaseClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PencairanDana {
    private int id_pencairan;
    private int id_kampanye;
    private int id_organisasi;
    private BigDecimal jumlah_dana;
    private LocalDateTime tanggal_pengajuan;
    private String status_pencairan;
    private LocalDateTime tgl_verifikasi;
    private String nama_bank;
    private String nomor_rekening;
    private String nama_pemilik_rekening;
    private String bukti_pendukung; // Menyimpan path ke file
    private String alasan_pencairan;

    // Getters and Setters

    public int getId_pencairan() {
        return id_pencairan;
    }

    public void setId_pencairan(int id_pencairan) {
        this.id_pencairan = id_pencairan;
    }

    public int getId_kampanye() {
        return id_kampanye;
    }

    public void setId_kampanye(int id_kampanye) {
        this.id_kampanye = id_kampanye;
    }

    public int getId_organisasi() {
        return id_organisasi;
    }

    public void setId_organisasi(int id_organisasi) {
        this.id_organisasi = id_organisasi;
    }

    public BigDecimal getJumlah_dana() {
        return jumlah_dana;
    }

    public void setJumlah_dana(BigDecimal jumlah_dana) {
        this.jumlah_dana = jumlah_dana;
    }

    public LocalDateTime getTanggal_pengajuan() {
        return tanggal_pengajuan;
    }

    public void setTanggal_pengajuan(LocalDateTime tanggal_pengajuan) {
        this.tanggal_pengajuan = tanggal_pengajuan;
    }

    public String getStatus_pencairan() {
        return status_pencairan;
    }

    public void setStatus_pencairan(String status_pencairan) {
        this.status_pencairan = status_pencairan;
    }

    public LocalDateTime getTgl_verifikasi() {
        return tgl_verifikasi;
    }

    public void setTgl_verifikasi(LocalDateTime tgl_verifikasi) {
        this.tgl_verifikasi = tgl_verifikasi;
    }

    public String getNama_bank() {
        return nama_bank;
    }

    public void setNama_bank(String nama_bank) {
        this.nama_bank = nama_bank;
    }

    public String getNomor_rekening() {
        return nomor_rekening;
    }

    public void setNomor_rekening(String nomor_rekening) {
        this.nomor_rekening = nomor_rekening;
    }

    public String getNama_pemilik_rekening() {
        return nama_pemilik_rekening;
    }

    public void setNama_pemilik_rekening(String nama_pemilik_rekening) {
        this.nama_pemilik_rekening = nama_pemilik_rekening;
    }

    public String getBukti_pendukung() {
        return bukti_pendukung;
    }

    public void setBukti_pendukung(String bukti_pendukung) {
        this.bukti_pendukung = bukti_pendukung;
    }

    public String getAlasan_pencairan() {
        return alasan_pencairan;
    }

    public void setAlasan_pencairan(String alasan_pencairan) {
        this.alasan_pencairan = alasan_pencairan;
    }
}