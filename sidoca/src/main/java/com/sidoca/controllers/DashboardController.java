package com.sidoca.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // Cek session - jika belum login, redirect ke login
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        // Tambahkan data user ke model
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("role", session.getAttribute("role"));

        return "dashboard";
    }
}
