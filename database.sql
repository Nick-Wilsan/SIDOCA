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

ALTER TABLE Akun
ADD COLUMN alamat TEXT NULL AFTER no_hp,
ADD COLUMN photo_profile MEDIUMBLOB NULL AFTER alamat;

-- HAPUS KOLOM GAMBAR KAMPANYE LAMA DARI TABEL KAMPANYE
ALTER TABLE Kampanye DROP COLUMN gambar_kampanye;

-- BUAT TABEL BARU UNTUK MENYIMPAN GAMBAR KAMPANYE
CREATE TABLE Kampanye_Gambar (
    id_gambar INT PRIMARY KEY AUTO_INCREMENT,
    id_kampanye INT NOT NULL,
    url_gambar VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_kampanye) REFERENCES Kampanye(id_kampanye)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tambahkan Status Akun dan Tanggal Registrasi
ALTER TABLE Akun
ADD COLUMN tgl_registrasi DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN status ENUM('aktif', 'nonaktif') NOT NULL DEFAULT 'aktif';

ALTER TABLE Kampanye
ADD COLUMN alasan_penolakan TEXT NULL DEFAULT NULL AFTER status_kampanye;

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

-- Mengisi Tabel Organisasi (id_akun = 2 dan 3)
INSERT INTO Organisasi (id_akun, nama_organisasi, deskripsi_organisasi) VALUES
(3, 'Yayasan Peduli Anak Sejahtera', 'Fokus pada pendidikan dan kesehatan anak yatim.'),
(4, 'Panti Jompo Kasih Ibu', 'Menyediakan tempat tinggal dan perawatan bagi lansia.');

-- Input Semua
