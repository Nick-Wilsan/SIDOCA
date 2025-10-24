package com.sidoca.Models.DTO;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class KampanyeDetailDTO {
    private int id_kampanye;
    private String judul_kampanye;
    private String deskripsi_kampanye;
    private BigDecimal target_dana;
    private Date batas_waktu;
    private String status_kampanye;

    // Data Organisasi
    private String nama_organisasi;
    private String deskripsi_organisasi; // Jika ada, bisa ditambahkan
    private String url_gambar_organisasi; // Jika ada

    // Data Terkumpul
    private BigDecimal dana_terkumpul;
    private long sisa_hari;
    private int persentase_terkumpul;

    // Gambar Kampanye
    private List<String> url_gambar_kampanye;

    // Komentar
    private List<KomentarDTO> daftar_komentar;

    // Konstruktor, Getter, dan Setter

    public int getId_kampanye() {
        return id_kampanye;
    }

    public void setId_kampanye(int id_kampanye) {
        this.id_kampanye = id_kampanye;
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

    public String getUrl_gambar_organisasi() {
        return url_gambar_organisasi;
    }

    public void setUrl_gambar_organisasi(String url_gambar_organisasi) {
        this.url_gambar_organisasi = url_gambar_organisasi;
    }

    public BigDecimal getDana_terkumpul() {
        return dana_terkumpul;
    }

    public void setDana_terkumpul(BigDecimal dana_terkumpul) {
        this.dana_terkumpul = dana_terkumpul;
    }

    public long getSisa_hari() {
        return sisa_hari;
    }

    public void setSisa_hari(long sisa_hari) {
        this.sisa_hari = sisa_hari;
    }

    public int getPersentase_terkumpul() {
        return persentase_terkumpul;
    }

    public void setPersentase_terkumpul(int persentase_terkumpul) {
        this.persentase_terkumpul = persentase_terkumpul;
    }

    public List<String> getUrl_gambar_kampanye() {
        return url_gambar_kampanye;
    }

    public void setUrl_gambar_kampanye(List<String> url_gambar_kampanye) {
        this.url_gambar_kampanye = url_gambar_kampanye;
    }

    public List<KomentarDTO> getDaftar_komentar() {
        return daftar_komentar;
    }

    public void setDaftar_komentar(List<KomentarDTO> daftar_komentar) {
        this.daftar_komentar = daftar_komentar;
    }
}