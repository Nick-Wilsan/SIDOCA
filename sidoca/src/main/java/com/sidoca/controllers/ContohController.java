package com.sidoca.controllers;

// ngambil class user
import com.sidoca.Models.DataBaseClass.Akun;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sidoca.Models.ContohModel;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ContohController extends BaseController{
    private final HttpSession session;

    // ngambil session
    public ContohController(HttpSession session) {
        this.session = session;
    }

    // cek session
    // @GetMapping("/dashboard")
    // public ModelAndView index() {
    //     // Sama seperti session_start + redirect di PHP
    //     if (session.getAttribute("user") == null) {
    //         return new ModelAndView("redirect:/login");
    //     }

    //     Map<String, Object> data = new HashMap<>();
    //     data.put("judul", "Halaman dashboard");

    //     return loadView("dashboard", data);
    // }


    @Autowired
    private ContohModel contohModel;

    @GetMapping("/user")
    public ModelAndView getUserByUsername(@RequestParam String username) {
        Akun akun = contohModel.ContohgetByUsername(username);
        return loadView("user", java.util.Map.of("user", akun));
    }
}
