-- =================================================================
-- BAGIAN 1: DEFINISI TABEL (FINAL)
-- =================================================================

-- Tabel untuk menyimpan semua jenis akun pengguna
CREATE TABLE Akun (
    id_akun INT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    no_hp VARCHAR(15) NULL UNIQUE,
    alamat TEXT NULL,
    photo_profile MEDIUMBLOB NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'donatur', 'organisasi') NOT NULL,
    tgl_registrasi DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('aktif', 'nonaktif') NOT NULL DEFAULT 'aktif'
);

-- Tabel profil yang terhubung dengan setiap akun
CREATE TABLE Profil (
    id_profil INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL UNIQUE,
    alamat TEXT NULL,
    photo_profile VARCHAR(255) DEFAULT 'default.png',
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel spesifik untuk peran Admin
CREATE TABLE Admin (
    id_admin INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    level_akses VARCHAR(50),
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel spesifik untuk peran Organisasi
CREATE TABLE Organisasi (
    id_organisasi INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    nama_organisasi VARCHAR(150) NOT NULL,
    deskripsi_organisasi TEXT,
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel spesifik untuk peran Donatur
CREATE TABLE Donatur (
    id_donatur INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    total_donasi DECIMAL(15,2) DEFAULT 0,
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk kampanye donasi
CREATE TABLE Kampanye (
    id_kampanye INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    judul_kampanye VARCHAR(150) NOT NULL,
    deskripsi_kampanye TEXT,
    target_dana DECIMAL(15,2) NOT NULL,
    dana_terkumpul DECIMAL(15,2) NOT NULL DEFAULT 0,
    batas_waktu DATE,
    status_kampanye ENUM('aktif', 'nonaktif', 'menunggu', 'ditolak') DEFAULT 'menunggu',
    alasan_penolakan TEXT NULL,
    tgl_pengajuan DATETIME DEFAULT CURRENT_TIMESTAMP,
    tgl_verifikasi DATETIME NULL DEFAULT NULL,
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk menyimpan gambar-gambar yang terkait dengan kampanye
CREATE TABLE Kampanye_Gambar (
    id_gambar INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    url_gambar VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk mencatat setiap donasi yang masuk
CREATE TABLE Donasi (
    id_donasi INT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(255) NULL UNIQUE,
    id_donatur INT NOT NULL,
    id_kampanye INT NOT NULL,
    nominal_donasi DECIMAL(15,2) NOT NULL,
    metode_pembayaran VARCHAR(100),
    tanggal_donasi DATETIME DEFAULT CURRENT_TIMESTAMP,
    anonim BOOLEAN DEFAULT FALSE,
    status_pembayaran ENUM('pending', 'berhasil', 'gagal') DEFAULT 'pending',
    FOREIGN KEY (id_donatur) REFERENCES Donatur(id_donatur)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk proses pencairan dana oleh organisasi
CREATE TABLE Pencairan_Dana (
    id_pencairan INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    id_organisasi INT NOT NULL,
    jumlah_dana DECIMAL(15,2) NOT NULL,
    tanggal_pengajuan DATETIME DEFAULT CURRENT_TIMESTAMP,
    status_pencairan ENUM('diajukan', 'disetujui', 'ditolak') DEFAULT 'diajukan',
    tgl_verifikasi DATETIME NULL DEFAULT NULL,
    nama_bank VARCHAR(50) NOT NULL,
    nomor_rekening VARCHAR(50) NOT NULL,
    nama_pemilik_rekening VARCHAR(100) NOT NULL,
    bukti_pendukung VARCHAR(255) NOT NULL,
    alasan_pencairan TEXT,
    komentar_admin TEXT NULL,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_organisasi) REFERENCES Organisasi(id_organisasi)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk laporan penggunaan dana oleh organisasi
CREATE TABLE Laporan_Dana (
    id_laporan INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    id_organisasi INT NOT NULL,
    total_pengeluaran DECIMAL(15,2),
    bukti_dokumen MEDIUMBLOB,
    deskripsi_penggunaan TEXT,
    status_verifikasi ENUM('menunggu', 'disetujui', 'ditolak') DEFAULT 'menunggu',
    tgl_pengajuan DATETIME DEFAULT CURRENT_TIMESTAMP,
    tgl_verifikasi DATETIME NULL DEFAULT NULL,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_organisasi) REFERENCES Organisasi(id_organisasi)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk komentar pada setiap kampanye
CREATE TABLE Komentar (
    id_komentar INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    id_kampanye INT NOT NULL,
    isi_komentar TEXT NOT NULL,
    tanggal_komentar DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk leaderboard donatur per kampanye
CREATE TABLE Leaderboard (
    id_leaderboard INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    id_donatur INT NOT NULL,
    total_donasi DECIMAL(15,2) NOT NULL,
    peringkat INT,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_donatur) REFERENCES Donatur(id_donatur)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk mencatat biaya administrasi per donasi
CREATE TABLE Biaya_Admin (
    id_biaya_admin INT PRIMARY KEY AUTO_INCREMENT,
    id_donasi INT NOT NULL,
    id_donatur INT NOT NULL,
    id_kampanye INT NOT NULL,
    jumlah_biaya DECIMAL(15,2) NOT NULL,
    tanggal_biaya DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_donasi) REFERENCES Donasi(id_donasi)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_donatur) REFERENCES Donatur(id_donatur)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabel untuk menyimpan dana dari kampanye yang dihapus/nonaktif
CREATE TABLE Uang_Kampanye_Nonaktif (
    id_uang_nonaktif INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye_asal INT NOT NULL,
    jumlah_dana DECIMAL(15,2) NOT NULL,
    tanggal_penghapusan DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- =================================================================
-- BAGIAN 2: DEFINISI TRIGGER
-- =================================================================

-- Trigger untuk secara otomatis membuat baris baru di tabel Profil setiap kali akun baru ditambahkan
DELIMITER $$
CREATE TRIGGER after_akun_insert
AFTER INSERT ON Akun
FOR EACH ROW
BEGIN
    INSERT INTO Profil (id_akun) VALUES (NEW.id_akun);
END$$
DELIMITER ;






INSERT INTO Akun (nama, username, email, no_hp, password, role, status, tgl_registrasi)
VALUES (
    'Nick Wilsan',
    'nickadmin',
    'wilsannick55@gmail.com',
    '081249730818',
    '*410CF83F572280AF59D55670EF5EB7DE2FCB824A',
    'admin',
    'aktif',
    '2025-10-28 20:14:00'
);

INSERT INTO Admin (id_akun, level_akses)
VALUES (
    LAST_INSERT_ID(),
    'Super Admin'
);

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_admin`, `id_akun`, `level_akses`) VALUES
(4, 1, 'Super Admin'),
(5, 12, NULL),
(6, 13, NULL);

--
-- Dumping data for table `akun`
--

INSERT INTO `akun` (`id_akun`, `nama`, `username`, `email`, `no_hp`, `alamat`, `photo_profile`, `password`, `role`, `tgl_registrasi`, `status`) VALUES
(1, 'Nick Wilsan', 'nickadmin', 'wilsannick55@gmail.com', '081249730818', NULL, NULL, '*410CF83F572280AF59D55670EF5EB7DE2FCB824A', 'admin', '2025-10-28 20:14:00', 'aktif'),
(10, 'Metteu AK Saragih', 'metteu', 'metteusaragih@gmail.com', '082384294702', NULL, NULL, '*454CC985DA762EBCE410240F6112BADBDAE68D08', 'donatur', '2025-10-28 20:59:28', 'aktif'),
(11, 'Ihsan Ramadhan', 'ihsan', 'ihsan.rmd@gmail.com', '082284670022', NULL, NULL, '*2F27231C665701921AD9C10E38D678BB6811573E', 'donatur', '2025-10-28 21:00:35', 'aktif'),
(12, 'Admin Utama', 'adminutama', 'admin1@gmail.com', '082149060138', NULL, NULL, '*01A6717B58FF5C7EAFFF6CB7C96F7428EA65FE4C', 'admin', '2025-10-28 21:01:28', 'aktif'),
(13, 'Admin Nonaktif', 'adminnonaktif', 'adminnonaktif@gmail.com', '0812567489', NULL, NULL, '*01A6717B58FF5C7EAFFF6CB7C96F7428EA65FE4C', 'admin', '2025-10-28 21:02:26', 'nonaktif'),
(14, 'Yayasan Peduli Anak', 'ypa_official', 'ypa@gmail.com', '081281803794', NULL, NULL, '*7BFA12115FD0A3E24BF512F7B0DA197BE191EED9', 'organisasi', '2025-10-28 21:04:11', 'aktif'),
(15, 'Panti Jompo Kasih', 'pjk_official', 'pjk@gmail.com', '082200764422', NULL, NULL, '*DB680C90274226C4C821882EE534C06DDF44111E', 'organisasi', '2025-10-28 21:04:51', 'aktif'),
(16, 'Yayasan Kasih Bunda', 'ykb_official', 'ykb@gmail.com', NULL, NULL, NULL, '*9C507143811A9B82EDD06B85BD7A91B4A753903F', 'organisasi', '2025-10-28 21:35:47', 'aktif');

--
-- Dumping data for table `biaya_admin`
--

INSERT INTO `biaya_admin` (`id_biaya_admin`, `id_donasi`, `id_donatur`, `id_kampanye`, `jumlah_biaya`, `tanggal_biaya`) VALUES
(1, 1, 2, 5, 250000.00, '2025-10-28 21:19:40'),
(2, 3, 2, 5, 367000.00, '2025-10-28 21:21:23'),
(4, 6, 2, 8, 300000.00, '2025-10-28 21:33:44'),
(5, 7, 2, 11, 8100000.00, '2025-10-28 21:37:30');

--
-- Dumping data for table `donasi`
--

INSERT INTO `donasi` (`id_donasi`, `order_id`, `id_donatur`, `id_kampanye`, `nominal_donasi`, `metode_pembayaran`, `tanggal_donasi`, `anonim`, `status_pembayaran`) VALUES
(1, 'SIDOCA-5-1761661161433', 2, 5, 2500000.00, 'qris', '2025-10-28 21:19:21', 0, 'berhasil'),
(2, 'SIDOCA-5-1761661223805', 2, 5, 1500000.00, 'bank_transfer', '2025-10-28 21:20:23', 1, 'pending'),
(3, 'SIDOCA-5-1761661266781', 2, 5, 3670000.00, 'bank_transfer', '2025-10-28 21:21:06', 1, 'berhasil'),
(4, 'SIDOCA-5-1761661308596', 2, 5, 50000000.00, 'echannel', '2025-10-28 21:21:48', 0, 'gagal'),
(5, 'SIDOCA-5-1761661439630', 2, 5, 830000.00, 'qris', '2025-10-28 21:23:59', 0, 'gagal'),
(6, 'SIDOCA-8-1761662007880', 2, 8, 3000000.00, 'qris', '2025-10-28 21:33:27', 0, 'berhasil'),
(7, 'SIDOCA-11-1761662233715', 2, 11, 81000000.00, 'bank_transfer', '2025-10-28 21:37:13', 0, 'berhasil');

--
-- Dumping data for table `donatur`
--

INSERT INTO `donatur` (`id_donatur`, `id_akun`, `total_donasi`) VALUES
(2, 10, 90170000.00),
(3, 11, 0.00);

--
-- Dumping data for table `kampanye`
--

INSERT INTO `kampanye` (`id_kampanye`, `id_akun`, `judul_kampanye`, `deskripsi_kampanye`, `target_dana`, `dana_terkumpul`, `batas_waktu`, `status_kampanye`, `alasan_penolakan`, `tgl_pengajuan`, `tgl_verifikasi`) VALUES
(5, 14, 'Kampanye YPA Aktif', 'Status Kampanye Aktif', 55000000.00, 3085000.00, '2026-04-30', 'aktif', '', '2025-10-28 21:15:07', '2025-10-28 21:18:01'),
(6, 14, 'Kampanye YPA nonaktif', 'Status Kampanye nonaktif', 32000000.00, 0.00, '2026-02-25', 'nonaktif', 'Bukti dukung kurang kuat', '2025-10-28 21:15:53', '2025-10-28 21:18:23'),
(7, 14, 'Kampanye YPA Menunggu', 'Status Kampanye Menunggu', 69000000.00, 0.00, '2026-07-02', 'menunggu', NULL, '2025-10-28 21:16:44', NULL),
(8, 15, 'Kampanye PJK Aktif', 'Status Kampanye Aktif', 15000000.00, 1500000.00, '2026-04-10', 'aktif', '', '2025-10-28 21:30:28', '2025-10-28 21:32:04'),
(9, 15, 'Kampanye PJK Nonaktif', 'Status Kampanye Nonaktif', 43000000.00, 0.00, '2026-05-07', 'nonaktif', 'Kurang bukti mendukung', '2025-10-28 21:31:10', '2025-10-28 21:32:15'),
(10, 15, 'Kampanye PJK Menunggu', 'Status Kampanye Menunggu', 64000000.00, 0.00, '2026-03-31', 'menunggu', NULL, '2025-10-28 21:31:40', NULL),
(11, 16, 'Kampanye YKB Aktif', 'Status Kampanye Aktif', 94000000.00, 81000000.00, '2026-02-25', 'aktif', '', '2025-10-28 21:36:36', '2025-10-28 21:36:49');

--
-- Dumping data for table `kampanye_gambar`
--

INSERT INTO `kampanye_gambar` (`id_gambar`, `id_kampanye`, `url_gambar`) VALUES
(1, 5, '/images/campaigns/1761660907140_layanan.png'),
(2, 6, '/images/campaigns/1761660953993_BLOG.png'),
(3, 7, '/images/campaigns/1761661004143_layanan.png'),
(4, 8, '/images/campaigns/1761661828550_layanan.png'),
(5, 9, '/images/campaigns/1761661870724_BLOG.png'),
(6, 10, '/images/campaigns/1761661900333_All_About_Charity.png'),
(7, 11, '/images/campaigns/1761662196902_layanan.png');

--
-- Dumping data for table `komentar`
--

INSERT INTO `komentar` (`id_komentar`, `id_akun`, `id_kampanye`, `isi_komentar`, `tanggal_komentar`) VALUES
(1, 10, 5, 'Test 1\r\n', '2025-10-28 21:26:22'),
(2, 10, 5, '<script>alert(123)</script>\r\n', '2025-10-28 21:26:40'),
(3, 10, 5, '\' OR \'1\'=\'1', '2025-10-28 21:27:11');

--
-- Dumping data for table `organisasi`
--

INSERT INTO `organisasi` (`id_organisasi`, `id_akun`, `nama_organisasi`, `deskripsi_organisasi`) VALUES
(1, 14, 'Yayasan Peduli Anak', 'Fokus pada pendidikan dan kesehatan anak yatim.'),
(2, 15, 'Panti Jompo Kasih', 'Menyediakan tempat tinggal dan perawatan bagi lansia.'),
(3, 16, 'Yayasan Kasih Bunda', NULL);

--
-- Dumping data for table `pencairan_dana`
--

INSERT INTO `pencairan_dana` (`id_pencairan`, `id_kampanye`, `id_organisasi`, `jumlah_dana`, `tanggal_pengajuan`, `status_pencairan`, `tgl_verifikasi`, `nama_bank`, `nomor_rekening`, `nama_pemilik_rekening`, `bukti_pendukung`, `alasan_pencairan`, `komentar_admin`) VALUES
(7, 5, 1, 3085000.00, '2025-10-28 21:39:03', 'disetujui', NULL, 'Mandiri', '1090022518880', 'NICK WILSAN', '/images/bukti_pencairan/1761662343116_Logo.png', 'Pengajuan Disetujui', NULL),
(8, 5, 1, 3085000.00, '2025-10-28 21:39:35', 'ditolak', '2025-10-28 21:42:33', 'Mandiri', '1090022518880', 'NICK WILSAN', '/images/bukti_pencairan/1761662375564_BLOG.png', 'Pengajuan Ditolak', 'Sudah mengajukan yang pertama'),
(9, 5, 1, 3085000.00, '2025-10-28 21:40:07', 'diajukan', '2025-10-28 21:42:29', 'Mandiri', '1090022518880', 'NICK WILSAN', '/images/bukti_pencairan/1761662407926_layanan.png', 'Pengajuan Diajukan', ''),
(10, 8, 2, 1500000.00, '2025-10-28 21:40:55', 'disetujui', NULL, 'Mandiri', '1090022518880', 'NICK WILSAN', '/images/bukti_pencairan/1761662455249_Logo_SIDOCA.png', 'Pengajuan Disetujui', NULL),
(11, 8, 2, 1500000.00, '2025-10-28 21:41:19', 'ditolak', '2025-10-28 21:42:26', 'Mandiri', '1090022518880', 'NICK WILSAN', '/images/bukti_pencairan/1761662479683_All_About_Charity.png', 'Pengajuan Ditolak', 'Sudah mengajukan yang pertama'),
(12, 8, 2, 1500000.00, '2025-10-28 21:41:56', 'diajukan', '2025-10-28 21:42:12', 'Mandiri', '1090022518880', 'NICK WILSAN', '/images/bukti_pencairan/1761662516147_image.png', 'Pengajuan Diajukan', '');

--
-- Dumping data for table `profil`
--

INSERT INTO `profil` (`id_profil`, `id_akun`, `alamat`, `photo_profile`) VALUES
(1, 1, 'Jalan Taman Borobudur Selatan no 30D', 'default.png'),
(10, 10, 'Kos Salvia', '10_metteu.jpg'),
(11, 11, 'Sigura gura', '11_images.jpeg'),
(12, 12, 'Suhat', '12_zaqy.jpg'),
(13, 13, '', 'default.png'),
(14, 14, 'Yayasan Peduli Anak, Jakarta, Indonesia', 'default.png'),
(15, 15, 'Panti Jompo Kasih, Jakarta, Indonesia', 'default.png'),
(16, 16, NULL, 'default.png');
