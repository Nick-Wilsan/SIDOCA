package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.sidoca.Models.ProfilModel;
import com.sidoca.Models.DTO.ProfilDTO;
import com.sidoca.Models.DataBaseClass.Akun;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProfileController extends BaseController {
    private final HttpSession session;

    @Autowired
    private ProfilModel profilModel;

    public ProfileController(HttpSession session) {
        this.session = session;
    }

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
}