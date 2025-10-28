// nick-wilsan/sidoca/SIDOCA-main/sidoca/src/main/java/com/sidoca/controllers/PencairanDanaController.java
package com.sidoca.controllers;

import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.OrganisasiModel;
import com.sidoca.Models.PencairanDanaModel;
import com.sidoca.Models.DTO.KampanyeAktifDTO;
import com.sidoca.Models.DTO.KampanyeDetailDTO;
import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DataBaseClass.PencairanDana;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import org.springframework.util.StringUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.Arrays;

@Controller
public class PencairanDanaController {

    @Autowired
    private HttpSession session;

    @Autowired
    private KampanyeModel kampanyeModel;
    
    @Autowired
    private PencairanDanaModel pencairanDanaModel;
    
    @Autowired
    private OrganisasiModel organisasiModel; 

    @GetMapping("/pencairanDana")
    public ModelAndView pilihKampanye(@RequestParam(name = "urutkan", required = false) String urutkan) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        List<KampanyeAktifDTO> kampanyeOrganisasi = kampanyeModel.getKampanyeAktifByOrganisasi(user.getId_akun(), urutkan);
        
        ModelAndView mav = new ModelAndView("pencairanDana");
        mav.addObject("kampanyeList", kampanyeOrganisasi);
        mav.addObject("urutkan", urutkan);
        return mav;
    }

    @GetMapping("/pencairan-dana/form/{id}")
    public ModelAndView showFormPencairan(@PathVariable("id") int idKampanye) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }

        KampanyeDetailDTO kampanye = kampanyeModel.getDetailKampanyeById(idKampanye);
        if (kampanye == null) {
            return new ModelAndView("redirect:/pencairan-dana");
        }

        boolean isFirstDisbursement = !pencairanDanaModel.hasCompletedDisbursements(idKampanye);
        BigDecimal danaTerkumpul = kampanye.getDana_terkumpul();
        BigDecimal danaSiapCair;

        if (isFirstDisbursement) {
            danaSiapCair = danaTerkumpul.multiply(new BigDecimal("0.5"));
        } else {
            BigDecimal danaSudahDicairkan = pencairanDanaModel.getTotalDanaDicairkan(idKampanye);
            danaSiapCair = danaTerkumpul.subtract(danaSudahDicairkan);
        }

        ModelAndView mav = new ModelAndView("formulir-pencairan");
        mav.addObject("id_kampanye", idKampanye);
        mav.addObject("nama_kampanye", kampanye.getJudul_kampanye());
        mav.addObject("totalDana", danaTerkumpul);
        mav.addObject("danaSiapCair", danaSiapCair);
        return mav;
    }

    @PostMapping("/pencairan-dana/submit")
    public String submitPencairan(
            @RequestParam("id_kampanye") int idKampanye,
            @RequestParam("jumlahPencairan") BigDecimal jumlahPencairan,
            @RequestParam("namaBank") String namaBank,
            @RequestParam("nomorRekening") String nomorRekening,
            @RequestParam("namaPemilikRekening") String namaPemilikRekening,
            @RequestParam("buktiPendukung") MultipartFile[] buktiPendukung,
            @RequestParam("alasanPencairan") String alasan,
            RedirectAttributes ra) {
        
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return "redirect:/";
        }

        int idOrganisasi = organisasiModel.GetIdOrganisasiByIdAkun(user.getId_akun());
        if (idOrganisasi == -1) {
            ra.addFlashAttribute("error", "Gagal menemukan data organisasi yang terhubung dengan akun Anda.");
            return "redirect:/pencairan-dana/form/" + idKampanye;
        }

        List<String> fileNames = new ArrayList<>();
        if (buktiPendukung != null && buktiPendukung.length > 0) {
            for (MultipartFile file : buktiPendukung) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = StringUtils.cleanPath(System.currentTimeMillis() + "_" + file.getOriginalFilename());
                        Path uploadPath = Paths.get("src/main/resources/static/images/bukti_pencairan/");
            
                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }
            
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                        fileNames.add("/images/bukti_pencairan/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        ra.addFlashAttribute("error", "Gagal mengunggah file bukti.");
                        return "redirect:/pencairan-dana/form/" + idKampanye;
                    }
                }
            }
        }

        PencairanDana pencairan = new PencairanDana();
        pencairan.setId_kampanye(idKampanye);
        pencairan.setId_organisasi(idOrganisasi);
        pencairan.setJumlah_dana(jumlahPencairan);
        pencairan.setNama_bank(namaBank);
        pencairan.setNomor_rekening(nomorRekening);
        pencairan.setNama_pemilik_rekening(namaPemilikRekening.toUpperCase());
        pencairan.setBukti_pendukung(String.join(",", fileNames));
        pencairan.setAlasan_pencairan(alasan);

        boolean success = pencairanDanaModel.savePencairan(pencairan);

        if (success) {
            KampanyeDetailDTO kampanye = kampanyeModel.getDetailKampanyeById(idKampanye);
            // DIPERBAIKI: Menggunakan addAttribute agar menjadi parameter URL
            ra.addAttribute("nama_kampanye", kampanye.getJudul_kampanye());
            return "redirect:/pencairan-dana/konfirmasi";
        } else {
            ra.addFlashAttribute("error", "Gagal mengajukan pencairan dana. Silakan coba lagi.");
            return "redirect:/pencairan-dana/form/" + idKampanye;
        }
    }

    @GetMapping("/pencairan-dana/konfirmasi")
    public ModelAndView konfirmasiPencairan(@RequestParam("nama_kampanye") String namaKampanye) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView("konfirmasi-pencairan", "nama_kampanye", namaKampanye);
    }
}