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
}
