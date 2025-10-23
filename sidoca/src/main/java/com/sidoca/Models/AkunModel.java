package com.sidoca.Models;

// untuk mengambil class User.java
import com.sidoca.Models.DataBaseClass.Akun;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AkunModel extends BaseModel{

    // ngambil data user berdasarkan username akun
    public Akun getByUsername(String username) {
        String query = "SELECT * FROM akun WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameter (setara dengan bind_param("s", $username))
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            // Jika data ditemukan, buat objek User
            if (rs.next()) {
                Akun akun = new Akun();
                akun.setId_akun(rs.getInt("id_akun"));
                akun.setNama(rs.getString("nama"));
                akun.setUsername(rs.getString("username"));
                akun.setEmail(rs.getString("email"));
                akun.setPassword(rs.getString("password"));
                akun.setRole(rs.getString("role"));
                return akun;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Akun findUserForLogin(String identifier, String password) {
        // Query untuk mencari berdasarkan username ATAU email, dan membandingkan password
        // dengan hash yang disimpan di DB
        String query = "SELECT * FROM akun WHERE (username = ? OR email = ?) AND password = PASSWORD(?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameter
            stmt.setString(1, identifier); // untuk username
            stmt.setString(2, identifier); // untuk email
            stmt.setString(3, password);   // untuk password mentah

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Akun akun = new Akun();
                akun.setId_akun(rs.getInt("id_akun"));
                akun.setNama(rs.getString("nama"));
                akun.setUsername(rs.getString("username"));
                akun.setEmail(rs.getString("email"));
                akun.setPassword(rs.getString("password"));
                akun.setRole(rs.getString("role"));
                akun.setNo_HP(rs.getString("no_hp")); 
                return akun;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Mencegah SQL Injection
    public boolean saveAkun(Akun akun) {
    // Query dengan placeholder (?)
    String query = "INSERT INTO akun (nama, username, email, password, role) VALUES (?, ?, ?, ?, PASSWORD(?), ?)";
    try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

        // --- PENCEGAHAN SQL INJECTION DENGAN BINDING PARAMETER ---
        // Data pengguna diikat sebagai parameter (bukan digabungkan ke string query)
        stmt.setString(1, akun.getNama());
        stmt.setString(2, akun.getUsername());
        stmt.setString(3, akun.getEmail());
        stmt.setString(4, akun.getPassword()); // Asumsi sudah di-hash di service/controller
        stmt.setString(5, akun.getRole());

        int rowsInserted = stmt.executeUpdate();
        return rowsInserted > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        // Anda mungkin ingin menambahkan logger di sini
        return false;
    }
    }
}
