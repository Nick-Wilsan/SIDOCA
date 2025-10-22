package com.sidoca.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Untuk mengembalikan halaman
@Controller
// Menuju localhost:8000
@RequestMapping("")
public class HomeController {
    
    @GetMapping
    // Fungsi biasanya untuk mengembalikan halaman
    // Model berguna untuk menghubungkan antara data controller yang akan ditampilkan ke UI
    public String welcome(Model model) {
        String messages = "Welcome to SIDOCA with Nick!";
        // String messages disimpan ke atribut pesan didalam context model
        model.addAttribute("pesan", messages);
        
        // Mencari file index.html di dalam folder templates
        return "index";
    }
}
