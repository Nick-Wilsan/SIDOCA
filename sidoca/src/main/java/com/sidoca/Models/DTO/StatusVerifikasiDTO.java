// File: src/main/java/com/sidoca/Models/DTO/StatusVerifikasiDTO.java

package com.sidoca.Models.DTO;

import java.sql.Timestamp;

public class StatusVerifikasiDTO {
    private int idKampanye;
    private String namaKampanye;
    private String jenisPengajuan;
    private Timestamp tanggalPengajuan;
    private Timestamp tanggalVerifikasi;
    private String statusVerifikasi;

    // Getter and Setter for idKampanye
    public int getIdKampanye() {
        return idKampanye;
    }

    public void setIdKampanye(int idKampanye) {
        this.idKampanye = idKampanye;
    }

    // Getters and Setters
    public String getNamaKampanye() {
        return namaKampanye;
    }

    public void setNamaKampanye(String namaKampanye) {
        this.namaKampanye = namaKampanye;
    }

    public String getJenisPengajuan() {
        return jenisPengajuan;
    }

    public void setJenisPengajuan(String jenisPengajuan) {
        this.jenisPengajuan = jenisPengajuan;
    }

    public Timestamp getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public void setTanggalPengajuan(Timestamp tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public Timestamp getTanggalVerifikasi() {
        return tanggalVerifikasi;
    }

    public void setTanggalVerifikasi(Timestamp tanggalVerifikasi) {
        this.tanggalVerifikasi = tanggalVerifikasi;
    }

    public String getStatusVerifikasi() {
        return statusVerifikasi;
    }

    public void setStatusVerifikasi(String statusVerifikasi) {
        this.statusVerifikasi = statusVerifikasi;
    }
}