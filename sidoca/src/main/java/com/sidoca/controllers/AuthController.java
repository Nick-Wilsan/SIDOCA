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
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/register")
    public String Register() {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @GetMapping("/daftarKampanye")
    public String DaftarKampanye() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "daftarKampanye";
    }
    
    @GetMapping("/laporanPenggunaanDana")
    public String LaporanPenggunaanDana() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "laporanPenggunaanDana";
    }
    
    @GetMapping("/riwayatDonasi")
    public String RiwayatDonasi() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "riwayatDonasi";
    }
    
    @GetMapping("/leaderboard")
    public String Leaderboard() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "leaderboard";
    }
    
    @GetMapping("/kampanyeBaru")
    public String KampanyeBaru() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "kampanyeBaru";
    }
    
    @GetMapping("/pencairanDana")
    public String PencairanDana() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "pencairanDana";
    }
    
    @GetMapping("/mengajukanLaporanDana")
    public String MengajukanLaporanDana() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "mengajukanLaporanDana";
    }

    @GetMapping("/statusVerifikasi")
    public String StatusVerifikasi() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "statusVerfikasi";
    }
    
    @GetMapping("/aboutUs")
    public String AboutUs() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "aboutUs";
    }

    @GetMapping("/verifikasiKampanye")
    public String VerifikasiKampanye() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "verifikasiKampanye";
    }

    @GetMapping("/verifikasiPenggunaanDana")
    public String VerifikasiPenggunaanDana() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "verifikasiPenggunaanDana";
    }

    @GetMapping("/verifikasiPencairanDana")
    public String VerifikasiPencairanDana() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "verifikasiPencairanDana";
    }

    @GetMapping("/menonaktifkanKampanye")
    public String MenonaktifkanKampanye() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "menonaktifkanKampanye";
    }

    @GetMapping("/kelolaAkun")
    public String KelolaAkun() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        } 
        return "kelolaAkun";
    }
    
    @GetMapping("/pusatBantuan")
    public String PusatBantuan() {
        return "pusatBantuan";
    }

    @GetMapping("/faq")
    public String FAQ() {
        return "faq";
    }

    @GetMapping("/hubungiKami")
    public String hubungiKami() {
        return "hubungiKami";
    }

    @GetMapping("/layanan")
    public String Layanan() {
        return "layanan";
    }

    @GetMapping("/kebijakanPrivasi")
    public String KebijakanPrivasi() {
        return "kebijakanPrivasi";
    }

    @GetMapping("/blog")
    public String Blog() {
        return "blog";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        Akun user = (Akun) session.getAttribute("user");
        String role = user.getRole();

        switch (role) {
            case "donatur":
                return "redirect:/dashboardDonatur";
            case "organisasi":
                return "redirect:/dashboardOrganisasi";
            case "admin":
                return "redirect:/dashboardAdmin";
            default:
                return "redirect:/";
        }
    }

    @GetMapping("/dashboardDonatur")
    public ModelAndView dashboardDonatur() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        Akun user = (Akun) session.getAttribute("user");
        return loadView("dashboardDonatur", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }
    
    @GetMapping("/dashboardOrganisasi")
    public ModelAndView dashboardOrganisasi() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        Akun user = (Akun) session.getAttribute("user");
        return loadView("dashboardOrganisasi", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/dashboardAdmin")
    public ModelAndView dashboardAdmin() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        Akun user = (Akun) session.getAttribute("user");
        return loadView("dashboardAdmin", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }

    @GetMapping("/profil")
    public String profil() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        Akun user = (Akun) session.getAttribute("user");
        String role = user.getRole();

        switch (role) {
            case "donatur":
                return "redirect:/profilDonatur";
            case "organisasi":
                return "redirect:/profilOrganisasi";
            case "admin":
                return "redirect:/profilAdmin";
            default:
                return "redirect:/";
        }
    }

    @GetMapping("/profilDonatur")
    public ModelAndView profilDonatur() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        Akun user = (Akun) session.getAttribute("user");
        return loadView("profilDonatur", java.util.Map.of("Judul", "profil Donatur", "nama", user.getNama()));
    }
    
    @GetMapping("/profilOrganisasi")
    public ModelAndView profilOrganisasi() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        Akun user = (Akun) session.getAttribute("user");
        return loadView("profilOrganisasi", java.util.Map.of("Judul", "profil Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/profilAdmin")
    public ModelAndView profilAdmin() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        Akun user = (Akun) session.getAttribute("user");
        return loadView("profilAdmin", java.util.Map.of("Judul", "profil Admin", "nama", user.getNama()));
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
