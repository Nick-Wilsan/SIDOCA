package com.sidoca.Models.DTO;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PencairanDanaVerifikasiDTO {
    private int idPencairan;
    private String namaOrganisasi;
    private String judulKampanye;
    private Timestamp tanggalPengajuan;
    private BigDecimal jumlahDiajukan;

    // Getters and Setters
    public int getIdPencairan() { return idPencairan; }
    public void setIdPencairan(int idPencairan) { this.idPencairan = idPencairan; }
    public String getNamaOrganisasi() { return namaOrganisasi; }
    public void setNamaOrganisasi(String namaOrganisasi) { this.namaOrganisasi = namaOrganisasi; }
    public String getJudulKampanye() { return judulKampanye; }
    public void setJudulKampanye(String judulKampanye) { this.judulKampanye = judulKampanye; }
    public Timestamp getTanggalPengajuan() { return tanggalPengajuan; }
    public void setTanggalPengajuan(Timestamp tanggalPengajuan) { this.tanggalPengajuan = tanggalPengajuan; }
    public BigDecimal getJumlahDiajukan() { return jumlahDiajukan; }
    public void setJumlahDiajukan(BigDecimal jumlahDiajukan) { this.jumlahDiajukan = jumlahDiajukan; }
}