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
}