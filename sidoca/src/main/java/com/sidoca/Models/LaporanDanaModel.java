package com.sidoca.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import org.springframework.stereotype.Component;

import com.sidoca.Models.DataBaseClass.LaporanDana;

@Component
public class LaporanDanaModel extends BaseModel{

    public LaporanDana GetLaporanDanaById(int id_laporan){
        String query = "SELECT * FROM laporan_dana WHERE id_laporan = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id_laporan);

            ResultSet rs = stmt.executeQuery();

            // Jika data ditemukan, buat objek User
            if (rs.next()) {
                LaporanDana laporanDana = new LaporanDana();
                laporanDana.setId_laporan(rs.getInt("id_laporan"));
                laporanDana.setId_kampanye(rs.getInt("id_kampanye"));
                laporanDana.setId_organisasi(rs.getInt("id_organisasi"));
                laporanDana.setBukti_dokumen(rs.getBytes("bukti_dokumen"));
                laporanDana.setDeskripsi_penggunaan(rs.getString("deskripsi_penggunaan"));
                laporanDana.setStatus_verifikasi(rs.getString("status_verifikasi"));
                Timestamp tglPengajuan = rs.getTimestamp("tgl_pengajuan");
                Timestamp tglVerifikasi = rs.getTimestamp("tgl_verifikasi");

                if (tglPengajuan != null) {
                    laporanDana.setTgl_pengajuan(tglPengajuan.toLocalDateTime());
                }
                if (tglVerifikasi != null) {
                    laporanDana.setTgl_verifikasi(tglVerifikasi.toLocalDateTime());
                }

                return laporanDana;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int InsertLaporanDana(LaporanDana laporanDana){
        String query = "INSERT INTO laporan_dana \r\n" + //
                        "(id_kampanye, id_organisasi, total_pengeluaran,bukti_dokumen, deskripsi_penggunaan, status_verifikasi, tgl_pengajuan)\r\n" + //
                        "VALUES (?, ?, ?, ?, ?, ?, ?);";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, laporanDana.getId_kampanye());
            stmt.setInt(2, laporanDana.getId_organisasi());
            stmt.setInt(3, laporanDana.getTotal_Pengeluaran());
            stmt.setBytes(4, laporanDana.getBukti_dokumen());
            stmt.setString(5, laporanDana.getDeskripsi_penggunaan());
            stmt.setString(6, laporanDana.getStatus_verifikasi());

            // mengecek input waktu
            if (laporanDana.getTgl_pengajuan() != null)
                stmt.setTimestamp(7, Timestamp.valueOf(laporanDana.getTgl_pengajuan()));
            else
                stmt.setNull(7, Types.TIMESTAMP);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
