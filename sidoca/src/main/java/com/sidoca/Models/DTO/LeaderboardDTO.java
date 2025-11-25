package com.sidoca.Models.DTO;

import java.math.BigDecimal;

public class LeaderboardDTO {
    private int peringkat;
    private String namaDonatur;
    private BigDecimal totalDonasi;
    private String photoProfile;
    private boolean isAnonim;

    // Getters and Setters
    public int getPeringkat() { return peringkat; }
    public void setPeringkat(int peringkat) { this.peringkat = peringkat; }
    public String getNamaDonatur() { return namaDonatur; }
    public void setNamaDonatur(String namaDonatur) { this.namaDonatur = namaDonatur; }
    public BigDecimal getTotalDonasi() { return totalDonasi; }
    public void setTotalDonasi(BigDecimal totalDonasi) { this.totalDonasi = totalDonasi; }
    public String getPhotoProfile() { return photoProfile; }
    public void setPhotoProfile(String photoProfile) { this.photoProfile = photoProfile; }
    public boolean isAnonim() { return isAnonim; }
    public void setAnonim(boolean isAnonim) { this.isAnonim = isAnonim; }
}
