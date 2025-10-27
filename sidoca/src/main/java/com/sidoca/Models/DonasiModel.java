package com.sidoca.Models;

import com.sidoca.Models.DTO.DonasiDTO;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

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
        String query = "SELECT k.judul_kampanye, d.nominal_donasi FROM Donasi d JOIN Kampanye k ON d.id_kampanye = k.id_kampanye WHERE d.order_id = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DonasiDTO dto = new DonasiDTO();
                dto.setNamaKampanye(rs.getString("judul_kampanye"));
                dto.setNominalDonasi(rs.getBigDecimal("nominal_donasi"));
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}