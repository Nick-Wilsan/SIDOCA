package com.sidoca.controllers;

import com.sidoca.Models.DTO.ProfilDTO;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.ProfilModel;
import com.sidoca.services.EmailService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sidoca.Models.AkunModel;

@Controller
public class ProfileController extends BaseController {
    
    @Autowired
    private HttpSession session;

    @Autowired
    private ProfilModel profilModel;

    @Autowired
    private AkunModel akunModel;

    @Autowired
    private EmailService emailService;

    private ModelAndView getProfilModelAndView(String viewName, String judul, Akun user, boolean isEditMode) {
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        ProfilDTO profil = profilModel.getProfilByAkunId(user.getId_akun(), user.getRole());
        
        Map<String, Object> data = new HashMap<>();
        data.put("Judul", judul);
        data.put("profil", profil);
        data.put("editMode", isEditMode);
        
        return loadView(viewName, data);
    }

    // Helper untuk mendapatkan suffix URL profil
    private String getRoleSuffix(String role) {
        if (role == null || role.isEmpty()) {
            return "";
        }
        return role.substring(0, 1).toUpperCase() + role.substring(1);
    }

    @GetMapping("/profilDonatur")
    public ModelAndView profilDonatur(@RequestParam(required = false) boolean edit) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"donatur".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return getProfilModelAndView("profilDonatur", "Profil Donatur", user, edit);
    }

    @GetMapping("/profilOrganisasi")
    public ModelAndView profilOrganisasi(@RequestParam(required = false) boolean edit) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return getProfilModelAndView("profilOrganisasi", "Profil Organisasi", user, edit);
    }

    @GetMapping("/profilAdmin")
    public ModelAndView profilAdmin(@RequestParam(required = false) boolean edit) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return getProfilModelAndView("profilAdmin", "Profil Admin", user, edit);
    }

    @PostMapping("/profil/update")
    public String updateProfil(@ModelAttribute ProfilDTO profilDTO, RedirectAttributes ra) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        // Cek keunikan Nomor Telepon
        if (profilModel.isNoHpTaken(profilDTO.getNoHp(), user.getId_akun())) {
            ra.addFlashAttribute("error", "Nomor telepon sudah digunakan oleh akun lain.");
            return "redirect:/profil" + getRoleSuffix(user.getRole()) + "?edit=true";
        }

        // Dapatkan email lama dari sesi dan email baru dari form
        String oldEmail = user.getEmail();
        String newEmail = profilDTO.getEmail();

        // Cek apakah email diubah
        if (newEmail != null && !newEmail.equalsIgnoreCase(oldEmail)) {
            // Email diubah, maka mulai proses verifikasi
            if (profilModel.isEmailTaken(newEmail, user.getId_akun())) {
                ra.addFlashAttribute("error", "Alamat email sudah digunakan oleh akun lain.");
                return "redirect:/profil" + getRoleSuffix(user.getRole()) + "?edit=true";
            }
            
            // Buat kode verifikasi
            String verificationCode = String.format("%06d", new Random().nextInt(999999));
            emailService.sendVerificationEmail(newEmail, verificationCode);

            // Simpan data perubahan (termasuk email baru) dan kode di session
            session.setAttribute("pending_profile_update", profilDTO);
            session.setAttribute("pending_email_code", verificationCode);

            ra.addFlashAttribute("info", "Perubahan disimpan sementara. Silakan cek email baru Anda untuk kode verifikasi.");
            return "redirect:/profil/verifikasi-email";

        } else {
            // Email tidak diubah, langsung update data
            profilDTO.setIdAkun(user.getId_akun()); // Pastikan ID akun sudah ter-set
            boolean isSuccess = profilModel.updateProfil(profilDTO, user.getRole());
            if (isSuccess) {
                ra.addFlashAttribute("success", "Profil berhasil diperbarui.");
                // Update juga data di session
                user.setNama(profilDTO.getNama());
                user.setNo_HP(profilDTO.getNoHp());
                session.setAttribute("user", user);
            } else {
                ra.addFlashAttribute("error", "Gagal memperbarui profil.");
            }
        }

        return "redirect:/profil" + getRoleSuffix(user.getRole());
    }

    @GetMapping("/profil/verifikasi-email")
    public ModelAndView showVerifyEmailPage() {
        if (session.getAttribute("user") == null || session.getAttribute("pending_profile_update") == null) {
            return new ModelAndView("redirect:/dashboard");
        }
        ProfilDTO pendingUpdate = (ProfilDTO) session.getAttribute("pending_profile_update");
        Map<String, Object> data = new HashMap<>();
        data.put("email", pendingUpdate.getEmail());
        return loadView("verifikasiUbahEmail", data); 
    }

    @PostMapping("/profil/verifikasi-email")
    public String verifyEmailChange(@RequestParam("code") String code, RedirectAttributes ra) {
        Akun user = (Akun) session.getAttribute("user");
        String sessionCode = (String) session.getAttribute("pending_email_code");
        ProfilDTO profilDTO = (ProfilDTO) session.getAttribute("pending_profile_update");

        if (user == null || sessionCode == null || profilDTO == null) {
            return "redirect:/dashboard";
        }

        if (sessionCode.equals(code)) {
            // Kode cocok, update semua data termasuk email baru
            profilDTO.setIdAkun(user.getId_akun());
            boolean profilUpdated = profilModel.updateProfil(profilDTO, user.getRole());
            boolean emailUpdated = profilModel.updateEmail(user.getId_akun(), profilDTO.getEmail());

            if (profilUpdated && emailUpdated) {
                // Update data di session
                user.setNama(profilDTO.getNama());
                user.setEmail(profilDTO.getEmail());
                user.setNo_HP(profilDTO.getNoHp());
                session.setAttribute("user", user);

                ra.addFlashAttribute("success", "Profil dan alamat email berhasil diperbarui!");
            } else {
                ra.addFlashAttribute("error", "Terjadi kesalahan saat menyimpan perubahan ke database.");
            }

            // Hapus data sementara dari session
            session.removeAttribute("pending_profile_update");
            session.removeAttribute("pending_email_code");

            return "redirect:/profil" + getRoleSuffix(user.getRole());
        } else {
            ra.addFlashAttribute("error", "Kode verifikasi salah. Silakan coba lagi.");
            return "redirect:/profil/verifikasi-email";
        }
    }

    @GetMapping("/profil/hapus-akun")
    public String requestDeleteAccount(RedirectAttributes ra) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"donatur".equals(user.getRole())) {
            return "redirect:/";
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        emailService.sendDeleteAccountEmail(user.getEmail(), verificationCode);

        session.setAttribute("delete_account_code", verificationCode);
        session.setAttribute("delete_account_user_id", user.getId_akun());

        ra.addFlashAttribute("info", "Silakan cek email Anda untuk kode verifikasi penghapusan akun.");
        return "redirect:/profil/verifikasi-hapus-akun";
    }

    @GetMapping("/profil/verifikasi-hapus-akun")
    public ModelAndView showVerifyDeletePage() {
        if (session.getAttribute("delete_account_code") == null) {
            return new ModelAndView("redirect:/profilDonatur");
        }
        return new ModelAndView("verifikasiHapusAkun");
    }

    @PostMapping("/profil/verifikasi-hapus-akun")
    public String verifyDeleteAccount(@RequestParam("code") String code, RedirectAttributes ra) {
        String sessionCode = (String) session.getAttribute("delete_account_code");
        Integer userId = (Integer) session.getAttribute("delete_account_user_id");

        if (sessionCode == null || userId == null) {
            return "redirect:/profilDonatur";
        }

        if (sessionCode.equals(code)) {
            boolean isDeleted = akunModel.deleteAkun(userId);
            if (isDeleted) {
                session.invalidate();
                ra.addFlashAttribute("success", "Akun Anda telah berhasil dihapus.");
                return "redirect:/";
            } else {
                ra.addFlashAttribute("error", "Gagal menghapus akun. Silakan coba lagi.");
                return "redirect:/profil/verifikasi-hapus-akun";
            }
        } else {
            ra.addFlashAttribute("error", "Kode verifikasi salah.");
            return "redirect:/profil/verifikasi-hapus-akun";
        }
    }
}