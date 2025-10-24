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
import com.sidoca.Models.KampanyeGambarModel;
import jakarta.servlet.http.HttpSession;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DataBaseClass.Kampanye;
import com.sidoca.Models.DataBaseClass.KampanyeGambar;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import org.springframework.util.StringUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.sidoca.Models.DTO.KampanyeVerifikasiDTO;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.sidoca.Models.DTO.KampanyeDetailDTO;

@Controller
public class AuthController extends BaseController{
    private final HttpSession session;

    @Autowired
    private AkunModel akunModel;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private KampanyeGambarModel kampanyeGambarModel;

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
    }

    @PostMapping("/buat-kampanye")
    public String buatKampanye(@RequestParam("judulKampanye") String judul,
                            @RequestParam("deskripsiKampanye") String deskripsi,
                            @RequestParam("targetDana") BigDecimal targetDana,
                            @RequestParam("batasWaktu") Date batasWaktu,
                            @RequestParam("fileUpload") MultipartFile[] files, // Ubah menjadi array
                            HttpSession session,
                            RedirectAttributes ra) {

        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        // Membuat objek Kampanye
        Kampanye kampanye = new Kampanye();
        kampanye.setId_akun(user.getId_akun());
        kampanye.setJudul_kampanye(judul);
        kampanye.setDeskripsi_kampanye(deskripsi);
        kampanye.setTarget_dana(targetDana);
        kampanye.setBatas_waktu(batasWaktu);
        kampanye.setStatus_kampanye("menunggu"); // Status awal

        // Simpan data kampanye dan dapatkan ID-nya
        int kampanyeId = kampanyeModel.saveKampanye(kampanye);

        if (kampanyeId == -1) {
            ra.addFlashAttribute("error", "Gagal membuat kampanye.");
            return "redirect:/kampanyeBaru";
        }

        // Proses dan simpan setiap file yang diunggah
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = StringUtils.cleanPath(System.currentTimeMillis() + "_" + file.getOriginalFilename());
                    Path uploadPath = Paths.get("src/main/resources/static/images/campaigns/");
                
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                    // Simpan URL gambar ke tabel Kampanye_Gambar
                    KampanyeGambar gambar = new KampanyeGambar();
                    gambar.setId_kampanye(kampanyeId);
                    gambar.setUrl_gambar("/images/campaigns/" + fileName);
                    kampanyeGambarModel.saveGambar(gambar);
                
                } catch (IOException e) {
                    e.printStackTrace();
                    ra.addFlashAttribute("error", "Gagal mengunggah salah satu file gambar.");
                    // Lanjutkan loop jika satu file gagal
                }
            }
        }
        ra.addFlashAttribute("success", "Kampanye berhasil dibuat dan menunggu persetujuan.");
        return "redirect:/dashboardOrganisasi";
    }

    @GetMapping("/verifikasiKampanye")
    public ModelAndView VerifikasiKampanye() { // Ubah tipe return menjadi ModelAndView
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        } 

        // Ambil daftar kampanye dari model
        List<KampanyeVerifikasiDTO> daftarKampanye = kampanyeModel.getKampanyeMenungguVerifikasi();

        // Siapkan data untuk dikirim ke view
        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Verifikasi Kampanye");
        data.put("kampanyeList", daftarKampanye);

        // Kirim data ke view "verifikasiKampanye"
        return loadView("verifikasiKampanye", data);
    }

    @GetMapping("/verifikasiKampanye/detail/{id}")
    public ModelAndView verifikasiDetailKampanye(@PathVariable("id") int id) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }

        KampanyeDetailDTO detail = kampanyeModel.getDetailKampanyeById(id);

        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Detail Verifikasi");
        data.put("kampanyeDetail", detail);

        return loadView("verifikasiDetailKampanye", data);
    }

    @PostMapping("/verifikasi-kampanye/proses")
    public String prosesVerifikasiKampanye(@RequestParam("id_kampanye") int idKampanye,
                                            @RequestParam("action") String action,
                                            @RequestParam(name = "alasan", required = false) String alasan, // Ambil parameter 'alasan'
                                            RedirectAttributes ra) {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        String newStatus = action.equals("setuju") ? "aktif" : "ditolak";

        // Panggil method model yang sudah diperbarui dengan parameter alasan
        boolean success = kampanyeModel.updateStatusKampanye(idKampanye, newStatus, alasan);

        if (success) {
            String message = "Kampanye berhasil di" + (action.equals("setuju") ? "setujui." : "tolak.");
            ra.addFlashAttribute("success", message);
        } else {
            ra.addFlashAttribute("error", "Gagal memperbarui status kampanye.");
        }

        return "redirect:/verifikasiKampanye";
    }
}
