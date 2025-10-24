package com.sidoca.Models.DTO;

import java.math.BigDecimal;
import java.sql.Date;

public class KampanyeAktifDTO {
    private int id_kampanye;
    private String judul_kampanye;
    private String nama_organisasi;
    private BigDecimal target_dana;
    private BigDecimal dana_terkumpul;
    private Date batas_waktu;
    private String url_gambar;
    private long sisa_hari;
    private int persentase_terkumpul;

    // Getters and Setters
    public int getId_kampanye() { return id_kampanye; }
    public void setId_kampanye(int id_kampanye) { this.id_kampanye = id_kampanye; }

    public String getJudul_kampanye() { return judul_kampanye; }
    public void setJudul_kampanye(String judul_kampanye) { this.judul_kampanye = judul_kampanye; }

    public String getNama_organisasi() { return nama_organisasi; }
    public void setNama_organisasi(String nama_organisasi) { this.nama_organisasi = nama_organisasi; }

    public BigDecimal getTarget_dana() { return target_dana; }
    public void setTarget_dana(BigDecimal target_dana) { this.target_dana = target_dana; }

    public BigDecimal getDana_terkumpul() { return dana_terkumpul; }
    public void setDana_terkumpul(BigDecimal dana_terkumpul) { this.dana_terkumpul = dana_terkumpul; }

    public Date getBatas_waktu() { return batas_waktu; }
    public void setBatas_waktu(Date batas_waktu) { this.batas_waktu = batas_waktu; }

    public String getUrl_gambar() { return url_gambar; }
    public void setUrl_gambar(String url_gambar) { this.url_gambar = url_gambar; }

    public long getSisa_hari() { return sisa_hari; }
    public void setSisa_hari(long sisa_hari) { this.sisa_hari = sisa_hari; }

    public int getPersentase_terkumpul() { return persentase_terkumpul; }
    public void setPersentase_terkumpul(int persentase_terkumpul) { this.persentase_terkumpul = persentase_terkumpul; }
}