package com.sidoca.Models;
import com.sidoca.Models.DataBaseClass.Akun;    
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        // UBAH QUERY: Tambahkan JOIN ke tabel Profil untuk ambil photo_profile
        String query = "SELECT a.*, p.photo_profile " + 
                       "FROM Akun a " +
                       "LEFT JOIN Profil p ON a.id_akun = p.id_akun " +
                       "WHERE (a.username = ? OR a.email = ?) " +
                       "AND a.password = PASSWORD(?) " +
                       "AND a.status = 'aktif'";
                       
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, identifier); 
            stmt.setString(2, identifier); 
            stmt.setString(3, password);   

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
                
                // --- TAMBAHKAN INI ---
                // Ambil kolom photo_profile dari hasil join
                akun.setPhotoProfile(rs.getString("photo_profile")); 
                // ---------------------
                
                return akun;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Akun findByEmail(String email) {
        String query = "SELECT * FROM akun WHERE email = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Akun akun = new Akun();
                akun.setId_akun(rs.getInt("id_akun"));
                akun.setNama(rs.getString("nama"));
                akun.setUsername(rs.getString("username"));
                akun.setEmail(rs.getString("email"));
                akun.setRole(rs.getString("role"));
                return akun;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        String query = "UPDATE akun SET password = PASSWORD(?) WHERE email = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int saveAkun(Akun akun) {
        // Tambahkan tgl_registrasi dan status ke dalam query
        String query = "INSERT INTO akun (nama, username, email, password, role, tgl_registrasi, status) VALUES (?, ?, ?, PASSWORD(?), ?, NOW(), 'aktif')";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, akun.getNama());
            stmt.setString(2, akun.getUsername());
            stmt.setString(3, akun.getEmail());
            stmt.setString(4, akun.getPassword());
            stmt.setString(5, akun.getRole());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Akun> getAllAkun(String keyword, String role, String status, int loggedInAdminId) {
        List<Akun> akunList = new ArrayList<>();
        // Ambil juga kolom tgl_registrasi dan status
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT id_akun, nama, email, role, tgl_registrasi, status FROM akun WHERE id_akun != ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(loggedInAdminId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            queryBuilder.append(" AND (nama LIKE ? OR email LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (role != null && !role.trim().isEmpty()) {
            queryBuilder.append(" AND role = ? ");
            params.add(role);
        }

        if (status != null && !status.trim().isEmpty()) {
            queryBuilder.append(" AND status = ? ");
            params.add(status);
        }

        queryBuilder.append(" ORDER BY id_akun DESC");

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Akun akun = new Akun();
                akun.setId_akun(rs.getInt("id_akun"));
                akun.setNama(rs.getString("nama"));
                akun.setEmail(rs.getString("email"));
                akun.setRole(rs.getString("role"));
                akun.setTgl_registrasi(rs.getTimestamp("tgl_registrasi"));
                akun.setStatus(rs.getString("status"));
                akunList.add(akun);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return akunList;
    }
    
    // Metode baru untuk mengubah status
    public boolean ubahStatusAkun(int idAkun, String status) {
        String query = "UPDATE Akun SET status = ? WHERE id_akun = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, idAkun);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ubahPeranMenjadiAdmin(int idAkun) {
        String updateRoleQuery = "UPDATE Akun SET role = 'admin' WHERE id_akun = ?";
        String deleteDonaturQuery = "DELETE FROM Donatur WHERE id_akun = ?";
        String insertAdminQuery = "INSERT INTO Admin (id_akun) VALUES (?)";
    
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
    
            // Hapus dari tabel Donatur
            try (PreparedStatement stmt = conn.prepareStatement(deleteDonaturQuery)) {
                stmt.setInt(1, idAkun);
                stmt.executeUpdate();
            }
    
            // Masukkan ke tabel Admin
            try (PreparedStatement stmt = conn.prepareStatement(insertAdminQuery)) {
                stmt.setInt(1, idAkun);
                stmt.executeUpdate();
            }
    
            // Ubah peran di tabel Akun
            try (PreparedStatement stmt = conn.prepareStatement(updateRoleQuery)) {
                stmt.setInt(1, idAkun);
                stmt.executeUpdate();
            }
    
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean ubahPeranMenjadiDonatur(int idAkun) {
        String updateRoleQuery = "UPDATE Akun SET role = 'donatur' WHERE id_akun = ?";
        String deleteAdminQuery = "DELETE FROM Admin WHERE id_akun = ?";
        String insertDonaturQuery = "INSERT INTO Donatur (id_akun) VALUES (?)";
    
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
    
            // Hapus dari tabel Admin
            try (PreparedStatement stmt = conn.prepareStatement(deleteAdminQuery)) {
                stmt.setInt(1, idAkun);
                stmt.executeUpdate();
            }
    
            // Masukkan ke tabel Donatur
            try (PreparedStatement stmt = conn.prepareStatement(insertDonaturQuery)) {
                stmt.setInt(1, idAkun);
                stmt.executeUpdate();
            }
    
            // Ubah peran di tabel Akun
            try (PreparedStatement stmt = conn.prepareStatement(updateRoleQuery)) {
                stmt.setInt(1, idAkun);
                stmt.executeUpdate();
            }
    
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean deleteAkun(int idAkun) {
        String query = "DELETE FROM Akun WHERE id_akun = ?";
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idAkun);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getAdminLevel(int idAkun) {
        String query = "SELECT level_akses FROM Admin WHERE id_akun = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idAkun);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("level_akses");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}