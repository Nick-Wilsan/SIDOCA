package com.sidoca.controllers;

import com.sidoca.Models.TestAkunModel;
import com.sidoca.Models.DataBaseClass.Akun;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
public class AuthController {

    @Autowired
    private TestAkunModel akunModel;

    @GetMapping("/")
    public String loginPage(Model model, HttpSession session, @RequestParam(value="error", required = false) String error){
        // Cek jika user sudah login, redirect ke dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("error", "Username/email atau password salah");
        }
    
        model.addAttribute("akun", new Akun());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        // Cek jika user sudah login, redirect ke dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("akunBaru", new Akun());
        return "register";
    }

    // Proses login
    @PostMapping("/login")
    public String loginProcess(@RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes ra, HttpSession session) {

            // Validasi Input
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                ra.addFlashAttribute("error", "Username dan password harus diisi");
                return "redirect:/";
            }

            // Cari akun di database
            Akun user = akunModel.findByUsernameOrEmail(username.trim());

            if (user != null) {
                // Verifikasi Password
                if (user.getPassword().equals(password.trim())) {
                    // Login berhasil - set session
                    session.setAttribute("user", user);
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("role", user.getRole());
                    session.setAttribute("userId", user.getId_akun());

                    // Set session timeout (30 menit)
                    session.setMaxInactiveInterval(30 * 60);

                    ra.addFlashAttribute("success", "Login berhasil! Selamat Datang " + user.getNama());
                    return "redirect:/dashboard";
                } 
            }

            // Login Gagal
            ra.addFlashAttribute("error", "Username/email atau password salah");
            return "redirect:/";
        }
    
    @GetMapping("/dashboard")
    public String dashboardPage(Model model, HttpSession session) {
        // Cek jika user belum login, redirect ke login
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        // Menambahkan data user
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));

        return "dashboard";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        // Invalidate Session
        session.invalidate();
        ra.addFlashAttribute("success", "Logout berhasil");
        return "redirect:/";
    }
    
    // POST: Memproses data Registrasi
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("akunBaru") Akun akun, RedirectAttributes ra, HttpSession session) {
        
        // Cek jika user sudah login, redirect ke dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }

        // Mencegah XXS
        akun.setUsername(akun.getUsername().trim());
        akun.setEmail(akun.getEmail().trim());
        akun.setNama(akun.getUsername().trim()); 
        akun.setRole(akun.getRole().trim());
        
        // Mencegah SQL Injection
        boolean success = akunModel.saveAkun(akun);
        
        if (success) {
            ra.addFlashAttribute("success", "Registrasi berhasil! Silakan masuk.");
            return "redirect:/";
        } else {
            ra.addFlashAttribute("error", "Registrasi gagal, coba lagi.");
            return "redirect:/register";            
        }
    }

    @GetMapping("/footer")
    public String footerPage(Model model) {
        return "teslogin";
    }
}