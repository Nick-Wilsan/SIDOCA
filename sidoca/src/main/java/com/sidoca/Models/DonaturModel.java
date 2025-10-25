package com.sidoca.Models;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DonaturModel extends BaseModel {

    /**
     * Mengambil id_donatur berdasarkan id_akun pengguna yang sedang login.
     * @param idAkun ID dari tabel Akun.
     * @return Integer id_donatur, atau null jika tidak ditemukan.
     */
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

    /**
     * Menyimpan data donatur baru berdasarkan id_akun.
     * @param idAkun ID akun yang terhubung dengan donatur.
     * @return boolean true jika berhasil, false jika gagal.
     */
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
}