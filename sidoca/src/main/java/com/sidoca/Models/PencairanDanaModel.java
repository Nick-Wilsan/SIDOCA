package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.PencairanDana;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.math.BigDecimal;

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
}