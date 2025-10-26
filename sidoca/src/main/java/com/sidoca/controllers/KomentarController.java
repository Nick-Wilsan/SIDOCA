package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sidoca.Models.DonaturModel;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DataBaseClass.Akun;

import jakarta.servlet.http.HttpSession;

@Controller
public class KomentarController extends BaseController{
    private final HttpSession session;

    @Autowired
    private DonaturModel donaturModel;

    @Autowired
    private KampanyeModel kampanyeModel;

    public KomentarController(HttpSession session) {
        this.session = session;
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
}
