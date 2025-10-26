package com.sidoca.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DTO.KampanyeDetailDTO;
import com.sidoca.Models.DTO.KampanyeVerifikasiDTO;
import com.sidoca.Models.DataBaseClass.Akun;
import jakarta.servlet.http.HttpSession;
import com.sidoca.Models.AkunModel;
import com.sidoca.Models.DTO.KampanyeAdminDTO;
import java.time.LocalDate;

@Controller
public class AdminController extends BaseController{
    private final HttpSession session;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private AkunModel akunModel;

    public AdminController(HttpSession session) {
        this.session = session;
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

    @GetMapping("/daftarKampanyeAdmin")
    public ModelAndView daftarKampanyeAdmin(@RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "status", required = false) String status) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }
        
        List<KampanyeAdminDTO> daftarKampanye = kampanyeModel.getAllKampanyeForAdmin(keyword, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Daftar Kampanye");
        data.put("kampanyeList", daftarKampanye);
        data.put("keyword", keyword);
        data.put("selectedStatus", status);
        
        return loadView("daftarKampanyeAdmin", data);
    }

    @GetMapping("/daftarKampanyeAdmin/detail/{id}")
    public ModelAndView detailKampanyeAdmin(@PathVariable("id") int id) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        KampanyeDetailDTO detail = kampanyeModel.getDetailKampanyeById(id);
        Map<String, Object> data = new HashMap<>();
        
        if (detail != null) {
            // Cek apakah kampanye sudah berakhir
            boolean isExpired = detail.getBatas_waktu().toLocalDate().isBefore(LocalDate.now());
            data.put("isExpired", isExpired);
        }
        
        data.put("judul", "Detail Kampanye");
        data.put("kampanye", detail);

        return loadView("detailKampanyeAdmin", data);
    }

    @PostMapping("/daftarKampanyeAdmin/ubahStatus")
    public String ubahStatusKampanye(@RequestParam("id_kampanye") int idKampanye, 
                                    @RequestParam("status") String status, 
                                    RedirectAttributes ra) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/";
        }

        boolean berhasil = kampanyeModel.ubahStatusKampanyeAdmin(idKampanye, status);
        if (berhasil) {
            ra.addFlashAttribute("success", "Status kampanye berhasil diubah menjadi " + status + ".");
        } else {
            ra.addFlashAttribute("error", "Gagal mengubah status. Kampanye mungkin telah berakhir.");
        }

        return "redirect:/daftarKampanyeAdmin";
    }

    @GetMapping("/kelolaAkun")
    public ModelAndView KelolaAkun(@RequestParam(name = "keyword", required = false) String keyword,
                                @RequestParam(name = "role", required = false) String role,
                                @RequestParam(name = "status", required = false) String status) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        // Dapatkan ID admin yang sedang login
        int loggedInAdminId = user.getId_akun();

        // Panggil model dengan parameter filter dan ID admin
        List<Akun> daftarAkun = akunModel.getAllAkun(keyword, role, status, loggedInAdminId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Kelola Akun");
        data.put("akunList", daftarAkun);

        // Kirim kembali nilai filter untuk ditampilkan di form
        data.put("keyword", keyword);
        data.put("selectedRole", role);
        data.put("selectedStatus", status);

        return loadView("kelolaAkun", data);
    }

    @PostMapping("/kelolaAkun/ubahStatus")
    public String ubahStatusAkun(@RequestParam("id_akun") int idAkun, 
                                @RequestParam("status") String status, 
                                RedirectAttributes ra) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/";
        }

        boolean berhasil = akunModel.ubahStatusAkun(idAkun, status);
        if (berhasil) {
            ra.addFlashAttribute("success", "Status akun berhasil diubah.");
        } else {
            ra.addFlashAttribute("error", "Gagal mengubah status akun.");
        }

        return "redirect:/kelolaAkun";
    }
}
