package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.KampanyeGambar;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class KampanyeGambarModel extends BaseModel {

    public boolean saveGambar(KampanyeGambar gambar) {
        String query = "INSERT INTO Kampanye_Gambar (id_kampanye, url_gambar) VALUES (?, ?)";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, gambar.getId_kampanye());
            stmt.setString(2, gambar.getUrl_gambar());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}