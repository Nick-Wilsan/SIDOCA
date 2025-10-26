package com.sidoca.Models.DTO;

public class KampanyeAdminDTO {
    private int id_kampanye;
    private String judul_kampanye;
    private String nama_organisasi;
    private String status_kampanye;

    // Getters and Setters
    public int getId_kampanye() { return id_kampanye; }
    public void setId_kampanye(int id_kampanye) { this.id_kampanye = id_kampanye; }
    
    public String getJudul_kampanye() { return judul_kampanye; }
    public void setJudul_kampanye(String judul_kampanye) { this.judul_kampanye = judul_kampanye; }
    
    public String getNama_organisasi() { return nama_organisasi; }
    public void setNama_organisasi(String nama_organisasi) { this.nama_organisasi = nama_organisasi; }
    
    public String getStatus_kampanye() { return status_kampanye; }
    public void setStatus_kampanye(String status_kampanye) { this.status_kampanye = status_kampanye; }
}