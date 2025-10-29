-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 29, 2025 at 10:00 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sidoca`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_admin` int(11) NOT NULL,
  `id_akun` int(11) NOT NULL,
  `level_akses` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_admin`, `id_akun`, `level_akses`) VALUES
(4, 1, 'Super Admin'),
(5, 12, NULL),
(6, 13, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `akun`
--

CREATE TABLE `akun` (
  `id_akun` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `no_hp` varchar(15) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `photo_profile` mediumblob DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','donatur','organisasi') NOT NULL,
  `tgl_registrasi` datetime DEFAULT current_timestamp(),
  `status` enum('aktif','nonaktif') NOT NULL DEFAULT 'aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Triggers `akun`
--
DELIMITER $$
CREATE TRIGGER `after_akun_insert` AFTER INSERT ON `akun` FOR EACH ROW BEGIN
    INSERT INTO Profil (id_akun) VALUES (NEW.id_akun);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `biaya_admin`
--

CREATE TABLE `biaya_admin` (
  `id_biaya_admin` int(11) NOT NULL,
  `id_donasi` int(11) NOT NULL,
  `id_donatur` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `jumlah_biaya` decimal(15,2) NOT NULL,
  `tanggal_biaya` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `biaya_admin`
--

INSERT INTO `biaya_admin` (`id_biaya_admin`, `id_donasi`, `id_donatur`, `id_kampanye`, `jumlah_biaya`, `tanggal_biaya`) VALUES
(1, 1, 2, 5, 250000.00, '2025-10-28 21:19:40'),
(2, 3, 2, 5, 367000.00, '2025-10-28 21:21:23'),
(4, 6, 2, 8, 300000.00, '2025-10-28 21:33:44'),
(5, 7, 2, 11, 8100000.00, '2025-10-28 21:37:30');

-- --------------------------------------------------------

--
-- Table structure for table `donasi`
--

CREATE TABLE `donasi` (
  `id_donasi` int(11) NOT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `id_donatur` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `nominal_donasi` decimal(15,2) NOT NULL,
  `metode_pembayaran` varchar(100) DEFAULT NULL,
  `tanggal_donasi` datetime DEFAULT current_timestamp(),
  `anonim` tinyint(1) DEFAULT 0,
  `status_pembayaran` enum('pending','berhasil','gagal') DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `donatur`
--

CREATE TABLE `donatur` (
  `id_donatur` int(11) NOT NULL,
  `id_akun` int(11) NOT NULL,
  `total_donasi` decimal(15,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `donatur`
--

INSERT INTO `donatur` (`id_donatur`, `id_akun`, `total_donasi`) VALUES
(2, 10, 90170000.00),
(3, 11, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `kampanye`
--

CREATE TABLE `kampanye` (
  `id_kampanye` int(11) NOT NULL,
  `id_akun` int(11) NOT NULL,
  `judul_kampanye` varchar(150) NOT NULL,
  `deskripsi_kampanye` text DEFAULT NULL,
  `target_dana` decimal(15,2) NOT NULL,
  `dana_terkumpul` decimal(15,2) NOT NULL DEFAULT 0.00,
  `batas_waktu` date DEFAULT NULL,
  `status_kampanye` enum('aktif','nonaktif','menunggu','ditolak') DEFAULT 'menunggu',
  `alasan_penolakan` text DEFAULT NULL,
  `tgl_pengajuan` datetime DEFAULT current_timestamp(),
  `tgl_verifikasi` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `kampanye_gambar`
--

CREATE TABLE `kampanye_gambar` (
  `id_gambar` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `url_gambar` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `komentar`
--

CREATE TABLE `komentar` (
  `id_komentar` int(11) NOT NULL,
  `id_akun` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `isi_komentar` text NOT NULL,
  `tanggal_komentar` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `komentar`
--

INSERT INTO `komentar` (`id_komentar`, `id_akun`, `id_kampanye`, `isi_komentar`, `tanggal_komentar`) VALUES
(1, 10, 5, 'Test 1\r\n', '2025-10-28 21:26:22'),
(2, 10, 5, '<script>alert(123)</script>\r\n', '2025-10-28 21:26:40'),
(3, 10, 5, '\' OR \'1\'=\'1', '2025-10-28 21:27:11');

-- --------------------------------------------------------

--
-- Table structure for table `laporan_dana`
--

CREATE TABLE `laporan_dana` (
  `id_laporan` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `id_organisasi` int(11) NOT NULL,
  `total_pengeluaran` decimal(15,2) DEFAULT NULL,
  `bukti_dokumen` mediumblob DEFAULT NULL,
  `deskripsi_penggunaan` text DEFAULT NULL,
  `status_verifikasi` enum('menunggu','disetujui','ditolak') DEFAULT 'menunggu',
  `tgl_pengajuan` datetime DEFAULT current_timestamp(),
  `tgl_verifikasi` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `leaderboard`
--

CREATE TABLE `leaderboard` (
  `id_leaderboard` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `id_donatur` int(11) NOT NULL,
  `total_donasi` decimal(15,2) NOT NULL,
  `peringkat` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `organisasi`
--

CREATE TABLE `organisasi` (
  `id_organisasi` int(11) NOT NULL,
  `id_akun` int(11) NOT NULL,
  `nama_organisasi` varchar(150) NOT NULL,
  `deskripsi_organisasi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `organisasi`
