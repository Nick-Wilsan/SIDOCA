package com.sidoca.Models.DataBaseClass;
import jakarta.persistence.*;

@Entity
@Table(name = "akun")
public class Akun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_akun;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    public Akun() {}

    public Akun(String nama, String username, String email, String password, String role) {
        this.nama = nama;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Integer getId_akun() { return id_akun; }
    public void setId_akun(Integer id_akun) { this.id_akun = id_akun; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
