package com.sidoca.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Kode Verifikasi Pendaftaran SIDOCA");
            message.setText("Kode verifikasi Anda adalah: " + code);
            mailSender.send(message);
        } catch (Exception e) {
            // Untuk pengembangan, jika email gagal dikirim, cetak di konsol
            System.out.println("====================================");
            System.out.println("Gagal mengirim email ke: " + to);
            System.out.println("Kode verifikasi: " + code);
            System.out.println("Error: " + e.getMessage());
            System.out.println("====================================");
        }
    }
}