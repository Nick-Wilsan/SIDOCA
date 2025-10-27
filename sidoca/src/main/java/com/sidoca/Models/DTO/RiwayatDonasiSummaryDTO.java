package com.sidoca.Models.DTO;

import java.math.BigDecimal;
import java.util.List;

public class RiwayatDonasiSummaryDTO {
    private BigDecimal totalDonasi;
    private long totalKampanye;
    private List<RiwayatDonasiItemDTO> rincianDonasi;

    // Getters and Setters
    public BigDecimal getTotalDonasi() { return totalDonasi; }
    public void setTotalDonasi(BigDecimal totalDonasi) { this.totalDonasi = totalDonasi; }
    public long getTotalKampanye() { return totalKampanye; }
    public void setTotalKampanye(long totalKampanye) { this.totalKampanye = totalKampanye; }
    public List<RiwayatDonasiItemDTO> getRincianDonasi() { return rincianDonasi; }
    public void setRincianDonasi(List<RiwayatDonasiItemDTO> rincianDonasi) { this.rincianDonasi = rincianDonasi; }
}