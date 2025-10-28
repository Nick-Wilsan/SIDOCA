package com.sidoca.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sidoca.Models.KampanyeGambarModel;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DTO.KampanyeAktifDTO;
import com.sidoca.Models.DTO.KampanyeDetailDTO;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DataBaseClass.Kampanye;
import com.sidoca.Models.DataBaseClass.KampanyeGambar;
import com.sidoca.services.EmailService;

import jakarta.servlet.http.HttpSession;
import com.sidoca.Models.DTO.StatusVerifikasiDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class KampanyeController extends BaseController{

    private final HttpSession session;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private KampanyeGambarModel kampanyeGambarModel;

    @Autowired
    private EmailService emailService;

    public KampanyeController(HttpSession session) {
        this.session = session;
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
    public ModelAndView statusVerifikasi(@RequestParam(name = "keyword", required = false) String keyword,
                                        @RequestParam(name = "jenis", required = false) String jenis,
                                        @RequestParam(name = "status", required = false) String status) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        List<StatusVerifikasiDTO> daftarStatus = kampanyeModel.getStatusVerifikasiForOrganisasi(user.getId_akun(), keyword, jenis, status);

        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Status Verifikasi");
        data.put("statusList", daftarStatus);
        data.put("keyword", keyword);
        data.put("selectedJenis", jenis);
        data.put("selectedStatus", status);

        return loadView("statusVerifikasi", data);
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

    @GetMapping("/kampanye/hapus/{id}")
    public String requestDeleteCampaign(@PathVariable("id") int idKampanye, RedirectAttributes ra, HttpSession session) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return "redirect:/";
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        emailService.sendDeleteCampaignEmail(user.getEmail(), verificationCode);

        session.setAttribute("delete_campaign_code", verificationCode);
        session.setAttribute("delete_campaign_id", idKampanye);

        ra.addFlashAttribute("info", "Silakan cek email Anda untuk kode verifikasi penghapusan kampanye.");
        return "redirect:/kampanye/verifikasi-hapus/" + idKampanye;
    }

    @GetMapping("/kampanye/verifikasi-hapus/{id}")
    public ModelAndView showVerifyDeletePage(@PathVariable("id") int idKampanye) {
        if (session.getAttribute("delete_campaign_code") == null) {
            return new ModelAndView("redirect:/statusVerifikasi");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id_kampanye", idKampanye);
        return new ModelAndView("verifikasiHapusKampanye", data);
    }

    @PostMapping("/kampanye/verifikasi-hapus")
    public String verifyDeleteCampaign(@RequestParam("code") String code, @RequestParam("id_kampanye") int idKampanye, RedirectAttributes ra) {
        String sessionCode = (String) session.getAttribute("delete_campaign_code");
        Integer campaignId = (Integer) session.getAttribute("delete_campaign_id");

        if (sessionCode == null || campaignId == null || campaignId != idKampanye) {
            return "redirect:/statusVerifikasi";
        }

        if (sessionCode.equals(code)) {
            BigDecimal danaTerkumpul = kampanyeModel.getDanaTerkumpul(idKampanye);
            kampanyeModel.saveDanaNonaktif(idKampanye, danaTerkumpul);
            boolean isDeleted = kampanyeModel.deleteKampanye(idKampanye);
            
            if (isDeleted) {
                ra.addFlashAttribute("success", "Kampanye Anda telah berhasil dihapus.");
            } else {
                ra.addFlashAttribute("error", "Gagal menghapus kampanye. Silakan coba lagi.");
            }
            session.removeAttribute("delete_campaign_code");
            session.removeAttribute("delete_campaign_id");
            return "redirect:/statusVerifikasi";
        } else {
            ra.addFlashAttribute("error", "Kode verifikasi salah.");
            return "redirect:/kampanye/verifikasi-hapus/" + idKampanye;
        }
    }
}