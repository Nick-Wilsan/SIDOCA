package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DonaturModel;
import com.sidoca.Models.LeaderboardModel; // Import model baru
import com.sidoca.Models.DTO.LeaderboardDTO; // Import DTO baru

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LeaderboardController extends BaseController{
    private final HttpSession session;

    @Autowired
    private DonaturModel donaturModel;
    
    @Autowired
    private LeaderboardModel leaderboardModel; // Inject model baru

    public LeaderboardController(HttpSession session) {
        this.session = session;
    }

    @GetMapping("/leaderboard")
    public ModelAndView Leaderboard() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
            // Redirect ke dashboard role lain jika bukan donatur
            return new ModelAndView("redirect:/dashboard"); 
        }

        // 1. Ambil data 10 donatur teratas
        List<LeaderboardDTO> topDonaturs = leaderboardModel.getTopDonaturs(10);
        
        // 2. Ambil data ranking pengguna yang login
        LeaderboardDTO myRanking = leaderboardModel.getMyRanking(user.getId_akun());
        
        // Jika tidak ada donatur di top 3, tambahkan placeholder anonim
        while (topDonaturs.size() < 3) {
            LeaderboardDTO placeholder = new LeaderboardDTO();
            placeholder.setNamaDonatur("-"); // Atau "Belum Terisi"
            placeholder.setTotalDonasi(java.math.BigDecimal.ZERO);
            
            placeholder.setPhotoProfile("1_default.png"); 
            
            placeholder.setPeringkat(topDonaturs.size() + 1);
            topDonaturs.add(placeholder);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("Judul", "Leaderboard Donatur");
        data.put("nama", user.getNama());
        data.put("topDonaturs", topDonaturs); // List lengkap donatur teratas
        data.put("myRanking", myRanking);     // Peringkat saya
        
        return loadView("leaderboard", data);
    }
}