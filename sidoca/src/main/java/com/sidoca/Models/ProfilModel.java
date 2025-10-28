package com.sidoca.Models;

import com.sidoca.Models.DTO.ProfilDTO;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProfilModel extends BaseModel {

    public ProfilDTO getProfilByAkunId(int idAkun, String role) {
        ProfilDTO profil = null;
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT a.id_akun, a.nama, a.username, a.email, a.no_hp, p.alamat, p.photo_profile "
        );

        if ("organisasi".equals(role)) {
            queryBuilder.append(", o.deskripsi_organisasi ");
        }

        queryBuilder.append("FROM Akun a LEFT JOIN Profil p ON a.id_akun = p.id_akun ");

        if ("organisasi".equals(role)) {
            queryBuilder.append("LEFT JOIN Organisasi o ON a.id_akun = o.id_akun ");
        }

        queryBuilder.append("WHERE a.id_akun = ?");

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            stmt.setInt(1, idAkun);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                profil = new ProfilDTO();
                profil.setIdAkun(rs.getInt("id_akun"));
                profil.setNama(rs.getString("nama"));
                profil.setUsername(rs.getString("username"));
                profil.setEmail(rs.getString("email"));
                profil.setNoHp(rs.getString("no_hp"));
                profil.setAlamat(rs.getString("alamat"));
                profil.setPhotoProfile(rs.getString("photo_profile"));
                if ("organisasi".equals(role)) {
                    profil.setDeskripsiOrganisasi(rs.getString("deskripsi_organisasi"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profil;
    }

    public boolean updateProfil(ProfilDTO profilDTO, String role) {
        // Implementasi logika update akan ditambahkan nanti
        // Anda perlu membuat beberapa query UPDATE di sini
        return true;
    }
    
    public boolean verifyOldPassword(int idAkun, String oldPassword) {
        String query = "SELECT id_akun FROM Akun WHERE id_akun = ? AND password = PASSWORD(?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idAkun);
            stmt.setString(2, oldPassword);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int idAkun, String newPassword) {
        String query = "UPDATE Akun SET password = PASSWORD(?) WHERE id_akun = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, idAkun);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}