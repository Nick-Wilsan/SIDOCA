package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sidoca.Models.AkunModel;
import jakarta.servlet.http.HttpSession;
import com.sidoca.Models.DataBaseClass.Akun;

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
        // Jika sudah login, redirect ke dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String Register() {
        // Jika sudah login, redirect ke dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        // (2) Validasi URL: Jika belum login, redirect ke halaman login
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }

        // Tampilkan halaman dashboard jika sudah login
        Akun user = (Akun) session.getAttribute("user");
        return loadView("dashboard", java.util.Map.of(
            "judul", "Dashboard Pengguna",
            "nama", user.getNama(),
            "role", user.getRole()
        ));
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String identifier, // Mengambil input username/email
                            @RequestParam("password") String password,
                            RedirectAttributes ra) {

        // 1. Ambil data dan lakukan pengecekan
        Akun akun = akunModel.findUserForLogin(identifier, password);

        if (akun != null) {
            // 3. Login berhasil: Simpan objek Akun ke dalam session
            session.setAttribute("user", akun);
            ra.addFlashAttribute("success", "Login berhasil! Selamat datang, " + akun.getNama() + ".");
            // Redirect ke halaman dashboard
            return "redirect:/dashboard";
        } else {
            // Login gagal
            ra.addFlashAttribute("error", "Username/Email atau Password salah.");
            // Redirect kembali ke halaman login
            return "redirect:/";
        }
    }
    
    // --- (4) Proses Logout ---
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        // Hapus semua data sesi
        session.invalidate();
        ra.addFlashAttribute("success", "Anda telah berhasil logout.");
        // Redirect kembali ke halaman login (/)
        return "redirect:/";
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

        // Map<String, Object> data = new HashMap<>();
        // data.put("judul", "Halaman dashboard");

        // return loadView("dashboard", data);
    }
}
