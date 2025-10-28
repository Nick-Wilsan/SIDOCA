package com.sidoca.Models.DTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class PencairanDanaDetailDTO {
    private int idPencairan;
    private int idKampanye;
    private String namaOrganisasi;
    private String judulKampanye;
    private Timestamp tanggalPengajuan;
    private BigDecimal jumlahDiajukan;
    private String namaBank;
    private String nomorRekening;
    private String namaPemilikRekening;
    private String alasanPencairan;
    private List<String> buktiPendukung; // Menggunakan List<String>

    // Getters and Setters
    public int getIdPencairan() { return idPencairan; }
    public void setIdPencairan(int idPencairan) { this.idPencairan = idPencairan; }
    public int getIdKampanye() { return idKampanye; }
    public void setIdKampanye(int idKampanye) { this.idKampanye = idKampanye; }
    public String getNamaOrganisasi() { return namaOrganisasi; }
    public void setNamaOrganisasi(String namaOrganisasi) { this.namaOrganisasi = namaOrganisasi; }
    public String getJudulKampanye() { return judulKampanye; }
    public void setJudulKampanye(String judulKampanye) { this.judulKampanye = judulKampanye; }
    public Timestamp getTanggalPengajuan() { return tanggalPengajuan; }
    public void setTanggalPengajuan(Timestamp tanggalPengajuan) { this.tanggalPengajuan = tanggalPengajuan; }
    public BigDecimal getJumlahDiajukan() { return jumlahDiajukan; }
    public void setJumlahDiajukan(BigDecimal jumlahDiajukan) { this.jumlahDiajukan = jumlahDiajukan; }
    public String getNamaBank() { return namaBank; }
    public void setNamaBank(String namaBank) { this.namaBank = namaBank; }
    public String getNomorRekening() { return nomorRekening; }
    public void setNomorRekening(String nomorRekening) { this.nomorRekening = nomorRekening; }
    public String getNamaPemilikRekening() { return namaPemilikRekening; }
    public void setNamaPemilikRekening(String namaPemilikRekening) { this.namaPemilikRekening = namaPemilikRekening; }
    public String getAlasanPencairan() { return alasanPencairan; }
    public void setAlasanPencairan(String alasanPencairan) { this.alasanPencairan = alasanPencairan; }
    public List<String> getBuktiPendukung() { return buktiPendukung; }
    public void setBuktiPendukung(List<String> buktiPendukung) { this.buktiPendukung = buktiPendukung; }
}