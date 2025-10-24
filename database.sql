CREATE TABLE Akun (
    id_akun INT PRIMARY KEY AUTO_INCREMENT,
    nama VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'donatur', 'organisasi') NOT NULL
);


CREATE TABLE Admin (
    id_admin INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    level_akses VARCHAR(50),
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Organisasi (
    id_organisasi INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    nama_organisasi VARCHAR(150) NOT NULL,
    deskripsi_organisasi TEXT,
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Donatur (
    id_donatur INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    total_donasi DECIMAL(15,2) DEFAULT 0,
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Kampanye (
    id_kampanye INT PRIMARY KEY AUTO_INCREMENT,
    id_akun INT NOT NULL,
    judul_kampanye VARCHAR(150) NOT NULL,
    deskripsi_kampanye TEXT,
    target_dana DECIMAL(15,2) NOT NULL,
    batas_waktu DATE,
    -- UBAH BARIS DI BAWAH INI
    status_kampanye ENUM('aktif', 'nonaktif', 'selesai', 'menunggu') DEFAULT 'menunggu',
    gambar_kampanye VARCHAR(255),
    FOREIGN KEY (id_akun) REFERENCES Akun(id_akun)
        ON DELETE CASCADE ON UPDATE CASCADE
    );

CREATE TABLE Donasi (
    id_donasi INT PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE Pencairan_Dana (
    id_pencairan INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    id_organisasi INT NOT NULL,
    jumlah_dana DECIMAL(15,2) NOT NULL,
    tanggal_pengajuan DATETIME DEFAULT CURRENT_TIMESTAMP,
    status_pencairan ENUM('diajukan', 'disetujui', 'ditolak') DEFAULT 'diajukan',
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_organisasi) REFERENCES Organisasi(id_organisasi)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Laporan_Dana (
    id_laporan INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    id_organisasi INT NOT NULL,
    total_pengeluaran DECIMAL(15,2),
    bukti_dokumen VARCHAR(255),
    deskripsi_penggunaan TEXT,
    status_verifikasi ENUM('menunggu', 'disetujui', 'ditolak') DEFAULT 'menunggu',
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_organisasi) REFERENCES Organisasi(id_organisasi)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Komentar (
    id_komentar INT PRIMARY KEY AUTO_INCREMENT,
    id_donatur INT NOT NULL,
    id_kampanye INT NOT NULL,
    isi_komentar TEXT NOT NULL,
    tanggal_komentar DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_donatur) REFERENCES Donatur(id_donatur)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE
);

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

-- tambahkan alter di tabel akun
ALTER TABLE akun
ADD COLUMN no_hp VARCHAR(15) NULL AFTER email;


-- 1. Akun Admin 1
INSERT INTO Akun (nama, username, email, no_hp, password, role) VALUES
('Nick Wilsan', 'nickadmin', 'wilsannick55@gmail.com', '081249730818', PASSWORD('nick123'), 'admin');

-- 2. Akun Admin 2
INSERT INTO Akun (nama, username, email, no_hp, password, role) VALUES
('Muhammad Zaqy', 'zaqyadmin', 'zaqy@gmail.com', '0812567489', PASSWORD('zaqy123'), 'admin');

-- 3. Akun Organisasi 1
INSERT INTO Akun (nama, username, email, no_hp, password, role) VALUES
('Yayasan Peduli Anak', 'ypa_official', 'ypa@gmail.com', '081281803794', PASSWORD('ypa123'), 'organisasi');

-- 4. Akun Organisasi 2
INSERT INTO Akun (nama, username, email, no_hp, password, role) VALUES
('Panti Jompo Kasih', 'pjk_official', 'pjk@gmail.com', '082200764422', PASSWORD('pjk123'), 'organisasi');

-- 5. Akun Donatur 1
INSERT INTO Akun (nama, username, email, no_hp, password, role) VALUES
('Ihsan Ramadhan', 'ihsan', 'ihsan.rmd@gmail.com', '082284670022', PASSWORD('ihsan123'), 'donatur');

-- 6. Akun Donatur 2
INSERT INTO Akun (nama, username, email, no_hp, password, role) VALUES
('Metteu AK Saragih', 'metteu', 'metteusaragih@gmail.com', '082384294702', PASSWORD('metteu123'), 'donatur');

-- Insert sampai akun admin dulu

-- Mengisi Tabel Organisasi (id_akun = 2 dan 3)
INSERT INTO Organisasi (id_akun, nama_organisasi, deskripsi_organisasi) VALUES
(2, 'Yayasan Peduli Anak Sejahtera', 'Fokus pada pendidikan dan kesehatan anak yatim.'),
(3, 'Panti Jompo Kasih Ibu', 'Menyediakan tempat tinggal dan perawatan bagi lansia.');

-- Mengisi Tabel Donatur (id_akun = 4 dan 5)
INSERT INTO Donatur (id_akun, total_donasi) VALUES
(4, 0.00), -- Donatur 1
(5, 0.00); -- Donatur 2


-- Kampanye 1 (Dibuat oleh Organisasi dengan id_akun = 2)
INSERT INTO Kampanye (id_akun, judul_kampanye, deskripsi_kampanye, target_dana, batas_waktu, status_kampanye, gambar_kampanye) VALUES
(2, 'Renovasi Sekolah Anak Yatim', 'Mengumpulkan dana untuk merenovasi gedung sekolah yang sudah tua dan tidak layak.', 50000000.00, '2025-12-31', 'aktif', 'sekolah_renovasi.jpg');

-- Kampanye 2 (Dibuat oleh Organisasi dengan id_akun = 3)
INSERT INTO Kampanye (id_akun, judul_kampanye, deskripsi_kampanye, target_dana, batas_waktu, status_kampanye, gambar_kampanye) VALUES
(3, 'Kebutuhan Harian Lansia', 'Penggalangan dana rutin untuk makanan, obat-obatan, dan popok lansia di panti.', 25000000.00, '2025-11-30', 'aktif', 'lansia_kebutuhan.png');

-- Kampanye 3 (Dibuat oleh Organisasi dengan id_akun = 2)
INSERT INTO Kampanye (id_akun, judul_kampanye, deskripsi_kampanye, target_dana, batas_waktu, status_kampanye, gambar_kampanye) VALUES
(2, 'Bantuan Beasiswa Anak Berprestasi', 'Dana untuk memberikan beasiswa penuh bagi 10 anak kurang mampu.', 15000000.00, '2025-10-31', 'selesai', 'beasiswa_anak.jpg');


-- Donasi untuk Kampanye 1 (id_kampanye=1, id_donatur=1)
INSERT INTO Donasi (id_donatur, id_kampanye, nominal_donasi, metode_pembayaran, tanggal_donasi, anonim, status_pembayaran) VALUES
(1, 1, 500000.00, 'Transfer Bank', NOW() - INTERVAL 5 HOUR, FALSE, 'berhasil'),
(2, 1, 200000.00, 'E-Wallet', NOW() - INTERVAL 3 HOUR, TRUE, 'berhasil');

-- Donasi untuk Kampanye 2 (id_kampanye=2, id_donatur=1)
INSERT INTO Donasi (id_donatur, id_kampanye, nominal_donasi, metode_pembayaran, tanggal_donasi, anonim, status_pembayaran) VALUES
(1, 2, 1000000.00, 'Transfer Bank', NOW() - INTERVAL 1 HOUR, FALSE, 'berhasil');

-- Donasi untuk Kampanye 3 (id_kampanye=3, id_donatur=2)
INSERT INTO Donasi (id_donatur, id_kampanye, nominal_donasi, metode_pembayaran, tanggal_donasi, anonim, status_pembayaran) VALUES
(2, 3, 50000.00, 'Kartu Kredit', NOW() - INTERVAL 2 DAY, TRUE, 'berhasil');

-- Donasi yang masih pending
INSERT INTO Donasi (id_donatur, id_kampanye, nominal_donasi, metode_pembayaran, tanggal_donasi, anonim, status_pembayaran) VALUES
(1, 1, 150000.00, 'E-Wallet', NOW(), FALSE, 'pending');


-- Donatur 1 (id_donatur=1): 500000 + 1000000 = 1500000
UPDATE Donatur
SET total_donasi = (
    SELECT SUM(nominal_donasi) 
    FROM Donasi 
    WHERE id_donatur = 1 AND status_pembayaran = 'berhasil'
)
WHERE id_donatur = 1;

-- Donatur 2 (id_donatur=2): 200000 + 50000 = 250000
UPDATE Donatur
SET total_donasi = (
    SELECT SUM(nominal_donasi) 
    FROM Donasi 
    WHERE id_donatur = 2 AND status_pembayaran = 'berhasil'
)
WHERE id_donatur = 2;


-- Komentar Donatur 1 (id_donatur=1) di Kampanye 1 (id_kampanye=1)
INSERT INTO Komentar (id_donatur, id_kampanye, isi_komentar) VALUES
(1, 1, 'Semoga renovasi berjalan lancar dan anak-anak bisa belajar dengan nyaman!'),
(2, 1, 'Sedikit rezeki dari saya, semoga bermanfaat untuk renovasi.');


-- id_organisasi Organisasi 1 adalah 1 (id_akun 2)
INSERT INTO Pencairan_Dana (id_kampanye, id_organisasi, jumlah_dana, status_pencairan) VALUES
(1, 1, 1000000.00, 'disetujui'),
(2, 2, 500000.00, 'diajukan');


-- id_organisasi Organisasi 1 adalah 1
INSERT INTO Laporan_Dana (id_kampanye, id_organisasi, total_pengeluaran, bukti_dokumen, deskripsi_penggunaan, status_verifikasi) VALUES
(1, 1, 950000.00, 'bukti_pembelian_cat.pdf', 'Digunakan untuk pembelian material awal renovasi seperti cat dan semen.', 'disetujui');


-- Leaderboard Kampanye 1 (id_kampanye=1). Donasi berhasil: D1=500rb, D2=200rb
INSERT INTO Leaderboard (id_kampanye, id_donatur, total_donasi, peringkat) VALUES
(1, 1, 500000.00, 1),
(1, 2, 200000.00, 2);
