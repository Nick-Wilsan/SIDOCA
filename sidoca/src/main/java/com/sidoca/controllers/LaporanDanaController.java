package com.sidoca.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sidoca.Models.DataBaseClass.Akun;

import jakarta.servlet.http.HttpSession;

@Controller
public class LaporanDanaController extends BaseController{
    
    private final HttpSession session;

    public LaporanDanaController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/laporanPenggunaanDana")
    public ModelAndView LaporanPenggunaanDana() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
        return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
        return new ModelAndView("redirect:/dashboard");
        }
        return loadView("laporanPenggunaanDana", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }
}
