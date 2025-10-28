package com.sidoca.Models.DTO;

public class ProfilDTO {
    private int idAkun;
    private String nama;
    private String username;
    private String email;
    private String noHp;
    private String alamat;
    private String photoProfile;
    private String deskripsiOrganisasi; // Khusus untuk organisasi

    // Getters and Setters
    public int getIdAkun() { return idAkun; }
    public void setIdAkun(int idAkun) { this.idAkun = idAkun; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoHp() { return noHp; }
    public void setNoHp(String noHp) { this.noHp = noHp; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getPhotoProfile() { return photoProfile; }
    public void setPhotoProfile(String photoProfile) { this.photoProfile = photoProfile; }

    public String getDeskripsiOrganisasi() { return deskripsiOrganisasi; }
    public void setDeskripsiOrganisasi(String deskripsiOrganisasi) { this.deskripsiOrganisasi = deskripsiOrganisasi; }
}