package com.sidoca.Models.DataBaseClass;
import java.sql.Timestamp;

// ini representasi dari tabel akun di database
public class Akun {
    private int id_akun;
    private String nama;
    private String username;
    private String email;
    private String no_hp;
    private String password;
    private String role;
    private Timestamp tgl_registrasi;
    private String status;

    // Getter & Setter 
    public int getId_akun() {
        return id_akun;
    }
    public void setId_akun(int id) {
        this.id_akun = id;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNo_HP() {
        return no_hp;
    }
    public void setNo_HP(String no_hp) {
        this.no_hp = no_hp;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public Timestamp getTgl_registrasi() {
        return tgl_registrasi;
    }
    public void setTgl_registrasi(Timestamp tgl_registrasi) {
        this.tgl_registrasi = tgl_registrasi;
    }

    // Getter & Setter untuk status
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
