package com.sidoca.controllers;

import jakarta.servlet.http.HttpSession;
// import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

public class ContohController extends BaseController{
    private final HttpSession session;

    public ContohController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/dashboard")
    public ModelAndView index() {
        // Sama seperti session_start + redirect di PHP
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/login");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("judul", "Halaman dashboard");

        return loadView("dashboard", data);
    }
}
