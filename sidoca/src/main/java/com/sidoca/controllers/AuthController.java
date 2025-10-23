package com.sidoca.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sidoca.Models.AkunModel;

// import org.springframework.beans.factory.annotation.Autowired;

// import org.springframework.web.bind.annotation.RequestParam;


import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController extends BaseController{
    private final HttpSession session;

    @Autowired
    private AkunModel akunModel;

    // ngambil session
    public AuthController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/")
    public String Login() {
        return "login";
    }

    @GetMapping("/register")
    public String Register() {
        return "register";
    }

    @GetMapping("/dashboardZaqy")
    public ModelAndView index() {
        // Sama seperti session_start + redirect di PHP
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("redirect:/login");
    }
    @GetMapping("/footer")
    public String footerPage(Model model) {
        return "footer";
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

        // Mencegah SQL Injection
        boolean success = akunModel.saveAkun(akun);

        if (success) {
            ra.addFlashAttribute("success", "Registrasi berhasil! Silakan masuk.");
            return "redirect:/";
        } else {
            ra.addFlashAttribute("error", "Registrasi gagal, coba lagi.");
            return "redirect:/register";            
        }

        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Halaman dashboard");

        return loadView("dashboard", data);
    }
}
