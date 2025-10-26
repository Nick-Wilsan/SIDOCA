package com.sidoca.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HalamanPublicController extends BaseController{
    @GetMapping("/aboutUs")
    public String AboutUs() {
        return "aboutUs";
    }
    
    @GetMapping("/pusatBantuan")
    public String PusatBantuan() {
        return "pusatBantuan";
    }

    @GetMapping("/faq")
    public String FAQ() {
        return "faq";
    }

    @GetMapping("/hubungiKami")
    public String hubungiKami() {
        return "hubungiKami";
    }

    @GetMapping("/layanan")
    public String Layanan() {
        return "layanan";
    }

    @GetMapping("/kebijakanPrivasi")
    public String KebijakanPrivasi() {
        return "kebijakanPrivasi";
    }

    @GetMapping("/blog")
    public String Blog() {
        return "blog";
    }
}
