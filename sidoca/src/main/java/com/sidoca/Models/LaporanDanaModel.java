package com.sidoca.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
                laporanDana.setTotal_Pengeluaran(rs.getBigDecimal("total_pengeluaran").intValue());
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

    public byte[] GetBuktiById(int id_laporan){
        String query = "SELECT bukti_dokumen FROM laporan_dana WHERE id_laporan = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id_laporan);

            ResultSet rs = stmt.executeQuery();

            // Jika data ditemukan, buat objek User
            if (rs.next()){
                return rs.getBytes("bukti_dokumen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<LaporanDana> GetAllLaporanDanaVerifikasi(){
        String query = "SELECT * FROM laporan_dana WHERE status_verifikasi = 'menunggu'";
        List<LaporanDana> list = new ArrayList<>();

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {
                LaporanDana laporanDana = new LaporanDana();
                laporanDana.setId_laporan(rs.getInt("id_laporan"));
                laporanDana.setId_kampanye(rs.getInt("id_kampanye"));
                laporanDana.setId_organisasi(rs.getInt("id_organisasi"));
                laporanDana.setBukti_dokumen(rs.getBytes("bukti_dokumen"));
                laporanDana.setDeskripsi_penggunaan(rs.getString("deskripsi_penggunaan"));
                laporanDana.setStatus_verifikasi(rs.getString("status_verifikasi"));
                laporanDana.setTotal_Pengeluaran(rs.getBigDecimal("total_pengeluaran").intValue());

                Timestamp tglPengajuan = rs.getTimestamp("tgl_pengajuan");
                Timestamp tglVerifikasi = rs.getTimestamp("tgl_verifikasi");

                if (tglPengajuan != null) laporanDana.setTgl_pengajuan(tglPengajuan.toLocalDateTime());
                if (tglVerifikasi != null) laporanDana.setTgl_verifikasi(tglVerifikasi.toLocalDateTime());

                list.add(laporanDana);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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

            return rowsInserted;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int VerifikasiTerima(int id_laporan) {
        String query = "UPDATE laporan_dana SET status_verifikasi = 'disetujui', tgl_verifikasi = NOW() WHERE id_laporan = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id_laporan);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int VerifikasiTolak(int id_laporan) {
        String query = "UPDATE laporan_dana SET status_verifikasi = 'ditolak', tgl_verifikasi = NOW() WHERE id_laporan = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id_laporan);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<LaporanDana> GetAllLaporanDanaAktif(String keyword) {

        String baseQuery =
            "SELECT ld.*, k.judul_kampanye AS nama_kampanye, o.nama_organisasi AS nama_organisasi " +
            "FROM laporan_dana ld " +
            "JOIN kampanye k ON ld.id_kampanye = k.id_kampanye " +
            "JOIN organisasi o ON ld.id_organisasi = o.id_organisasi " +
            "WHERE ld.status_verifikasi = 'menunggu' OR ld.status_verifikasi = 'aktif'";

        // Tambahkan filter jika keyword ada
        if (keyword != null && !keyword.trim().isEmpty()) {
            baseQuery += " AND LOWER(k.judul_kampanye) LIKE ?";
        }

        baseQuery += " ORDER BY ld.tgl_pengajuan ASC";

        List<LaporanDana> list = new ArrayList<>();

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(baseQuery)) {

            if (keyword != null && !keyword.trim().isEmpty()) {
                stmt.setString(1, "%" + keyword.toLowerCase() + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LaporanDana ld = new LaporanDana();

                ld.setId_laporan(rs.getInt("id_laporan"));
                ld.setId_kampanye(rs.getInt("id_kampanye"));
                ld.setId_organisasi(rs.getInt("id_organisasi"));
                ld.setDeskripsi_penggunaan(rs.getString("deskripsi_penggunaan"));
                ld.setStatus_verifikasi(rs.getString("status_verifikasi"));
                ld.setTotal_Pengeluaran(rs.getBigDecimal("total_pengeluaran").intValue());

                Timestamp tglP = rs.getTimestamp("tgl_pengajuan");
                Timestamp tglV = rs.getTimestamp("tgl_verifikasi");

                if (tglP != null) ld.setTgl_pengajuan(tglP.toLocalDateTime());
                if (tglV != null) ld.setTgl_verifikasi(tglV.toLocalDateTime());

                ld.setNama_kampanye(rs.getString("nama_kampanye"));
                ld.setNama_organisasi(rs.getString("nama_organisasi"));

                list.add(ld);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }



}
