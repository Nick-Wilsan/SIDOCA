package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sidoca.Models.ProfilModel;
import com.sidoca.Models.DataBaseClass.Akun;

import jakarta.servlet.http.HttpSession;

@Controller
public class UbahPasswordController extends BaseController {

    @Autowired
    private HttpSession session;

    @Autowired
    private ProfilModel profilModel;

    @GetMapping("/ganti-kata-sandi")
    public ModelAndView showGantiPasswordPage() {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        return loadView("gantiKataSandi", null);
    }

    @PostMapping("/ganti-kata-sandi")
    public String prosesGantiPassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes ra) {

        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        // 1. Cek password lama
        if (!profilModel.verifyOldPassword(user.getId_akun(), oldPassword)) {
            ra.addFlashAttribute("error", "Password lama yang Anda masukkan salah.");
            return "redirect:/ganti-kata-sandi";
        }

        // 2. Cek apakah password baru dan konfirmasi cocok
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Password baru dan konfirmasi tidak cocok.");
            return "redirect:/ganti-kata-sandi";
        }

        // 3. Update password di database
        if (profilModel.updatePassword(user.getId_akun(), newPassword)) {
            ra.addFlashAttribute("success", "Password berhasil diubah.");
            // Redirect ke halaman profil sesuai role
            return "redirect:/profil" + user.getRole().substring(0, 1).toUpperCase() + user.getRole().substring(1);
        } else {
            ra.addFlashAttribute("error", "Terjadi kesalahan saat mengubah password.");
            return "redirect:/ganti-kata-sandi";
        }
    }
}