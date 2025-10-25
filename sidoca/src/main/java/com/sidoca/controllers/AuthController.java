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

import ch.qos.logback.core.model.Model;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import org.springframework.util.StringUtils;
import java.nio.file.Path;
import com.sidoca.Models.DTO.KampanyeVerifikasiDTO;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.sidoca.Models.DTO.KampanyeDetailDTO;
import com.sidoca.Models.AkunModel;
import com.sidoca.Models.DTO.KampanyeAktifDTO;
import com.sidoca.Models.DonaturModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController extends BaseController{
    private final HttpSession session;

    @Autowired
    private AkunModel akunModel;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private KampanyeGambarModel kampanyeGambarModel;

    @Autowired
    private DonaturModel donaturModel;

    // ngambil session
    public AuthController(HttpSession session) {
        this.session = session;
    }

    

    // =================================================================
    // HALAMAN PUBLIK & AUTENTIKASI
    // =================================================================
    @GetMapping("/")
    public String Login() {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "index";
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
    
    @GetMapping("/register")
    public String Register() {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

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
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        // Hapus semua data sesi
        session.invalidate();
        ra.addFlashAttribute("success", "Anda telah berhasil logout.");
        // Redirect kembali ke halaman login (/)
        return "redirect:/";
    }



    // =================================================================
    // PENGALIH UTAMA (DASHBOARD & PROFIL)
    // =================================================================
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



    // =================================================================
    // HALAMAN KHUSUS DONATUR
    // =================================================================
    @GetMapping("/dashboardDonatur")
    public ModelAndView dashboardDonatur() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("dashboardDonatur", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }

    @GetMapping("/daftarKampanye")
    public ModelAndView daftarKampanye(@RequestParam(name = "keyword", required = false) String keyword,
                                        @RequestParam(name = "urutkan", required = false) String urutkan) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        List<KampanyeAktifDTO> daftarKampanye = kampanyeModel.getKampanyeAktif(keyword, urutkan);
        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Daftar Kampanye");
        data.put("kampanyeList", daftarKampanye);
        data.put("keyword", keyword);
        data.put("urutkan", urutkan);

        return loadView("daftarKampanye", data);
    }

    @GetMapping("/kampanye/{id}")
    public ModelAndView lihatDetailKampanye(@PathVariable("id") int idKampanye) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        KampanyeDetailDTO detailKampanye = kampanyeModel.getDetailKampanyeById(idKampanye);
        if (detailKampanye == null) {
            return new ModelAndView("redirect:/daftarKampanye?error=notfound");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Detail Kampanye");
        data.put("kampanye", detailKampanye);
        Akun loggedInUser = (Akun) session.getAttribute("user");
        if (loggedInUser != null) {
            data.put("namaUserLogin", loggedInUser.getNama()); 
        } else {
            data.put("namaUserLogin", "Anonim");
        }

        return loadView("lihatDetailKampanye", data);
    }

    @PostMapping("/kampanye/{id}/komentar")
    public ModelAndView tambahKomentar(@PathVariable("id") int idKampanye,
                                        @RequestParam("isiKomentar") String isiKomentar,
                                        RedirectAttributes ra) {
        Akun loggedInUser = (Akun) session.getAttribute("user");
        if (loggedInUser == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(loggedInUser.getRole())) {
            ra.addFlashAttribute("error", "Hanya donatur yang dapat memberikan komentar.");
            return new ModelAndView("redirect:/kampanye/" + idKampanye);
        }
        Integer idDonatur = donaturModel.getDonaturIdByAkunId(loggedInUser.getId_akun());
        if (idDonatur == null) {
            ra.addFlashAttribute("error", "Gagal mengirim komentar. Data donatur tidak ditemukan.");
            return new ModelAndView("redirect:/kampanye/" + idKampanye);
        }
        if (isiKomentar != null && !isiKomentar.trim().isEmpty()) {
            kampanyeModel.tambahKomentar(idKampanye, idDonatur, isiKomentar);
        }
        return new ModelAndView("redirect:/kampanye/" + idKampanye + "#komentarSection");
    }

    @GetMapping("/laporanPenggunaanDana")
    public ModelAndView LaporanPenggunaanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("laporanPenggunaanDana", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }

    @GetMapping("/riwayatDonasi")
    public ModelAndView RiwayatDonasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("riwayatDonasi", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }
    
    @GetMapping("/leaderboard")
    public ModelAndView Leaderboard() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("leaderboard", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }

    @GetMapping("/profilDonatur")
    public ModelAndView profilDonatur() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("profilDonatur", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }



    // =================================================================
    // HALAMAN KHUSUS ORGANISASI
    // =================================================================
    @GetMapping("/dashboardOrganisasi")
    public ModelAndView dashboardOrganisasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("dashboardOrganisasi", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/kampanyeBaru")
    public ModelAndView KampanyeBaru() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("kampanyeBaru", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @PostMapping("/buat-kampanye")
    public ModelAndView buatKampanye(@RequestParam("judulKampanye") String judul,
                            @RequestParam("deskripsiKampanye") String deskripsi,
                            @RequestParam("targetDana") BigDecimal targetDana,
                            @RequestParam("batasWaktu") Date batasWaktu,
                            @RequestParam("fileUpload") MultipartFile[] files, // Ubah menjadi array
                            HttpSession session,
                            RedirectAttributes ra) {

        if (batasWaktu.before(new java.util.Date())) {
            ra.addFlashAttribute("error", "Batas waktu tidak boleh kurang dari tanggal hari ini.");
            return new ModelAndView("redirect:/kampanyeBaru");
        }
        
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
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
            return new ModelAndView("redirect:/kampanyeBaru");
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
        return new ModelAndView("redirect:/dashboardOrganisasi");
    }

    @GetMapping("/pencairanDana")
    public ModelAndView PencairanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("pencairanDana", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/mengajukanLaporanDana")
    public ModelAndView MengajukanLaporanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("mengajukanLaporanDana", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/statusVerifikasi")
    public ModelAndView StatusVerifikasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("statusVerikasi", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/profilOrganisasi")
    public ModelAndView profilOrganisasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("profilOrganisasi", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }



    // =================================================================
    // HALAMAN KHUSUS ADMIN
    // =================================================================
    @GetMapping("/dashboardAdmin")
    public ModelAndView dashboardAdmin() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("dashboardAdmin", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }

    @GetMapping("/verifikasiKampanye")
    public ModelAndView VerifikasiKampanye() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }

        List<KampanyeVerifikasiDTO> daftarKampanye = kampanyeModel.getKampanyeMenungguVerifikasi();
        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Verifikasi Kampanye");
        data.put("kampanyeList", daftarKampanye);

        return loadView("verifikasiKampanye", data);
    }

    @GetMapping("/verifikasiKampanye/detail/{id}")
    public ModelAndView verifikasiDetailKampanye(@PathVariable("id") int id) {
        Akun user = (Akun) session.getAttribute("user");
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
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

        String newStatus = action.equals("setuju") ? "aktif" : "nonaktif";

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

    @GetMapping("/verifikasiPenggunaanDana")
    public ModelAndView VerifikasiPenggunaanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("verifikasiPenggunaanDana", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }

    @GetMapping("/verifikasiPencairanDana")
    public ModelAndView VerifikasiPencairanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("verifikasiPencairanDana", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }

    @GetMapping("/menonaktifkanKampanye")
    public ModelAndView MenonaktifkanKampanye() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("menonaktifkanKampanye", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }
    @GetMapping("/kelolaAkun")
    public ModelAndView KelolaAkun() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("kelolaAkun", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }

    @GetMapping("/profilAdmin")
    public ModelAndView profilAdmin() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("profilAdmin", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }



    // =================================================================
    // HALAMAN STATIS (DAPAT DIAKSES SEMUA)
    // =================================================================
    
    @GetMapping("/aboutUs")
    public String AboutUs() {
        return "aboutUs";
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
}