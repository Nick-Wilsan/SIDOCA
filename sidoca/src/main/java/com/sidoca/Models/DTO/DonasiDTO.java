package com.sidoca.Models.DTO;

import java.math.BigDecimal;

public class DonasiDTO {
    private String namaKampanye;
    private BigDecimal nominalDonasi;

    // Getters and Setters
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