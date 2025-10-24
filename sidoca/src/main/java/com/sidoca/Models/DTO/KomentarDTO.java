package com.sidoca.Models.DTO;

import java.sql.Timestamp;

public class KomentarDTO {
    private String nama_pengirim;
    private String isi_komentar;
    private Timestamp tanggal_komentar;

    public String getNama_pengirim() {
        return nama_pengirim;
    }

    public void setNama_pengirim(String nama_pengirim) {
        this.nama_pengirim = nama_pengirim;
    }

    public String getIsi_komentar() {
        return isi_komentar;
    }

    public void setIsi_komentar(String isi_komentar) {
        this.isi_komentar = isi_komentar;
    }

    public Timestamp getTanggal_komentar() {
        return tanggal_komentar;
    }

    public void setTanggal_komentar(Timestamp tanggal_komentar) {
        this.tanggal_komentar = tanggal_komentar;
    }
}