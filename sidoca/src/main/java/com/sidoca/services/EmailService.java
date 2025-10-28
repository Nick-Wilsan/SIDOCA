package com.sidoca.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException; 
import jakarta.mail.internet.MimeMessage;  


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = buildEmailTemplate("Kode Verifikasi Akun SIDOCA Anda", 
                                                    "Terima kasih telah mendaftar di SIDOCA. Gunakan kode berikut untuk memverifikasi email Anda:", 
                                                    code);
            
            helper.setText(htmlContent, true); 
            helper.setTo(to);
            helper.setSubject("Kode Verifikasi Pendaftaran SIDOCA");

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            System.out.println("====================================");
            System.out.println("Gagal mengirim email HTML ke: " + to);
            System.out.println("Kode verifikasi: " + code);
            System.out.println("Error: " + e.getMessage());
            System.out.println("====================================");
        }
    }

    public void sendPasswordResetEmail(String to, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = buildEmailTemplate("Reset Password Akun SIDOCA", 
                                                    "Kami menerima permintaan untuk mereset password Anda. Gunakan kode berikut:", 
                                                    code);

            helper.setText(htmlContent, true); 
            helper.setTo(to);
            helper.setSubject("Kode Reset Password SIDOCA");
            
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            System.out.println("====================================");
            System.out.println("Gagal mengirim email reset password ke: " + to);
            System.out.println("Kode reset: " + code);
            System.out.println("Error: " + e.getMessage());
            System.out.println("====================================");
        }
    }

    private String buildEmailTemplate(String title, String message, String code) {
        return "<!DOCTYPE html>" +
               "<html lang='id'>" +
               "<head>" +
               "    <meta charset='UTF-8'>" +
               "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "    <style>" +
               "        body { font-family: Arial, sans-serif; line-height: 1.6; }" +
               "        .container { width: 90%; max-width: 600px; margin: 20px auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }" +
               "        .header { background-color: #3A5A40; color: white; padding: 20px; text-align: center; }" +
               "        .header h1 { margin: 0; font-size: 24px; }" +
               "        .content { padding: 30px; }" +
               "        .content p { margin-bottom: 20px; }" +
               "        .code-box { background-color: #f4f4f4; border-radius: 5px; padding: 15px 20px; text-align: center; }" +
               "        .code { font-size: 32px; font-weight: bold; letter-spacing: 4px; color: #333; }" +
               "        .footer { background-color: #f9f9f9; color: #777; padding: 20px; text-align: center; font-size: 12px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class='container'>" +
               "        <div class='header'>" +
               "            <h1>" + title + "</h1>" + 
               "        </div>" +
               "        <div class='content'>" +
               "            <p>Halo,</p>" +
               "            <p>" + message + "</p>" + 
               "            <div class='code-box'>" +
               "                <span class='code'>" + code + "</span>" + 
               "            </div>" +
               "            <p style='margin-top: 20px;'>Jika Anda tidak merasa melakukan tindakan ini, harap abaikan email ini.</p>" +
               "        </div>" +
               "        <div class='footer'>" +
               "            <p>&copy; " + java.time.Year.now() + " SIDOCA. Semua hak cipta dilindungi.</p>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }

    public void sendDeleteAccountEmail(String to, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = buildEmailTemplate("Konfirmasi Hapus Akun SIDOCA",
                                                    "Anda telah meminta untuk menghapus akun Anda. Gunakan kode berikut untuk mengonfirmasi:",
                                                    code);
            
            helper.setText(htmlContent, true); 
            helper.setTo(to);
            helper.setSubject("Kode Konfirmasi Hapus Akun SIDOCA");

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            System.out.println("====================================");
            System.out.println("Gagal mengirim email hapus akun ke: " + to);
            System.out.println("Kode: " + code);
            System.out.println("Error: " + e.getMessage());
            System.out.println("====================================");
        }
    }
}