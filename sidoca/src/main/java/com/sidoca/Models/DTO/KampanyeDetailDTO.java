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
    private String nama_organisasi;
    private List<String> gambarUrls;

    // Getters and Setters
    public int getId_kampanye() { return id_kampanye; }
    public void setId_kampanye(int id_kampanye) { this.id_kampanye = id_kampanye; }
    public String getJudul_kampanye() { return judul_kampanye; }
    public void setJudul_kampanye(String judul_kampanye) { this.judul_kampanye = judul_kampanye; }
    public String getDeskripsi_kampanye() { return deskripsi_kampanye; }
    public void setDeskripsi_kampanye(String deskripsi_kampanye) { this.deskripsi_kampanye = deskripsi_kampanye; }
    public BigDecimal getTarget_dana() { return target_dana; }
    public void setTarget_dana(BigDecimal target_dana) { this.target_dana = target_dana; }
    public Date getBatas_waktu() { return batas_waktu; }
    public void setBatas_waktu(Date batas_waktu) { this.batas_waktu = batas_waktu; }
    public String getNama_organisasi() { return nama_organisasi; }
    public void setNama_organisasi(String nama_organisasi) { this.nama_organisasi = nama_organisasi; }
    public List<String> getGambarUrls() { return gambarUrls; }
    public void setGambarUrls(List<String> gambarUrls) { this.gambarUrls = gambarUrls; }
}