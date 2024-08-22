package com.umiverse.umiversebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:5173/verify-user?token=" + token;
        String message = "Please click the link below to verify your email address:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("umiestsender@gmail.com");

        mailSender.send(email);
    }
}
