package com.sidoca.Models;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DonaturModel extends BaseModel {

    public Integer getDonaturIdByAkunId(int idAkun) {
        String query = "SELECT id_donatur FROM Donatur WHERE id_akun = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAkun);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_donatur");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Mengembalikan null jika donatur tidak ditemukan
    }

    public boolean saveDonatur(int idAkun) {
        // Query untuk insert ke tabel Donatur
        String query = "INSERT INTO Donatur (id_akun) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAkun);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTotalDonasi(int idDonatur, BigDecimal nominal) {
        String query = "UPDATE Donatur SET total_donasi = total_donasi + ? WHERE id_donatur = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, nominal);
            stmt.setInt(2, idDonatur);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}