package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.Kampanye;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sidoca.Models.DTO.KampanyeDetailDTO;
import com.sidoca.Models.DTO.KampanyeVerifikasiDTO;
import java.util.ArrayList;
import java.util.List;

@Component
public class KampanyeModel extends BaseModel {

    public int saveKampanye(Kampanye kampanye) {
        String query = "INSERT INTO Kampanye (id_akun, judul_kampanye, deskripsi_kampanye, target_dana, batas_waktu, status_kampanye) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, kampanye.getId_akun());
            stmt.setString(2, kampanye.getJudul_kampanye());
            stmt.setString(3, kampanye.getDeskripsi_kampanye());
            stmt.setBigDecimal(4, kampanye.getTarget_dana());
            stmt.setDate(5, kampanye.getBatas_waktu());
            stmt.setString(6, kampanye.getStatus_kampanye());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Mengembalikan ID kampanye yang baru dibuat
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Mengembalikan -1 jika gagal
    }

    public List<KampanyeVerifikasiDTO> getKampanyeMenungguVerifikasi() {
        List<KampanyeVerifikasiDTO> kampanyeList = new ArrayList<>();
        String query = "SELECT k.id_kampanye, k.judul_kampanye, o.nama_organisasi " +
                    "FROM Kampanye k " +
                    "JOIN Akun a ON k.id_akun = a.id_akun " +
                    "JOIN Organisasi o ON a.id_akun = o.id_akun " +
                    "WHERE k.status_kampanye = 'menunggu'";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                KampanyeVerifikasiDTO dto = new KampanyeVerifikasiDTO();
                dto.setId_kampanye(rs.getInt("id_kampanye"));
                dto.setJudul_kampanye(rs.getString("judul_kampanye"));
                dto.setNama_organisasi(rs.getString("nama_organisasi"));
                kampanyeList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kampanyeList;
    }

    public KampanyeDetailDTO getDetailKampanyeById(int idKampanye) {
        KampanyeDetailDTO detail = null;
        String query = "SELECT k.id_kampanye, k.judul_kampanye, k.deskripsi_kampanye, k.target_dana, k.batas_waktu, o.nama_organisasi " +
                    "FROM Kampanye k " +
                    "JOIN Akun a ON k.id_akun = a.id_akun " +
                    "JOIN Organisasi o ON a.id_akun = o.id_akun " +
                    "WHERE k.id_kampanye = ? AND k.status_kampanye = 'menunggu'";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idKampanye);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                detail = new KampanyeDetailDTO();
                detail.setId_kampanye(rs.getInt("id_kampanye"));
                detail.setJudul_kampanye(rs.getString("judul_kampanye"));
                detail.setDeskripsi_kampanye(rs.getString("deskripsi_kampanye"));
                detail.setTarget_dana(rs.getBigDecimal("target_dana"));
                detail.setBatas_waktu(rs.getDate("batas_waktu"));
                detail.setNama_organisasi(rs.getString("nama_organisasi"));

                // Mengambil semua URL gambar terkait
                detail.setGambarUrls(getGambarUrlsByKampanyeId(idKampanye));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detail;
    }

    private List<String> getGambarUrlsByKampanyeId(int idKampanye) throws SQLException {
        List<String> urls = new ArrayList<>();
        String query = "SELECT url_gambar FROM Kampanye_Gambar WHERE id_kampanye = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                urls.add(rs.getString("url_gambar"));
            }
        }
        return urls;
    }

    public boolean updateStatusKampanye(int idKampanye, String status, String alasan) {
        // Query diubah untuk menyertakan kolom alasan_penolakan
        String query = "UPDATE Kampanye SET status_kampanye = ?, alasan_penolakan = ? WHERE id_kampanye = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);

            // Jika statusnya 'ditolak', simpan alasannya. Jika tidak, simpan NULL.
            if ("ditolak".equals(status) && alasan != null && !alasan.isEmpty()) {
                stmt.setString(2, alasan);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            stmt.setInt(3, idKampanye);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}  