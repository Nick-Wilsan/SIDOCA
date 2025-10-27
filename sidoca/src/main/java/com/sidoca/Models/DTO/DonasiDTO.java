package com.sidoca.Models.DTO;

import java.math.BigDecimal;

public class DonasiDTO {
    private int idDonasi;
    private int idDonatur;
    private int idKampanye;
    private String namaKampanye;
    private BigDecimal nominalDonasi;

    // Getters and Setters
    public int getIdDonasi() {
        return idDonasi;
    }
    public void setIdDonasi(int idDonasi) {
        this.idDonasi = idDonasi;
    }
    public int getIdDonatur() {
        return idDonatur;
    }
    public void setIdDonatur(int idDonatur) {
        this.idDonatur = idDonatur;
    }
    public int getIdKampanye() {
        return idKampanye;
    }
    public void setIdKampanye(int idKampanye) {
        this.idKampanye = idKampanye;
    }
    public String getNamaKampanye() {
        return namaKampanye;
    }
    public void setNamaKampanye(String namaKampanye) {
        this.namaKampanye = namaKampanye;
    }
    public BigDecimal getNominalDonasi() {
        return nominalDonasi;
    }
    public void setNominalDonasi(BigDecimal nominalDonasi) {
        this.nominalDonasi = nominalDonasi;
    }
}