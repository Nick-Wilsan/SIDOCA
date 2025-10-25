package com.sidoca.Models;
import com.sidoca.Models.DataBaseClass.Akun;    
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    public int saveAkun(Akun akun) {
        // Query dengan placeholder (?) dan request generated keys
        String query = "INSERT INTO akun (nama, username, email, password, role) VALUES (?, ?, ?, PASSWORD(?), ?)";
        try (Connection conn = getConnection();
             // Menambahkan Statement.RETURN_GENERATED_KEYS
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, akun.getNama());
            stmt.setString(2, akun.getUsername());
            stmt.setString(3, akun.getEmail());
            stmt.setString(4, akun.getPassword()); // Pastikan password sudah di-handle dengan benar
            stmt.setString(5, akun.getRole());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                // Ambil generated keys (ID akun baru)
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Kembalikan ID akun baru
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Anda mungkin ingin menambahkan logger di sini
        }
        return -1; // Kembalikan -1 jika gagal
    }
}
