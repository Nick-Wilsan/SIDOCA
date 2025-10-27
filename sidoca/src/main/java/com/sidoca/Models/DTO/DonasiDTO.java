package com.sidoca.Models.DTO;

import java.math.BigDecimal;

public class DonasiDTO {
    private int idKampanye; // Tambahkan ini
    private String namaKampanye;
    private BigDecimal nominalDonasi;

    // Tambahkan getter dan setter untuk idKampanye
    public int getIdKampanye() {
        return idKampanye;
    }
    public void setIdKampanye(int idKampanye) {
        this.idKampanye = idKampanye;
    }

    // Getter dan Setter lainnya...
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