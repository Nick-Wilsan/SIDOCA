package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.services.MidtransService;
import com.midtrans.httpclient.error.MidtransError;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class DonasiController extends BaseController {
    
    // Biarkan Spring Boot yang mengelola sesi dan service
    @Autowired
    private HttpSession session;

    @Autowired
    private MidtransService midtransService;

    // HAPUS CONSTRUCTOR LAMA
    // public DonasiController(HttpSession session) {
    //     this.session = session;
    // }

    @GetMapping("/riwayatDonasi")
    public ModelAndView RiwayatDonasi() {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/");
        }
        if (!"donatur".equals(user.getRole())) {
            return new ModelAndView("redirect:/dashboard");
        }
        return loadView("riwayatDonasi", java.util.Map.of("Judul", "Dashboard Donatur", "nama", user.getNama()));
    }

    @PostMapping("/donasi/proses")
    public String prosesDonasi(@RequestParam("id_kampanye") int idKampanye,
                               @RequestParam("judul_kampanye") String judulKampanye,
                               @RequestParam("nominal") double nominal,
                               RedirectAttributes ra) {
        
        Akun user = (Akun) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        String orderId = "SIDOCA-" + idKampanye + "-" + System.currentTimeMillis();

        try {
            String snapToken = midtransService.createSnapToken(
                orderId, 
                nominal, 
                "Donasi untuk: " + judulKampanye, 
                user.getNama(), 
                user.getEmail()
            );

            ra.addFlashAttribute("snapToken", snapToken);
            ra.addFlashAttribute("orderId", orderId);
            ra.addFlashAttribute("nominal", nominal);
            return "redirect:/donasi/konfirmasi";

        } catch (MidtransError e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Gagal membuat transaksi: " + e.getMessage());
            return "redirect:/kampanye/" + idKampanye;
        }
    }

    @GetMapping("/donasi/konfirmasi")
    public ModelAndView konfirmasiDonasi() {
        return new ModelAndView("donasiKonfirmasi");
    }

    @PostMapping("/donasi/notifikasi")
    public ResponseEntity<String> handleMidtransNotification(@RequestBody Map<String, Object> notificationPayload) {
        String orderId = (String) notificationPayload.get("order_id");
        String transactionStatus = (String) notificationPayload.get("transaction_status");
        String fraudStatus = (String) notificationPayload.get("fraud_status");

        System.out.println("Menerima notifikasi untuk Order ID: " + orderId + " dengan status: " + transactionStatus);

        String newStatus = "pending";

        if ("capture".equals(transactionStatus)) {
            if ("accept".equals(fraudStatus)) {
                newStatus = "berhasil";
            }
        } else if ("settlement".equals(transactionStatus)) {
            newStatus = "berhasil";
        } else if ("cancel".equals(transactionStatus) || "deny".equals(transactionStatus) || "expire".equals(transactionStatus)) {
            newStatus = "gagal";
        }

        System.out.println("Memperbarui status Order ID: " + orderId + " menjadi " + newStatus);
        
        // Di sini Anda perlu menambahkan logika untuk update database
        // Contoh: donasiModel.updateStatusByOrderId(orderId, newStatus);

        return new ResponseEntity<>("Notification received.", HttpStatus.OK);
    }
}