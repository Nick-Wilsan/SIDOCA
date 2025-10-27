package com.sidoca.Models.DTO;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RiwayatDonasiItemDTO {
    private String gambarKampanye;
    private String namaKampanye;
    private String namaOrganisasi;
    private BigDecimal jumlahDonasi;
    private Timestamp tanggalDonasi;

    // Getters and Setters
    public String getGambarKampanye() { return gambarKampanye; }
    public void setGambarKampanye(String gambarKampanye) { this.gambarKampanye = gambarKampanye; }
    public String getNamaKampanye() { return namaKampanye; }
    public void setNamaKampanye(String namaKampanye) { this.namaKampanye = namaKampanye; }
    public String getNamaOrganisasi() { return namaOrganisasi; }
    public void setNamaOrganisasi(String namaOrganisasi) { this.namaOrganisasi = namaOrganisasi; }
    public BigDecimal getJumlahDonasi() { return jumlahDonasi; }
    public void setJumlahDonasi(BigDecimal jumlahDonasi) { this.jumlahDonasi = jumlahDonasi; }
    public Timestamp getTanggalDonasi() { return tanggalDonasi; }
    public void setTanggalDonasi(Timestamp tanggalDonasi) { this.tanggalDonasi = tanggalDonasi; }
}