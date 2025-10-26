package com.sidoca.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sidoca.Models.DataBaseClass.Akun;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController extends BaseController{
    private final HttpSession session;

    public ProfileController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/profil")
    public String profil() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        Akun user = (Akun) session.getAttribute("user");
        String role = user.getRole();

        switch (role) {
            case "donatur":
                return "redirect:/profilDonatur";
            case "organisasi":
                return "redirect:/profilOrganisasi";
            case "admin":
                return "redirect:/profilAdmin";
            default:
                return "redirect:/";
        }
    }

    @GetMapping("/profilDonatur")
    public ModelAndView profilDonatur() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("profilDonatur", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }

    @GetMapping("/profilOrganisasi")
    public ModelAndView profilOrganisasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("profilOrganisasi", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama()));
    }

    @GetMapping("/profilAdmin")
    public ModelAndView profilAdmin() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("profilAdmin", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }
}
