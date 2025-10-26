package com.sidoca.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sidoca.Models.AkunModel;
import jakarta.servlet.http.HttpSession;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.OrganisasiModel;
import com.sidoca.services.EmailService;
import java.util.Random;
import com.sidoca.Models.DonaturModel;

@Controller
public class AuthController extends BaseController{
    private final HttpSession session;

    @Autowired
    private AkunModel akunModel;

    // @Autowired
    // private KampanyeModel kampanyeModel;

    @Autowired
    private EmailService emailService;

    // @Autowired
    // private KampanyeGambarModel kampanyeGambarModel;

    @Autowired
    private DonaturModel donaturModel;

    @Autowired
    private OrganisasiModel organisasiModel;

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
    public String handleRegister(@ModelAttribute Akun akun, RedirectAttributes ra, HttpSession session) {
        // Validasi dasar
        if (akun.getUsername() == null || akun.getUsername().trim().isEmpty() ||
            akun.getEmail() == null || akun.getEmail().trim().isEmpty() ||
            akun.getPassword() == null || akun.getPassword().isEmpty() ||
            akun.getRole() == null || akun.getRole().trim().isEmpty() ||
            akun.getNama() == null || akun.getNama().trim().isEmpty()) {
            ra.addFlashAttribute("error", "Semua field wajib diisi.");
            ra.addFlashAttribute("akunBaru", akun);
            return "redirect:/register";
        }

        // Buat kode verifikasi 6 digit
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        // Simpan data akun dan kode verifikasi di session
        session.setAttribute("akun_pending_verification", akun);
        session.setAttribute("verification_code", verificationCode);

        session.setAttribute("can_access_verify_email", true);

        // Kirim email verifikasi
        emailService.sendVerificationEmail(akun.getEmail(), verificationCode);

        // Arahkan ke halaman verifikasi
        return "redirect:/verifikasiEmail";
    }

    @GetMapping("/verifikasiEmail")
    public String showVerificationPage(HttpSession session) {
        // Periksa flag di sini
        if (session.getAttribute("can_access_verify_email") == null) {
            return "redirect:/register";
        }

        // Jika tidak ada akun yang menunggu verifikasi, kembalikan ke register
        if (session.getAttribute("akun_pending_verification") == null) {
            return "redirect:/register";
        }
        return "verifikasiEmail";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam("code") String code, HttpSession session, RedirectAttributes ra) {
        String sessionCode = (String) session.getAttribute("verification_code");
        Akun akun = (Akun) session.getAttribute("akun_pending_verification");

        if (sessionCode == null || akun == null) {
            return "redirect:/register";
        }

        if (sessionCode.equals(code)) {
            // Kode cocok, lanjutkan proses registrasi
            int idAkunBaru = akunModel.saveAkun(akun);

            if (idAkunBaru > 0) {
                boolean detailSaved = false;
                if ("donatur".equals(akun.getRole())) {
                    detailSaved = donaturModel.saveDonatur(idAkunBaru);
                } else if ("organisasi".equals(akun.getRole())) {
                    detailSaved = organisasiModel.saveOrganisasi(idAkunBaru, akun.getNama());
                }

                if (detailSaved) {
                    ra.addFlashAttribute("success", "Verifikasi berhasil! Silakan masuk.");
                    // Hapus data dari session setelah berhasil
                    session.removeAttribute("akun_pending_verification");
                    session.removeAttribute("verification_code");

                    session.removeAttribute("can_access_verify_email");
                    return "redirect:/";
                } else {
                    ra.addFlashAttribute("error", "Registrasi gagal setelah verifikasi. Hubungi admin.");
                    return "redirect:/verifikasiEmail";
                }
            } else {
                ra.addFlashAttribute("error", "Registrasi gagal. Username atau Email mungkin sudah terdaftar.");
                return "redirect:/register";
            }
        } else {
            // Kode tidak cocok
            ra.addFlashAttribute("error", "Kode verifikasi salah. Silakan coba lagi.");
            return "redirect:/verifikasiEmail";
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
    // FORGOT PASSWORD
    // =================================================================
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, HttpSession session, RedirectAttributes ra) {
        Akun akun = akunModel. findByEmail(email);
        if (akun == null) {
            ra.addFlashAttribute("error", "Email tidak terdaftar di sistem kami.");
            return "redirect:/forgot-password";
        }
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        emailService.sendPasswordResetEmail(email, verificationCode);

        session.setAttribute("reset_email", email);
        session.setAttribute("reset_code", verificationCode);

        // Tambahkan flag ini
        session.setAttribute("can_access_verify_reset", true);

        return "redirect:/verify-reset-code";
    }

    @GetMapping("/verify-reset-code")
    public String verifyResetCodePage(HttpSession session) {
        // Periksa flag di sini
        if (session.getAttribute("can_access_verify_reset") == null) {
            return "redirect:/forgot-password";
        }

        if (session.getAttribute("reset_email") == null) {
            return "redirect:/forgot-password";
        }
        return "verify-reset-code";
    }

    @PostMapping("/verify-reset-code")
    public String handleVerifyResetCode(@RequestParam("code") String code, HttpSession session, RedirectAttributes ra) {
        String sessionCode = (String) session.getAttribute("reset_code");
        if (sessionCode == null) {
            return "redirect:/forgot-password";
        }
        if (sessionCode.equals(code)) {
            session.setAttribute("reset_verified", true);
            session.removeAttribute("reset_code");
            // Hapus flag di sini
            session.removeAttribute("can_access_verify_reset");
            return "redirect:/reset-password";
        } else {
            ra.addFlashAttribute("error", "Kode verifikasi salah.");
            return "redirect:/verify-reset-code";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session) {
        Boolean isVerified = (Boolean) session.getAttribute("reset_verified");
        if (isVerified == null || !isVerified) {
            return "redirect:/forgot-password";
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("password") String password,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    HttpSession session, RedirectAttributes ra) {
        Boolean isVerified = (Boolean) session.getAttribute("reset_verified");
        String email = (String) session.getAttribute("reset_email");

        if (isVerified == null || !isVerified || email == null) {
            return "redirect:/forgot-password";
        }

        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Password dan konfirmasi password tidak cocok.");
            return "redirect:/reset-password";
        }

        boolean success = akunModel.updatePasswordByEmail(email, password);
        if (success) {
            session.removeAttribute("reset_email");
            session.removeAttribute("reset_verified");
            ra.addFlashAttribute("success", "Password berhasil diubah. Silakan login dengan password baru Anda.");
            return "redirect:/";
        } else {
            ra.addFlashAttribute("error", "Gagal mengubah password. Silakan coba lagi.");
            return "redirect:/reset-password";
        }
    }
}