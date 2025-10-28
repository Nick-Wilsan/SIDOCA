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
import java.util.HashMap;
import java.util.Map;
import com.sidoca.Models.KampanyeModel;
import com.sidoca.Models.DonasiModel;
import com.sidoca.Models.DTO.DonasiDTO;
import com.sidoca.Models.DTO.RiwayatDonasiSummaryDTO;

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
        if (user == null || !"donatur".equals(user.getRole())) {
            return new ModelAndView("redirect:/");
        }
        
        Integer idDonatur = donaturModel.getDonaturIdByAkunId(user.getId_akun());
        if (idDonatur == null) {
            // Handle jika data donatur tidak ditemukan
            return new ModelAndView("redirect:/dashboard");
        }

        RiwayatDonasiSummaryDTO riwayat = donasiModel.getRiwayatDonasi(idDonatur);

        Map<String, Object> data = new HashMap<>();
        data.put("Judul", "Riwayat Donasi");
        data.put("nama", user.getNama());
        data.put("riwayat", riwayat);
        
        return loadView("riwayatDonasi", data);
    }

    @GetMapping("/donasi/{id}")
    public ModelAndView donasi(@PathVariable("id") int idKampanye) {
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"donatur".equals(user.getRole())) {
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
                                @RequestParam(name = "anonim", required = false) boolean anonim,
                                RedirectAttributes ra) {
                                
        Akun user = (Akun) session.getAttribute("user");
        if (user == null || !"donatur".equals(user.getRole())) {
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
        boolean isSaved = donasiModel.saveDonasi(idDonatur, idKampanye, new BigDecimal(nominal), orderId, anonim);
        if (!isSaved) {
            ra.addFlashAttribute("error", "Gagal menyimpan transaksi awal.");
            return "redirect:/donasi/" + idKampanye;
        }

        try {
            String snapToken = midtransService.createSnapToken(
                orderId, 
                nominal, // Kirim nominal asli ke service
                "Donasi untuk: " + judulKampanye, 
                user.getNama(), 
                user.getEmail()
            );

            ra.addFlashAttribute("snapToken", snapToken);
            ra.addFlashAttribute("orderId", orderId);
            ra.addFlashAttribute("nominal", nominal);

            // Set session flag sebagai "tiket" untuk mengakses halaman konfirmasi
            session.setAttribute("can_access_konfirmasi", true);
            
            // Tambahkan tiket untuk mengakses halaman status
            session.setAttribute("can_access_status", true);

            return "redirect:/donasi/konfirmasi";

        } catch (MidtransError e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Gagal membuat transaksi: " + e.getMessage());
            return "redirect:/donasi/" + idKampanye;
        }
    }

    @GetMapping("/donasi/konfirmasi")
    public ModelAndView konfirmasiDonasi() {
        Akun user = (Akun) session.getAttribute("user");
        Boolean canAccess = (Boolean) session.getAttribute("can_access_konfirmasi");

        // Cek user, role, dan "tiket"
        if (user == null || !"donatur".equals(user.getRole()) || canAccess == null || !canAccess) {
            // Jika tidak valid, redirect ke dashboard
            return new ModelAndView("redirect:/dashboard");
        }

        // Hapus "tiket" setelah digunakan agar tidak bisa diakses lagi
        session.removeAttribute("can_access_konfirmasi");

        return new ModelAndView("donasiKonfirmasi");
    }

    @GetMapping("/donasi/status")
    public ModelAndView donasiStatus(@RequestParam("order_id") String orderId,
                                    @RequestParam("status") String status) {
        
        Akun user = (Akun) session.getAttribute("user");
        Boolean canAccess = (Boolean) session.getAttribute("can_access_status");
        
        // Cek user, role, dan "tiket" untuk halaman status
        if (user == null || !"donatur".equals(user.getRole()) || canAccess == null || !canAccess) {
            return new ModelAndView("redirect:/");
        }

        // Hapus "tiket" setelah digunakan
        session.removeAttribute("can_access_status");

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
        String paymentType = (String) notificationPayload.get("payment_type");

        String newStatus = "pending";
        boolean isSuccess = false;

        if ("capture".equals(transactionStatus)) {
            if ("accept".equals(fraudStatus)) {
                newStatus = "berhasil";
                isSuccess = true;
            }
        } else if ("settlement".equals(transactionStatus)) {
            newStatus = "berhasil";
            isSuccess = true;
        } else if ("cancel".equals(transactionStatus) || "deny".equals(transactionStatus) || "expire".equals(transactionStatus)) {
            newStatus = "gagal";
        }

        boolean statusUpdated = donasiModel.updateStatusAndPaymentMethodByOrderId(orderId, newStatus, paymentType);

        if (isSuccess && statusUpdated) {
            DonasiDTO donasiInfo = donasiModel.getDonasiAndKampanyeByOrderId(orderId);
            if (donasiInfo != null) {
                // Update dana terkumpul di kampanye
                kampanyeModel.updateDanaTerkumpul(donasiInfo.getIdKampanye(), donasiInfo.getNominalDonasi());
                
                // Hitung dan simpan biaya admin
                BigDecimal adminFee = donasiInfo.getNominalDonasi().multiply(new BigDecimal("0.1"));
                donasiModel.saveBiayaAdmin(donasiInfo.getIdDonasi(), donasiInfo.getIdDonatur(), donasiInfo.getIdKampanye(), adminFee);
                donaturModel.updateTotalDonasi(donasiInfo.getIdDonatur(), donasiInfo.getNominalDonasi());
            }
        }

        return new ResponseEntity<>("Notification received.", HttpStatus.OK);
    }
}