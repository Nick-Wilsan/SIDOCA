package com.sidoca.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sidoca.Models.DataBaseClass.Akun;
import com.sidoca.Models.DonaturModel;
import com.sidoca.services.MidtransService;
import com.midtrans.httpclient.error.MidtransError;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DonasiModel;
import com.sidoca.Models.DTO.DonasiDTO;


@Controller
public class DonasiController extends BaseController {
    
    @Autowired
    private HttpSession session;

    @Autowired
    private MidtransService midtransService;

    @Autowired
    private KampanyeModel kampanyeModel;

    @Autowired
    private DonaturModel donaturModel;

    @Autowired
    private DonasiModel donasiModel;


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

    @GetMapping("/donasi/{id}")
    public ModelAndView donasi(@PathVariable("id") int idKampanye) {
        if (session.getAttribute("user") == null) {
            return new ModelAndView("redirect:/");
        }
        
        com.sidoca.Models.DTO.KampanyeDetailDTO kampanye = kampanyeModel.getDetailKampanyeById(idKampanye);
        if (kampanye == null) {
            return new ModelAndView("redirect:/daftarKampanye");
        }

        ModelAndView mav = new ModelAndView("donasi");
        mav.addObject("id_kampanye", idKampanye);
        mav.addObject("judul_kampanye", kampanye.getJudul_kampanye());
        return mav;
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

        // Dapatkan id_donatur
        Integer idDonatur = donaturModel.getDonaturIdByAkunId(user.getId_akun());
        if (idDonatur == null) {
            ra.addFlashAttribute("error", "Data donatur tidak ditemukan.");
            return "redirect:/donasi/" + idKampanye;
        }

        String orderId = "SIDOCA-" + idKampanye + "-" + System.currentTimeMillis();

        // Simpan transaksi ke DB dengan status 'pending'
        boolean isSaved = donasiModel.saveDonasi(idDonatur, idKampanye, new BigDecimal(nominal), orderId);
        if (!isSaved) {
            ra.addFlashAttribute("error", "Gagal menyimpan transaksi awal.");
            return "redirect:/donasi/" + idKampanye;
        }

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
            return "redirect:/donasi/" + idKampanye;
        }
    }

    @GetMapping("/donasi/konfirmasi")
    public ModelAndView konfirmasiDonasi() {
        return new ModelAndView("donasiKonfirmasi");
    }

    // ENDPOINT BARU UNTUK MENANGANI REDIRECT DARI MIDTRANS
    @GetMapping("/donasi/status")
    public ModelAndView donasiStatus(@RequestParam("order_id") String orderId,
                                    @RequestParam("status") String status) {
        
        ModelAndView mav = new ModelAndView("donasiStatus");
        DonasiDTO donasiInfo = donasiModel.getDonasiAndKampanyeByOrderId(orderId);

        mav.addObject("status", status);
        if (donasiInfo != null) {
            mav.addObject("namaKampanye", donasiInfo.getNamaKampanye());
            mav.addObject("jumlahDonasi", donasiInfo.getNominalDonasi());
        }

        return mav;
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
        
        // Update status di database
        donasiModel.updateStatusByOrderId(orderId, newStatus);

        return new ResponseEntity<>("Notification received.", HttpStatus.OK);
    }
}