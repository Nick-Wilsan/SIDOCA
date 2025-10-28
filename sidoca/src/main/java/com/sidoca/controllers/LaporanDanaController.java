package com.sidoca.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sidoca.Models.LaporanDanaModel;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DataBaseClass.LaporanDana;

import jakarta.servlet.http.HttpSession;

@Controller
public class LaporanDanaController extends BaseController{
    
    private final HttpSession session;

    @Autowired
    private LaporanDanaModel laporanDanaModel;

    public LaporanDanaController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/laporanPenggunaanDana")
    public ModelAndView LaporanPenggunaanDana(@RequestParam("id_kampanye") int idKampanye) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        // buat objek baru untuk form
        LaporanDana laporanDana = new LaporanDana();
        laporanDana.setId_kampanye(idKampanye);
        laporanDana.setId_organisasi(user.getId_akun()); // organisasi dari akun login

        Map<String, Object> model = new HashMap<>();
        model.put("laporanDanaBaru", laporanDana);
        model.put("Judul", "Ajukan Laporan Dana");
        model.put("nama", user.getNama());

        return new ModelAndView("mengajukanLaporanDana", model);
    }

    @PostMapping("/tambahLaporanDana")
    public String TambahLaporanDana(
        @RequestParam("bukti_dokumen") MultipartFile file,
        @ModelAttribute LaporanDana laporanDana,
        RedirectAttributes ra,
        HttpSession session) {

        try {
            // Ambil user organisasi dari session
            Akun user = (Akun) session.getAttribute("user");
            if (user == null) {
                ra.addFlashAttribute("error", "Sesi berakhir. Silakan login kembali.");
                return "redirect:/";
            }

            // Validasi field dasar
            if (laporanDana.getDeskripsi_penggunaan() == null || laporanDana.getDeskripsi_penggunaan().trim().isEmpty()
                    || file.isEmpty() || laporanDana.getTotal_Pengeluaran() == 0) {
                ra.addFlashAttribute("error", "Semua field wajib diisi.");
                ra.addFlashAttribute("laporanDanaBaru", laporanDana);
                return "redirect:/laporanPenggunaanDana";
            }

            // Konversi file PDF ke byte[]
            laporanDana.setBukti_dokumen(file.getBytes());

            // Isi otomatis field lain
            laporanDana.setId_organisasi(user.getId_akun());
            laporanDana.setStatus_verifikasi("Menunggu Verifikasi");
            laporanDana.setTgl_pengajuan(LocalDateTime.now());
            laporanDana.setTgl_verifikasi(null);

            // Insert ke database
            int insertedId = laporanDanaModel.InsertLaporanDana(laporanDana);

            if (insertedId > 0) {
                ra.addFlashAttribute("success", "Laporan dana berhasil diajukan!");
            } else {
                ra.addFlashAttribute("error", "Terjadi kesalahan saat menyimpan laporan.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Gagal membaca file bukti dokumen.");
        }

        return "redirect:/mengajukanLaporanDana";
    }

}
