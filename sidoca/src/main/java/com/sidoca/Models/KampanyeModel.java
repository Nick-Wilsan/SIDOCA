package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.Kampanye;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sidoca.Models.DTO.KampanyeDetailDTO;
import com.sidoca.Models.DTO.KampanyeVerifikasiDTO;
import java.util.ArrayList;
import java.util.List;
import com.sidoca.Models.DTO.KampanyeAktifDTO;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.sidoca.Models.DTO.KomentarDTO;

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

        String kampanyeQuery = "SELECT k.id_kampanye, k.judul_kampanye, k.deskripsi_kampanye, k.target_dana, k.batas_waktu, k.status_kampanye, o.nama_organisasi " +
                                "FROM Kampanye k " +
                                "JOIN Akun a ON k.id_akun = a.id_akun " +
                                "JOIN Organisasi o ON a.id_akun = o.id_akun " +
                                "WHERE k.id_kampanye = ?";

        String donasiQuery = "SELECT SUM(nominal_donasi) FROM Donasi WHERE id_kampanye = ? AND status_pembayaran = 'berhasil'";
        String gambarQuery = "SELECT url_gambar FROM Kampanye_Gambar WHERE id_kampanye = ?";

        // --- QUERY KOMENTAR YANG DIPERBAIKI ---
        String komentarQuery = "SELECT a.nama, kom.isi_komentar, kom.tanggal_komentar " +
                                "FROM Komentar kom " +
                                "JOIN Donatur d ON kom.id_donatur = d.id_donatur " +
                                "JOIN Akun a ON d.id_akun = a.id_akun " +
                                "WHERE kom.id_kampanye = ? " +
                                "ORDER BY kom.tanggal_komentar DESC";

        try (Connection conn = getConnection()) {
            // 1. Ambil detail kampanye
            try (PreparedStatement stmt = conn.prepareStatement(kampanyeQuery)) {
                stmt.setInt(1, idKampanye);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    detail = new KampanyeDetailDTO();
                    detail.setId_kampanye(rs.getInt("id_kampanye"));
                    detail.setJudul_kampanye(rs.getString("judul_kampanye"));
                    detail.setDeskripsi_kampanye(rs.getString("deskripsi_kampanye"));
                    detail.setTarget_dana(rs.getBigDecimal("target_dana"));
                    detail.setBatas_waktu(rs.getDate("batas_waktu"));
                    detail.setStatus_kampanye(rs.getString("status_kampanye"));
                    detail.setNama_organisasi(rs.getString("nama_organisasi"));

                    Date batasWaktu = rs.getDate("batas_waktu");
                    if (batasWaktu != null) {
                        long sisaHari = ChronoUnit.DAYS.between(LocalDate.now(), batasWaktu.toLocalDate());
                        detail.setSisa_hari(sisaHari > 0 ? sisaHari : 0);
                    } else {
                        detail.setSisa_hari(0);
                    }
                } else {
                    return null;
                }
            }

            // 2. Ambil total dana
            try (PreparedStatement stmt = conn.prepareStatement(donasiQuery)) {
                stmt.setInt(1, idKampanye);
                ResultSet rs = stmt.executeQuery();
                BigDecimal danaTerkumpul = rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
                detail.setDana_terkumpul(danaTerkumpul == null ? BigDecimal.ZERO : danaTerkumpul);

                if (detail.getTarget_dana().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal persentase = detail.getDana_terkumpul().multiply(new BigDecimal(100)).divide(detail.getTarget_dana(), 0, RoundingMode.HALF_UP);
                    detail.setPersentase_terkumpul(persentase.intValue());
                } else {
                    detail.setPersentase_terkumpul(0);
                }
            }

            // 3. Ambil gambar
            List<String> gambarList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(gambarQuery)) {
                stmt.setInt(1, idKampanye);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    gambarList.add(rs.getString("url_gambar"));
                }
                detail.setUrl_gambar_kampanye(gambarList);
            }

            // 4. Ambil komentar
            List<KomentarDTO> komentarList = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(komentarQuery)) {
                stmt.setInt(1, idKampanye);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    KomentarDTO komentar = new KomentarDTO();
                    komentar.setNama_pengirim(rs.getString("nama")); // Menggunakan kolom 'nama'
                    komentar.setIsi_komentar(rs.getString("isi_komentar"));
                    komentar.setTanggal_komentar(rs.getTimestamp("tanggal_komentar"));
                    komentarList.add(komentar);
                }
                detail.setDaftar_komentar(komentarList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return detail;
    }

    public void tambahKomentar(int idKampanye, int idDonatur, String isiKomentar) {
        String query = "INSERT INTO Komentar (id_kampanye, id_donatur, isi_komentar, tanggal_komentar) VALUES (?, ?, ?, NOW())";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            stmt.setInt(2, idDonatur); // Menggunakan id_donatur
            stmt.setString(3, isiKomentar);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<KampanyeAktifDTO> getKampanyeAktif(String keyword, String urutkan) {
        List<KampanyeAktifDTO> kampanyeList = new ArrayList<>();
        // Query dasar untuk mengambil data kampanye yang aktif
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT " +
            "k.id_kampanye, k.judul_kampanye, o.nama_organisasi, k.target_dana, k.batas_waktu, " +
            // Subquery untuk mengambil satu URL gambar pertama
            "(SELECT url_gambar FROM Kampanye_Gambar WHERE id_kampanye = k.id_kampanye LIMIT 1) as url_gambar, " +
            // Subquery untuk menghitung total donasi yang berhasil
            "COALESCE((SELECT SUM(d.nominal_donasi) FROM Donasi d WHERE d.id_kampanye = k.id_kampanye AND d.status_pembayaran = 'berhasil'), 0) as dana_terkumpul " +
            "FROM Kampanye k " +
            "JOIN Akun a ON k.id_akun = a.id_akun " +
            "JOIN Organisasi o ON a.id_akun = o.id_akun " +
            "WHERE k.status_kampanye = 'aktif'"
        );

        // Menambahkan filter pencarian jika ada keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryBuilder.append(" AND (k.judul_kampanye LIKE ? OR o.nama_organisasi LIKE ?)");
        }

        // Menentukan kriteria pengurutan
        String orderByClause = " ORDER BY k.id_kampanye DESC"; // Default: terbaru
        if ("mendesak".equals(urutkan)) {
            orderByClause = " ORDER BY k.batas_waktu ASC";
        } else if ("paling_sedikit".equals(urutkan)) {
            // Urutkan berdasarkan persentase dana terkumpul yang paling sedikit
            orderByClause = " ORDER BY (dana_terkumpul / k.target_dana) ASC";
        }
        queryBuilder.append(orderByClause);

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            // Set parameter untuk keyword jika ada
            if (keyword != null && !keyword.trim().isEmpty()) {
                String keywordParam = "%" + keyword + "%";
                stmt.setString(1, keywordParam);
                stmt.setString(2, keywordParam);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                KampanyeAktifDTO dto = new KampanyeAktifDTO();
                dto.setId_kampanye(rs.getInt("id_kampanye"));
                dto.setJudul_kampanye(rs.getString("judul_kampanye"));
                dto.setNama_organisasi(rs.getString("nama_organisasi"));
                dto.setTarget_dana(rs.getBigDecimal("target_dana"));
                dto.setDana_terkumpul(rs.getBigDecimal("dana_terkumpul"));
                dto.setBatas_waktu(rs.getDate("batas_waktu"));
                dto.setUrl_gambar(rs.getString("url_gambar"));

                // Menghitung sisa hari
                Date batasWaktu = rs.getDate("batas_waktu");
                if (batasWaktu != null) {
                    long sisaHari = ChronoUnit.DAYS.between(LocalDate.now(), batasWaktu.toLocalDate());
                    dto.setSisa_hari(sisaHari > 0 ? sisaHari : 0);
                } else {
                    dto.setSisa_hari(0);
                }

                // Menghitung persentase dana terkumpul
                BigDecimal target = dto.getTarget_dana();
                BigDecimal terkumpul = dto.getDana_terkumpul();
                if (target != null && target.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal persentase = terkumpul.multiply(new BigDecimal(100)).divide(target, 0, RoundingMode.HALF_UP);
                    dto.setPersentase_terkumpul(persentase.intValue());
                } else {
                    dto.setPersentase_terkumpul(0);
                }

                kampanyeList.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Sebaiknya gunakan logger di aplikasi production
        }

        return kampanyeList;
    }

    public boolean updateStatusKampanye(int idKampanye, String newStatus, String alasan) {
        String query = "UPDATE Kampanye SET status_kampanye = ?, alasan_penolakan = ? WHERE id_kampanye = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, alasan); // Bisa null jika disetujui
            stmt.setInt(3, idKampanye);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Sebaiknya gunakan logger
            return false;
        }
    }
}  