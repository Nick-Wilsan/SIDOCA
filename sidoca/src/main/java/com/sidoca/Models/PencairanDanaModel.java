package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.PencairanDana;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.math.BigDecimal;
import com.sidoca.Models.DTO.PencairanDanaDetailDTO;
import com.sidoca.Models.DTO.PencairanDanaVerifikasiDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PencairanDanaModel extends BaseModel {

    /**
     * Mengecek apakah sudah ada pencairan dana yang disetujui untuk sebuah kampanye.
     * @param idKampanye ID kampanye yang akan dicek.
     * @return boolean true jika sudah pernah ada, false jika belum.
     */
    public boolean hasCompletedDisbursements(int idKampanye) {
        String query = "SELECT COUNT(*) FROM Pencairan_Dana WHERE id_kampanye = ? AND status_pencairan = 'disetujui'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menghitung total dana yang sudah berhasil dicairkan untuk sebuah kampanye.
     * @param idKampanye ID kampanye.
     * @return BigDecimal total dana yang sudah dicairkan.
     */
    public BigDecimal getTotalDanaDicairkan(int idKampanye) {
        String query = "SELECT SUM(jumlah_dana) FROM Pencairan_Dana WHERE id_kampanye = ? AND status_pencairan = 'disetujui'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal(1);
                return total == null ? BigDecimal.ZERO : total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Menyimpan data pengajuan pencairan dana baru ke database.
     * @param pencairan Objek PencairanDana yang berisi semua detail pengajuan.
     * @return boolean true jika berhasil disimpan, false jika gagal.
     */
    public boolean savePencairan(PencairanDana pencairan) {
        String query = "INSERT INTO Pencairan_Dana (id_kampanye, id_organisasi, jumlah_dana, tanggal_pengajuan, status_pencairan, nama_bank, nomor_rekening, nama_pemilik_rekening, bukti_pendukung, alasan_pencairan) " +
                       "VALUES (?, ?, ?, NOW(), 'diajukan', ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, pencairan.getId_kampanye());
            stmt.setInt(2, pencairan.getId_organisasi());
            stmt.setBigDecimal(3, pencairan.getJumlah_dana());
            stmt.setString(4, pencairan.getNama_bank());
            stmt.setString(5, pencairan.getNomor_rekening());
            stmt.setString(6, pencairan.getNama_pemilik_rekening());
            stmt.setString(7, pencairan.getBukti_pendukung());
            stmt.setString(8, pencairan.getAlasan_pencairan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PencairanDanaVerifikasiDTO> getPengajuanPencairanDana(String sortBy, String sortOrder) {
        List<PencairanDanaVerifikasiDTO> daftarPengajuan = new ArrayList<>();
        String query = "SELECT pd.id_pencairan, o.nama_organisasi, k.judul_kampanye, pd.tanggal_pengajuan, pd.jumlah_dana " +
                    "FROM Pencairan_Dana pd " +
                    "JOIN Kampanye k ON pd.id_kampanye = k.id_kampanye " +
                    "JOIN Organisasi o ON pd.id_organisasi = o.id_organisasi " +
                    "WHERE pd.status_pencairan = 'diajukan' ";

        // Menambahkan logika pengurutan
        if (sortBy != null && !sortBy.isEmpty()) {
            String order = "asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";
            if ("jumlah".equals(sortBy)) {
                query += "ORDER BY pd.jumlah_dana " + order;
            } else if ("tanggal".equals(sortBy)) {
                query += "ORDER BY pd.tanggal_pengajuan " + order;
            }
        } else {
            query += "ORDER BY pd.tanggal_pengajuan DESC";
        }

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PencairanDanaVerifikasiDTO dto = new PencairanDanaVerifikasiDTO();
                dto.setIdPencairan(rs.getInt("id_pencairan"));
                dto.setNamaOrganisasi(rs.getString("nama_organisasi"));
                dto.setJudulKampanye(rs.getString("judul_kampanye"));
                dto.setTanggalPengajuan(rs.getTimestamp("tanggal_pengajuan"));
                dto.setJumlahDiajukan(rs.getBigDecimal("jumlah_dana"));
                daftarPengajuan.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarPengajuan;
    }

    public PencairanDanaDetailDTO getDetailPencairanById(int idPencairan) {
        String query = "SELECT pd.*, o.nama_organisasi, k.judul_kampanye " +
                    "FROM Pencairan_Dana pd " +
                    "JOIN Organisasi o ON pd.id_organisasi = o.id_organisasi " +
                    "JOIN Kampanye k ON pd.id_kampanye = k.id_kampanye " +
                    "WHERE pd.id_pencairan = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPencairan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PencairanDanaDetailDTO dto = new PencairanDanaDetailDTO();
                dto.setIdPencairan(rs.getInt("id_pencairan"));
                dto.setIdKampanye(rs.getInt("id_kampanye"));
                dto.setNamaOrganisasi(rs.getString("nama_organisasi"));
                dto.setJudulKampanye(rs.getString("judul_kampanye"));
                dto.setTanggalPengajuan(rs.getTimestamp("tanggal_pengajuan"));
                dto.setJumlahDiajukan(rs.getBigDecimal("jumlah_dana"));
                dto.setNamaBank(rs.getString("nama_bank"));
                dto.setNomorRekening(rs.getString("nomor_rekening"));
                dto.setNamaPemilikRekening(rs.getString("nama_pemilik_rekening"));
                dto.setAlasanPencairan(rs.getString("alasan_pencairan"));
                
                String buktiPaths = rs.getString("bukti_pendukung");
                if (buktiPaths != null && !buktiPaths.isEmpty()) {
                    dto.setBuktiPendukung(Arrays.asList(buktiPaths.split(",")));
                } else {
                    dto.setBuktiPendukung(new ArrayList<>());
                }

                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatusPencairan(int idPencairan, String status, String komentar) {
        String query = "UPDATE Pencairan_Dana SET status_pencairan = ?, komentar_admin = ?, tgl_verifikasi = NOW() WHERE id_pencairan = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, komentar);
            stmt.setInt(3, idPencairan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}