--

INSERT INTO `organisasi` (`id_organisasi`, `id_akun`, `nama_organisasi`, `deskripsi_organisasi`) VALUES
(1, 14, 'Yayasan Peduli Anak', 'Fokus pada pendidikan dan kesehatan anak yatim.'),
(2, 15, 'Panti Jompo Kasih', 'Menyediakan tempat tinggal dan perawatan bagi lansia.'),
(3, 16, 'Yayasan Kasih Bunda', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `pencairan_dana`
--

CREATE TABLE `pencairan_dana` (
  `id_pencairan` int(11) NOT NULL,
  `id_kampanye` int(11) NOT NULL,
  `id_organisasi` int(11) NOT NULL,
  `jumlah_dana` decimal(15,2) NOT NULL,
  `tanggal_pengajuan` datetime DEFAULT current_timestamp(),
  `status_pencairan` enum('diajukan','disetujui','ditolak') DEFAULT 'diajukan',
  `tgl_verifikasi` datetime DEFAULT NULL,
  `nama_bank` varchar(50) NOT NULL,
  `nomor_rekening` varchar(50) NOT NULL,
  `nama_pemilik_rekening` varchar(100) NOT NULL,
  `bukti_pendukung` varchar(255) NOT NULL,
  `alasan_pencairan` text DEFAULT NULL,
  `komentar_admin` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `profil`
--

CREATE TABLE `profil` (
  `id_profil` int(11) NOT NULL,
  `id_akun` int(11) NOT NULL,
  `alamat` text DEFAULT NULL,
  `photo_profile` varchar(255) DEFAULT 'default.png'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Table structure for table `uang_kampanye_nonaktif`
--

CREATE TABLE `uang_kampanye_nonaktif` (
  `id_uang_nonaktif` int(11) NOT NULL,
  `id_kampanye_asal` int(11) NOT NULL,
  `jumlah_dana` decimal(15,2) NOT NULL,
  `tanggal_penghapusan` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`),
  ADD KEY `id_akun` (`id_akun`);

--
-- Indexes for table `akun`
--
ALTER TABLE `akun`
  ADD PRIMARY KEY (`id_akun`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `no_hp` (`no_hp`);

--
-- Indexes for table `biaya_admin`
--
ALTER TABLE `biaya_admin`
  ADD PRIMARY KEY (`id_biaya_admin`),
  ADD KEY `id_donasi` (`id_donasi`),
  ADD KEY `id_donatur` (`id_donatur`),
  ADD KEY `id_kampanye` (`id_kampanye`);

--
-- Indexes for table `donasi`
--
ALTER TABLE `donasi`
  ADD PRIMARY KEY (`id_donasi`),
  ADD UNIQUE KEY `order_id` (`order_id`),
  ADD KEY `id_donatur` (`id_donatur`),
  ADD KEY `id_kampanye` (`id_kampanye`);

--
-- Indexes for table `donatur`
--
ALTER TABLE `donatur`
  ADD PRIMARY KEY (`id_donatur`),
  ADD KEY `id_akun` (`id_akun`);

--
-- Indexes for table `kampanye`
--
ALTER TABLE `kampanye`
  ADD PRIMARY KEY (`id_kampanye`),
  ADD KEY `id_akun` (`id_akun`);

--
-- Indexes for table `kampanye_gambar`
--
ALTER TABLE `kampanye_gambar`
  ADD PRIMARY KEY (`id_gambar`),
  ADD KEY `id_kampanye` (`id_kampanye`);

--
-- Indexes for table `komentar`
--
ALTER TABLE `komentar`
  ADD PRIMARY KEY (`id_komentar`),
  ADD KEY `id_akun` (`id_akun`),
  ADD KEY `id_kampanye` (`id_kampanye`);

--
-- Indexes for table `laporan_dana`
--
ALTER TABLE `laporan_dana`
  ADD PRIMARY KEY (`id_laporan`),
  ADD KEY `id_kampanye` (`id_kampanye`),
  ADD KEY `id_organisasi` (`id_organisasi`);

--
-- Indexes for table `leaderboard`
--
ALTER TABLE `leaderboard`
  ADD PRIMARY KEY (`id_leaderboard`),
  ADD KEY `id_kampanye` (`id_kampanye`),
  ADD KEY `id_donatur` (`id_donatur`);

--
-- Indexes for table `organisasi`
--
ALTER TABLE `organisasi`
  ADD PRIMARY KEY (`id_organisasi`),
  ADD KEY `id_akun` (`id_akun`);

--
-- Indexes for table `pencairan_dana`
--
ALTER TABLE `pencairan_dana`
  ADD PRIMARY KEY (`id_pencairan`),
  ADD KEY `id_kampanye` (`id_kampanye`),
  ADD KEY `id_organisasi` (`id_organisasi`);

--
-- Indexes for table `profil`
--
ALTER TABLE `profil`
  ADD PRIMARY KEY (`id_profil`),
  ADD UNIQUE KEY `id_akun` (`id_akun`);

--
-- Indexes for table `uang_kampanye_nonaktif`
--
ALTER TABLE `uang_kampanye_nonaktif`
  ADD PRIMARY KEY (`id_uang_nonaktif`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `akun`
--
ALTER TABLE `akun`
  MODIFY `id_akun` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `biaya_admin`
--
ALTER TABLE `biaya_admin`
  MODIFY `id_biaya_admin` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `donasi`
--
ALTER TABLE `donasi`
  MODIFY `id_donasi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `donatur`
--
ALTER TABLE `donatur`
  MODIFY `id_donatur` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `kampanye`
--
ALTER TABLE `kampanye`
  MODIFY `id_kampanye` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `kampanye_gambar`
--
ALTER TABLE `kampanye_gambar`
  MODIFY `id_gambar` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `komentar`
--
ALTER TABLE `komentar`
  MODIFY `id_komentar` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `laporan_dana`
--
ALTER TABLE `laporan_dana`
  MODIFY `id_laporan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `leaderboard`
--
ALTER TABLE `leaderboard`
  MODIFY `id_leaderboard` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `organisasi`
--
ALTER TABLE `organisasi`
  MODIFY `id_organisasi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `pencairan_dana`
--
ALTER TABLE `pencairan_dana`
  MODIFY `id_pencairan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `profil`
--
ALTER TABLE `profil`
  MODIFY `id_profil` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `uang_kampanye_nonaktif`
--
ALTER TABLE `uang_kampanye_nonaktif`
  MODIFY `id_uang_nonaktif` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`id_akun`) REFERENCES `akun` (`id_akun`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `biaya_admin`
--
ALTER TABLE `biaya_admin`
  ADD CONSTRAINT `biaya_admin_ibfk_1` FOREIGN KEY (`id_donasi`) REFERENCES `donasi` (`id_donasi`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `biaya_admin_ibfk_2` FOREIGN KEY (`id_donatur`) REFERENCES `donatur` (`id_donatur`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `biaya_admin_ibfk_3` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `donasi`
--
ALTER TABLE `donasi`
  ADD CONSTRAINT `donasi_ibfk_1` FOREIGN KEY (`id_donatur`) REFERENCES `donatur` (`id_donatur`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `donasi_ibfk_2` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `donatur`
--
ALTER TABLE `donatur`
  ADD CONSTRAINT `donatur_ibfk_1` FOREIGN KEY (`id_akun`) REFERENCES `akun` (`id_akun`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `kampanye`
--
ALTER TABLE `kampanye`
  ADD CONSTRAINT `kampanye_ibfk_1` FOREIGN KEY (`id_akun`) REFERENCES `akun` (`id_akun`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `kampanye_gambar`
--
ALTER TABLE `kampanye_gambar`
  ADD CONSTRAINT `kampanye_gambar_ibfk_1` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `komentar`
--
ALTER TABLE `komentar`
  ADD CONSTRAINT `komentar_ibfk_1` FOREIGN KEY (`id_akun`) REFERENCES `akun` (`id_akun`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `komentar_ibfk_2` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `laporan_dana`
--
ALTER TABLE `laporan_dana`
  ADD CONSTRAINT `laporan_dana_ibfk_1` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `laporan_dana_ibfk_2` FOREIGN KEY (`id_organisasi`) REFERENCES `organisasi` (`id_organisasi`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `leaderboard`
--
ALTER TABLE `leaderboard`
  ADD CONSTRAINT `leaderboard_ibfk_1` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `leaderboard_ibfk_2` FOREIGN KEY (`id_donatur`) REFERENCES `donatur` (`id_donatur`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `organisasi`
--
ALTER TABLE `organisasi`
  ADD CONSTRAINT `organisasi_ibfk_1` FOREIGN KEY (`id_akun`) REFERENCES `akun` (`id_akun`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `pencairan_dana`
--
ALTER TABLE `pencairan_dana`
  ADD CONSTRAINT `pencairan_dana_ibfk_1` FOREIGN KEY (`id_kampanye`) REFERENCES `kampanye` (`id_kampanye`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `pencairan_dana_ibfk_2` FOREIGN KEY (`id_organisasi`) REFERENCES `organisasi` (`id_organisasi`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `profil`
--
ALTER TABLE `profil`
  ADD CONSTRAINT `profil_ibfk_1` FOREIGN KEY (`id_akun`) REFERENCES `akun` (`id_akun`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
