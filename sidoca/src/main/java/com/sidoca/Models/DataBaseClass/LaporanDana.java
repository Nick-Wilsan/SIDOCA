package com.sidoca.Models.DataBaseClass;

import java.time.LocalDateTime;

public class LaporanDana {
    private int id_laporan;
    private int id_kampanye;
    private int id_organisasi;
    private byte[] bukti_dokumen;
    private String deskripsi_penggunaan;
    private String status_verifikasi;
    private LocalDateTime tgl_pengajuan;
    private LocalDateTime tgl_verifikasi;

    public int getId_laporan() {
        return id_laporan;
    }

    public void setId_laporan(int id_laporan) {
        this.id_laporan = id_laporan;
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

    public byte[] getBukti_dokumen() {
        return bukti_dokumen;
    }

    public void setBukti_dokumen(byte[] bukti_dokumen) {
        this.bukti_dokumen = bukti_dokumen;
    }

    public String getDeskripsi_penggunaan() {
        return deskripsi_penggunaan;
    }

    public void setDeskripsi_penggunaan(String deskripsi_penggunaan) {
        this.deskripsi_penggunaan = deskripsi_penggunaan;
    }

    public String getStatus_verifikasi() {
        return status_verifikasi;
    }

    public void setStatus_verifikasi(String status_verifikasi) {
        this.status_verifikasi = status_verifikasi;
    }

    public LocalDateTime getTgl_pengajuan() {
        return tgl_pengajuan;
    }

    public void setTgl_pengajuan(LocalDateTime tgl_pengajuan) {
        this.tgl_pengajuan = tgl_pengajuan;
    }

    public LocalDateTime getTgl_verifikasi() {
        return tgl_verifikasi;
    }

    public void setTgl_verifikasi(LocalDateTime tgl_verifikasi) {
        this.tgl_verifikasi = tgl_verifikasi;
    }
}
