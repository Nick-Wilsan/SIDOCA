package com.sidoca.Models;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class OrganisasiModel extends BaseModel {

    /**
     * Menyimpan data organisasi baru berdasarkan id_akun.
     * @param idAkun ID akun yang terhubung dengan organisasi.
     * @param namaOrganisasi Nama organisasi (diambil dari nama akun).
     * @return boolean true jika berhasil, false jika gagal.
     */
    public boolean saveOrganisasi(int idAkun, String namaOrganisasi) {
        // Query untuk insert ke tabel Organisasi
        String query = "INSERT INTO Organisasi (id_akun, nama_organisasi) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAkun);
            // Menggunakan nama dari akun sebagai nama organisasi default
            stmt.setString(2, namaOrganisasi);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int GetIdOrganisasiByIdAkun(int idAkun) {
        String query = "SELECT id_organisasi FROM Organisasi WHERE id_akun = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAkun);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_organisasi");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Mengembalikan -1 jika tidak ditemukan atau gagal
    }
}