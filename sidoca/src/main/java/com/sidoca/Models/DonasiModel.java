package com.sidoca.Models;

import com.sidoca.Models.DTO.DonasiDTO;
import com.sidoca.Models.DTO.RiwayatDonasiItemDTO;
import com.sidoca.Models.DTO.RiwayatDonasiSummaryDTO;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DonasiModel extends BaseModel {

    public boolean saveDonasi(int idDonatur, int idKampanye, BigDecimal nominal, String orderId) {
        String query = "INSERT INTO Donasi (id_donatur, id_kampanye, nominal_donasi, order_id, status_pembayaran, tanggal_donasi) VALUES (?, ?, ?, ?, 'pending', NOW())";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idDonatur);
            stmt.setInt(2, idKampanye);
            stmt.setBigDecimal(3, nominal);
            stmt.setString(4, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatusByOrderId(String orderId, String status) {
        String query = "UPDATE Donasi SET status_pembayaran = ? WHERE order_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public DonasiDTO getDonasiAndKampanyeByOrderId(String orderId) {
        String query = "SELECT d.id_kampanye, k.judul_kampanye, d.nominal_donasi FROM Donasi d JOIN Kampanye k ON d.id_kampanye = k.id_kampanye WHERE d.order_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DonasiDTO dto = new DonasiDTO();
                dto.setIdKampanye(rs.getInt("id_kampanye")); // Ambil id_kampanye
                dto.setNamaKampanye(rs.getString("judul_kampanye"));
                dto.setNominalDonasi(rs.getBigDecimal("nominal_donasi"));
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RiwayatDonasiSummaryDTO getRiwayatDonasi(int idDonatur) {
        RiwayatDonasiSummaryDTO summary = new RiwayatDonasiSummaryDTO();
        List<RiwayatDonasiItemDTO> rincianList = new ArrayList<>();

        // Query untuk summary (Total Donasi dan Total Kampanye)
        String summaryQuery = "SELECT SUM(nominal_donasi) as total_donasi, COUNT(DISTINCT id_kampanye) as total_kampanye " +
                              "FROM Donasi WHERE id_donatur = ? AND status_pembayaran = 'berhasil'";

        // Query untuk rincian donasi
        String rincianQuery = "SELECT " +
                            "k.judul_kampanye, o.nama_organisasi, d.nominal_donasi, d.tanggal_donasi, " +
                            "(SELECT url_gambar FROM Kampanye_Gambar WHERE id_kampanye = k.id_kampanye LIMIT 1) as url_gambar " +
                            "FROM Donasi d " +
                            "JOIN Kampanye k ON d.id_kampanye = k.id_kampanye " +
                            "JOIN Akun a ON k.id_akun = a.id_akun " +
                            "JOIN Organisasi o ON a.id_akun = o.id_akun " +
                            "WHERE d.id_donatur = ? AND d.status_pembayaran = 'berhasil' " +
                            "ORDER BY d.tanggal_donasi DESC";
        
        try (Connection conn = getConnection()) {
            // Eksekusi query summary
            try (PreparedStatement stmt = conn.prepareStatement(summaryQuery)) {
                stmt.setInt(1, idDonatur);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    summary.setTotalDonasi(rs.getBigDecimal("total_donasi"));
                    summary.setTotalKampanye(rs.getLong("total_kampanye"));
                } else {
                    summary.setTotalDonasi(BigDecimal.ZERO);
                    summary.setTotalKampanye(0L);
                }
            }

            // Eksekusi query rincian
            try (PreparedStatement stmt = conn.prepareStatement(rincianQuery)) {
                stmt.setInt(1, idDonatur);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    RiwayatDonasiItemDTO item = new RiwayatDonasiItemDTO();
                    item.setGambarKampanye(rs.getString("url_gambar"));
                    item.setNamaKampanye(rs.getString("judul_kampanye"));
                    item.setNamaOrganisasi(rs.getString("nama_organisasi"));
                    item.setJumlahDonasi(rs.getBigDecimal("nominal_donasi"));
                    item.setTanggalDonasi(rs.getTimestamp("tanggal_donasi"));
                    rincianList.add(item);
                }
            }
            summary.setRincianDonasi(rincianList);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }
}