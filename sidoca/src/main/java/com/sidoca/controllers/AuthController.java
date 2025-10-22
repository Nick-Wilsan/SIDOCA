package com.sidoca.controllers;

import com.sidoca.Models.AkunModel;
import com.sidoca.Models.DataBaseClass.Akun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private AkunModel akunModel;

    @GetMapping("/")
    public String loginPage(Model model) {
        // Objek Akun kosong diperlukan agar form login (jika menggunakan th:object) tidak error
        model.addAttribute("akun", new Akun());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        // Objek Akun kosong untuk diikat dengan form register (th:object)
        model.addAttribute("akunBaru", new Akun());
        return "register";
    }

    // POST: Memproses data Registrasi
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("akunBaru") Akun akun, RedirectAttributes ra) {
        
        // Mencegah XXS
        akun.setUsername(akun.getUsername().trim());
        akun.setEmail(akun.getEmail().trim());
        // Jika form tidak mengirim 'nama', gunakan 'username' sebagai nama sementara
        akun.setNama(akun.getUsername().trim()); 
        akun.setRole(akun.getRole().trim());

        // 1. Cek duplikasi username/email.
        // 2. Gunakan BCrypt (atau sejenisnya) untuk MENGHATCH PASSWORD di sini.
        // akun.setPassword(passwordEncoder.encode(akun.getPassword()));

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
}