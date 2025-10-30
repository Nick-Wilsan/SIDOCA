package com.sidoca.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DataBaseClass.Kampanye;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.OrganisasiModel;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController extends BaseController{
    private final HttpSession session;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private OrganisasiModel organisasiModel;

    public DashboardController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        Akun user = (Akun) session.getAttribute("user");
        String role = user.getRole();

        switch (role) {
            case "donatur":
                return "redirect:/dashboardDonatur";
            case "organisasi":
                return "redirect:/dashboardOrganisasi";
            case "admin":
                return "redirect:/dashboardAdmin";
            default:
                return "redirect:/";
        }
    }

    @GetMapping("/dashboardDonatur")
    public ModelAndView dashboardDonatur() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("dashboardDonatur", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }

    @GetMapping("/dashboardOrganisasi")
    public ModelAndView dashboardOrganisasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"organisasi".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }

        int id_organisasi = organisasiModel.GetIdOrganisasiByIdAkun(user.getId_akun());
        List<Kampanye> kampanyelList = kampanyeModel.GetAllKampanyeByOrganisasi(id_organisasi);
        String namaOrganisasi = organisasiModel.GetNamaOrganisasiById(id_organisasi);

        return loadView("dashboardOrganisasi", java.util.Map.of("Judul", "Dashboard Organisasi", "nama", user.getNama(), "kampanyeList", kampanyelList, "namaOrganisasi", namaOrganisasi));
    }

    @GetMapping("/dashboardAdmin")
    public ModelAndView dashboardAdmin() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"admin".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("dashboardAdmin", java.util.Map.of("Judul", "Dashboard Admin", "nama", user.getNama()));
    }
}
