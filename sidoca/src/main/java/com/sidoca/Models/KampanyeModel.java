package com.sidoca.Models;

import com.sidoca.Models.DataBaseClass.Kampanye;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}