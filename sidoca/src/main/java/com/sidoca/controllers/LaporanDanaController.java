package com.sidoca.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.LaporanDanaModel;
import com.sidoca.Models.OrganisasiModel;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DataBaseClass.Kampanye;
import com.sidoca.Models.DataBaseClass.LaporanDana;

import jakarta.servlet.http.HttpSession;

@Controller
public class LaporanDanaController extends BaseController{
    
    private final HttpSession session;

    @Autowired
    private LaporanDanaModel laporanDanaModel;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private OrganisasiModel organisasiModel;

    public LaporanDanaController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/mengajukanLaporanDana")
    public ModelAndView LaporanPenggunaanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        // Ambil semua kampanye milik organisasi ini
        List<Kampanye> listKampanye = kampanyeModel.GetAllKampanyeByOrganisasi(user.getId_akun());

        // Buat objek kosong untuk form
        LaporanDana laporanDana = new LaporanDana();

        int idOrganisasi = organisasiModel.GetIdOrganisasiByIdAkun(user.getId_akun());
        laporanDana.setId_organisasi(idOrganisasi);
        
        laporanDana.setId_kampanye(-1);
        Map<String, Object> model = new HashMap<>();
        
        if(listKampanye.isEmpty()){
            model.put("kampanyeKosong", "Tidak Ada Kampaye");
        }
        else{
            model.put("listKampanye", listKampanye);
        }
        

        model.put("laporanDanaBaru", laporanDana);
        model.put("Judul", "Ajukan Laporan Dana");
        model.put("nama", user.getNama());
        

        return new ModelAndView("mengajukanLaporanDana", model);
    }

    @PostMapping("/tambahLaporanDana")
    public String TambahLaporanDana(
            @RequestParam("bukti_file") MultipartFile file,
            @ModelAttribute LaporanDana laporanDana,
            RedirectAttributes ra,
            HttpSession session) {

        try {
            Akun user = (Akun) session.getAttribute("user");
            if (user == null) {
                ra.addFlashAttribute("error", "Sesi berakhir. Silakan login kembali.");
                return "redirect:/";
            }

            // Validasi dasar
            if (laporanDana.getId_kampanye() == 0 ||
                laporanDana.getDeskripsi_penggunaan() == null ||
                laporanDana.getDeskripsi_penggunaan().trim().isEmpty() ||
                file.isEmpty() ||
                laporanDana.getTotal_Pengeluaran() <= 0) {

                ra.addFlashAttribute("error", "Semua field wajib diisi.");
                return "redirect:/mengajukanLaporanDana";
            }

            // Ambil ID organisasi dari akun
            // int idOrganisasi = organisasiModel.GetIdOrganisasiByIdAkun(user.getId_akun());
            // laporanDana.setId_organisasi(idOrganisasi);

            // Simpan file bukti
            laporanDana.setBukti_dokumen(file.getBytes());
            laporanDana.setStatus_verifikasi("menunggu");
            laporanDana.setTgl_pengajuan(LocalDateTime.now());
            laporanDana.setTgl_verifikasi(null);

            int insertedId = laporanDanaModel.InsertLaporanDana(laporanDana);

            if (insertedId > 0) {
                ra.addFlashAttribute("success", "Laporan dana berhasil diajukan!");
            } else {
                String error = "id_org:" + insertedId;
                ra.addFlashAttribute("error", error);
            }


        } catch (IOException e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Gagal membaca file bukti dokumen.");
        }

        return "redirect:/mengajukanLaporanDana";
    }


}
