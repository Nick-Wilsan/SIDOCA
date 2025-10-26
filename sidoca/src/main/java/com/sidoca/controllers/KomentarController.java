package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DataBaseClass.Akun;

import jakarta.servlet.http.HttpSession;

@Controller
public class KomentarController extends BaseController{
    private final HttpSession session;

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
        
        // Langsung gunakan id_akun dari user yang login
        int idAkun = loggedInUser.getId_akun();

        if (isiKomentar != null && !isiKomentar.trim().isEmpty()) {
            kampanyeModel.tambahKomentar(idKampanye, idAkun, isiKomentar);
        }

        return new ModelAndView("redirect:/kampanye/" + idKampanye + "#komentarSection");
    }
}