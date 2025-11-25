package com.sidoca.Models;

import com.sidoca.Models.DTO.LeaderboardDTO;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Component
public class LeaderboardModel extends BaseModel {

public List<LeaderboardDTO> getTopDonaturs(int limit) {
        List<LeaderboardDTO> donaturs = new ArrayList<>();
        String query = "SELECT a.nama, d.total_donasi, p.photo_profile, a.id_akun " +
                       "FROM Donatur d " +
                       "JOIN Akun a ON d.id_akun = a.id_akun " +
                       "LEFT JOIN Profil p ON a.id_akun = p.id_akun " +
                       "WHERE a.status = 'aktif' AND d.total_donasi > 0 " +
                       "ORDER BY d.total_donasi DESC " +
                       "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            int rank = 1;
            while (rs.next()) {
                LeaderboardDTO dto = new LeaderboardDTO();
                dto.setPeringkat(rank++);
                dto.setNamaDonatur(rs.getString("nama"));
                dto.setTotalDonasi(rs.getBigDecimal("total_donasi"));
                
                // LOGIKA BARU: Cek Anonimitas & Foto
                String rawPhoto = rs.getString("photo_profile");
                boolean isAnonim = dto.getNamaDonatur().toLowerCase().contains("anonim");
                
                dto.setAnonim(isAnonim);

                // Jika Anonim ATAU foto null/kosong di database -> Pakai default
                if (isAnonim || rawPhoto == null || rawPhoto.trim().isEmpty()) {
                    dto.setPhotoProfile("1_default.png"); 
                } else {
                    dto.setPhotoProfile(rawPhoto);
                }
                
                donaturs.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donaturs;
    }

    public LeaderboardDTO getMyRanking(int idAkun) {
        // Gabungkan data donatur dan peringkatnya dalam satu query
        String query = 
            "SELECT sub.ranking, a.nama, d.total_donasi " +
            "FROM ( " +
            "    SELECT id_donatur, RANK() OVER (ORDER BY total_donasi DESC) AS ranking " +
            "    FROM Donatur WHERE total_donasi > 0" +
            ") sub " +
            "JOIN Donatur d ON sub.id_donatur = d.id_donatur " +
            "JOIN Akun a ON d.id_akun = a.id_akun " +
            "WHERE a.id_akun = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAkun);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LeaderboardDTO dto = new LeaderboardDTO();
                dto.setPeringkat(rs.getInt("ranking"));
                dto.setNamaDonatur(rs.getString("nama"));
                dto.setTotalDonasi(rs.getBigDecimal("total_donasi"));
                // Asumsi foto profil didapatkan dari sesi atau query terpisah jika diperlukan
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Mengembalikan null jika pengguna tidak ditemukan di leaderboard
    }
}