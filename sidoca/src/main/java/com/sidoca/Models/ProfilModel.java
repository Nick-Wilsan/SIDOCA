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

    /**
     * Memeriksa apakah email sudah digunakan oleh akun lain.
     */
    public boolean isEmailTaken(String email, int idAkun) {
        String query = "SELECT id_akun FROM Akun WHERE email = ? AND id_akun != ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setInt(2, idAkun);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Memeriksa apakah nomor HP sudah digunakan oleh akun lain.
     */
    public boolean isNoHpTaken(String noHp, int idAkun) {
        if (noHp == null || noHp.trim().isEmpty()) {
            return false;
        }
        String query = "SELECT id_akun FROM Akun WHERE no_hp = ? AND id_akun != ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, noHp);
            stmt.setInt(2, idAkun);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Memperbarui data profil pengguna (selain email).
     */
    public boolean updateProfil(ProfilDTO profilDTO, String role) {
        String akunQuery = "UPDATE Akun SET nama = ?, no_hp = ? WHERE id_akun = ?";
        String profilQuery = "UPDATE Profil SET alamat = ? WHERE id_akun = ?";
        String orgQuery = "UPDATE Organisasi SET deskripsi_organisasi = ? WHERE id_akun = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(akunQuery)) {
                stmt.setString(1, profilDTO.getNama());
                stmt.setString(2, profilDTO.getNoHp());
                stmt.setInt(3, profilDTO.getIdAkun());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(profilQuery)) {
                stmt.setString(1, profilDTO.getAlamat());
                stmt.setInt(2, profilDTO.getIdAkun());
                stmt.executeUpdate();
            }

            if ("organisasi".equals(role)) {
                try (PreparedStatement stmt = conn.prepareStatement(orgQuery)) {
                    stmt.setString(1, profilDTO.getDeskripsiOrganisasi());
                    stmt.setInt(2, profilDTO.getIdAkun());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Memperbarui alamat email setelah verifikasi berhasil.
     */
    public boolean updateEmail(int idAkun, String newEmail) {
        String query = "UPDATE Akun SET email = ? WHERE id_akun = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, idAkun);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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