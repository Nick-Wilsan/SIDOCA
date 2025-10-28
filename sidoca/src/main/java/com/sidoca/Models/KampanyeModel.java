package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.Kampanye;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.sidoca.Models.DTO.StatusVerifikasiDTO;
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
import com.sidoca.Models.DTO.KampanyeAdminDTO;
import java.time.LocalDate;

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

        String komentarQuery = "SELECT a.nama, kom.isi_komentar, kom.tanggal_komentar " +
                            "FROM Komentar kom " +
                            "JOIN Akun a ON kom.id_akun = a.id_akun " +
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

    public void tambahKomentar(int idKampanye, int idAkun, String isiKomentar) {
    String query = "INSERT INTO Komentar (id_kampanye, id_akun, isi_komentar, tanggal_komentar) VALUES (?, ?, ?, NOW())";
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, idKampanye);
        stmt.setInt(2, idAkun);
        stmt.setString(3, isiKomentar);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public List<KampanyeAktifDTO> getKampanyeAktif(String keyword, String urutkan) {
        updateStatusKampanyeOtomatis();
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

    public void updateStatusKampanyeOtomatis() {
        String query = "UPDATE Kampanye SET status_kampanye = 'nonaktif' WHERE batas_waktu < CURDATE() AND status_kampanye = 'aktif'";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Sebaiknya gunakan logger
        }
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

    public List<StatusVerifikasiDTO> getStatusVerifikasiForOrganisasi(int idAkun, String keyword, String jenis, String status) {
        List<StatusVerifikasiDTO> resultList = new ArrayList<>();
        
        // Query untuk mengambil data dari 3 tabel dan menyatukannya
        String query = 
            // 1. Data dari Kampanye Baru
            "SELECT judul_kampanye AS nama_kampanye, 'Kampanye Baru' AS jenis_pengajuan, tgl_pengajuan, tgl_verifikasi, " +
            "CASE " +
            "    WHEN status_kampanye = 'menunggu' THEN 'Menunggu Verifikasi' " +
            "    WHEN status_kampanye = 'aktif' THEN 'Terverifikasi' " +
            "    WHEN status_kampanye = 'nonaktif' THEN 'Ditolak' " +
            "    ELSE status_kampanye " +
            "END AS status_verifikasi " +
            "FROM Kampanye WHERE id_akun = ? " +

            "UNION ALL " +

            // 2. Data dari Pencairan Dana
            "SELECT k.judul_kampanye, 'Pencairan Dana' AS jenis_pengajuan, pd.tanggal_pengajuan, pd.tgl_verifikasi, " +
            "CASE " +
            "    WHEN pd.status_pencairan = 'diajukan' THEN 'Menunggu Verifikasi' " +
            "    WHEN pd.status_pencairan = 'disetujui' THEN 'Terverifikasi' " +
            "    WHEN pd.status_pencairan = 'ditolak' THEN 'Ditolak' " +
            "    ELSE pd.status_pencairan " +
            "END AS status_verifikasi " +
            "FROM Pencairan_Dana pd JOIN Kampanye k ON pd.id_kampanye = k.id_kampanye WHERE k.id_akun = ? " +

            "UNION ALL " +

            // 3. Data dari Laporan Dana
            "SELECT k.judul_kampanye, 'Laporan Penggunaan Dana' AS jenis_pengajuan, ld.tgl_pengajuan, ld.tgl_verifikasi, " +
            "CASE " +
            "    WHEN ld.status_verifikasi = 'menunggu' THEN 'Menunggu Verifikasi' " +
            "    WHEN ld.status_verifikasi = 'disetujui' THEN 'Terverifikasi' " +
            "    WHEN ld.status_verifikasi = 'ditolak' THEN 'Ditolak' " +
            "    ELSE ld.status_verifikasi " +
            "END AS status_verifikasi " +
            "FROM Laporan_Dana ld JOIN Kampanye k ON ld.id_kampanye = k.id_kampanye WHERE k.id_akun = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idAkun);
            stmt.setInt(2, idAkun);
            stmt.setInt(3, idAkun);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StatusVerifikasiDTO dto = new StatusVerifikasiDTO();
                dto.setNamaKampanye(rs.getString("nama_kampanye"));
                dto.setJenisPengajuan(rs.getString("jenis_pengajuan"));
                dto.setTanggalPengajuan(rs.getTimestamp("tgl_pengajuan"));
                dto.setTanggalVerifikasi(rs.getTimestamp("tgl_verifikasi"));
                dto.setStatusVerifikasi(rs.getString("status_verifikasi"));
                resultList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<StatusVerifikasiDTO> filteredList = new ArrayList<>();
        for (StatusVerifikasiDTO item : resultList) {
            boolean match = true;
            if (keyword != null && !keyword.isEmpty() && !item.getNamaKampanye().toLowerCase().contains(keyword.toLowerCase())) {
                match = false;
            }
            if (jenis != null && !jenis.isEmpty() && !item.getJenisPengajuan().equals(jenis)) {
                match = false;
            }
            if (status != null && !status.isEmpty() && !item.getStatusVerifikasi().equals(status)) {
                match = false;
            }
            if (match) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    public List<KampanyeAdminDTO> getAllKampanyeForAdmin(String keyword, String status) {
        List<KampanyeAdminDTO> kampanyeList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT k.id_kampanye, k.judul_kampanye, o.nama_organisasi, k.status_kampanye " +
            "FROM Kampanye k " +
            "JOIN Akun a ON k.id_akun = a.id_akun " +
            "JOIN Organisasi o ON a.id_akun = o.id_akun WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            queryBuilder.append(" AND (k.judul_kampanye LIKE ? OR o.nama_organisasi LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            queryBuilder.append(" AND k.status_kampanye = ?");
            params.add(status);
        }

        queryBuilder.append(" ORDER BY k.id_kampanye DESC");

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                KampanyeAdminDTO dto = new KampanyeAdminDTO();
                dto.setId_kampanye(rs.getInt("id_kampanye"));
                dto.setJudul_kampanye(rs.getString("judul_kampanye"));
                dto.setNama_organisasi(rs.getString("nama_organisasi"));
                dto.setStatus_kampanye(rs.getString("status_kampanye"));
                kampanyeList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kampanyeList;
    }

    public boolean ubahStatusKampanyeAdmin(int idKampanye, String newStatus) {
        if ("aktif".equals(newStatus)) {
            String checkDateQuery = "SELECT batas_waktu FROM Kampanye WHERE id_kampanye = ?";
            try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(checkDateQuery)) {
                stmt.setInt(1, idKampanye);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Date batasWaktu = rs.getDate("batas_waktu");
                    if (batasWaktu != null && batasWaktu.toLocalDate().isBefore(LocalDate.now())) {
                        System.out.println("Gagal mengaktifkan: Kampanye telah berakhir.");
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        String query = "UPDATE Kampanye SET status_kampanye = ? WHERE id_kampanye = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, idKampanye);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDanaTerkumpul(int idKampanye, BigDecimal jumlahDonasi) {
        String query = "UPDATE Kampanye SET dana_terkumpul = dana_terkumpul + ? WHERE id_kampanye = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, jumlahDonasi);
            stmt.setInt(2, idKampanye);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public BigDecimal getDanaTerkumpul(int idKampanye) {
        String query = "SELECT COALESCE(SUM(nominal_donasi), 0) AS total_donasi FROM Donasi WHERE id_kampanye = ? AND status_pembayaran = 'berhasil'";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total_donasi");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public boolean saveDanaNonaktif(int idKampanye, BigDecimal danaTerkumpul) {
        String query = "INSERT INTO Uang_Kampanye_Nonaktif (id_kampanye_asal, jumlah_dana) VALUES (?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            stmt.setBigDecimal(2, danaTerkumpul);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteKampanye(int idKampanye) {
        String query = "DELETE FROM Kampanye WHERE id_kampanye = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idKampanye);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<StatusVerifikasiDTO> getStatusVerifikasi(int idAkun, String keyword, String jenis, String status) {
        List<StatusVerifikasiDTO> resultList = new ArrayList<>();
        
        // Query dasar untuk menggabungkan data dari tiga tabel
        String baseQuery = 
            "(SELECT id_kampanye, judul_kampanye AS nama_kampanye, 'Kampanye Baru' AS jenis_pengajuan, tgl_pengajuan, tgl_verifikasi, " +
            "CASE " +
            "    WHEN status_kampanye = 'menunggu' THEN 'Menunggu Verifikasi' " +
            "    WHEN status_kampanye = 'aktif' THEN 'Terverifikasi' " +
            "    WHEN status_kampanye = 'nonaktif' THEN 'Ditolak' " +
            "    ELSE status_kampanye " +
            "END AS status_verifikasi " +
            "FROM Kampanye WHERE id_akun = ?) " +

            "UNION ALL " +

            "(SELECT k.id_kampanye, k.judul_kampanye, 'Pencairan Dana' AS jenis_pengajuan, pd.tanggal_pengajuan, pd.tgl_verifikasi, " +
            "CASE " +
            "    WHEN pd.status_pencairan = 'diajukan' THEN 'Menunggu Verifikasi' " +
            "    WHEN pd.status_pencairan = 'disetujui' THEN 'Terverifikasi' " +
            "    WHEN pd.status_pencairan = 'ditolak' THEN 'Ditolak' " +
            "    ELSE pd.status_pencairan " +
            "END AS status_verifikasi " +
            "FROM Pencairan_Dana pd JOIN Kampanye k ON pd.id_kampanye = k.id_kampanye WHERE k.id_akun = ?) " +

            "UNION ALL " +

            "(SELECT k.id_kampanye, k.judul_kampanye, 'Laporan Penggunaan Dana' AS jenis_pengajuan, ld.tgl_pengajuan, ld.tgl_verifikasi, " +
            "CASE " +
            "    WHEN ld.status_verifikasi = 'menunggu' THEN 'Menunggu Verifikasi' " +
            "    WHEN ld.status_verifikasi = 'disetujui' THEN 'Terverifikasi' " +
            "    WHEN ld.status_verifikasi = 'ditolak' THEN 'Ditolak' " +
            "    ELSE ld.status_verifikasi " +
            "END AS status_verifikasi " +
            "FROM Laporan_Dana ld JOIN Kampanye k ON ld.id_kampanye = k.id_kampanye WHERE k.id_akun = ?)";

        // Membungkus query dasar dan menambahkan filter
        StringBuilder finalQueryBuilder = new StringBuilder("SELECT * FROM (");
        finalQueryBuilder.append(baseQuery);
        finalQueryBuilder.append(") AS combined_results WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        params.add(idAkun);
        params.add(idAkun);
        params.add(idAkun);

        if (keyword != null && !keyword.trim().isEmpty()) {
            finalQueryBuilder.append(" AND nama_kampanye LIKE ?");
            params.add("%" + keyword + "%");
        }

        if (jenis != null && !jenis.trim().isEmpty()) {
            finalQueryBuilder.append(" AND jenis_pengajuan = ?");
            params.add(jenis);
        }

        if (status != null && !status.trim().isEmpty()) {
            finalQueryBuilder.append(" AND status_verifikasi = ?");
            params.add(status);
        }

        finalQueryBuilder.append(" ORDER BY tgl_pengajuan DESC");

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(finalQueryBuilder.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StatusVerifikasiDTO dto = new StatusVerifikasiDTO();
                dto.setIdKampanye(rs.getInt("id_kampanye"));
                dto.setNamaKampanye(rs.getString("nama_kampanye"));
                dto.setJenisPengajuan(rs.getString("jenis_pengajuan"));
                dto.setTanggalPengajuan(rs.getTimestamp("tgl_pengajuan"));
                dto.setTanggalVerifikasi(rs.getTimestamp("tgl_verifikasi"));
                dto.setStatusVerifikasi(rs.getString("status_verifikasi"));
                resultList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}  