package com.sidoca.Models;

// untuk mengambil class User.java
import com.sidoca.Models.DataBaseClass.Akun;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class ContohModel extends BaseModel {

    public Akun ContohgetByUsername(String username) {
        String query = "SELECT * FROM akun WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameter (setara dengan bind_param("s", $username))
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            // Jika data ditemukan, buat objek User
            if (rs.next()) {
                Akun akun = new Akun();
                akun.setId_akun(rs.getInt("id"));
                akun.setUsername(rs.getString("username"));
                akun.setEmail(rs.getString("email"));
                akun.setPassword(rs.getString("password"));
                return akun;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

// ada taro di model
// class User {
//     private int id;
//     private String username;
//     private String email;
//     private String password;

//     // Getter & Setter
//     public int getId() {
//         return id;
//     }
//     public void setId(int id) {
//         this.id = id;
//     }
//     public String getUsername() {
//         return username;
//     }
//     public void setUsername(String username) {
//         this.username = username;
//     }
//     public String getEmail() {
//         return email;
//     }
//     public void setEmail(String email) {
//         this.email = email;
//     }
//     public String getPassword() {
//         return password;
//     }
//     public void setPassword(String password) {
//         this.password = password;
//     }
// }