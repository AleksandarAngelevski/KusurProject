package com.kusur.Kusur.service;

import com.kusur.Kusur.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.security.auth.Subject;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;


    public void sendVerificationEmail(User user){
        String subject = "Verification Email";
        String verificationLink = "http://localhost:8080/verify?token="+ user.getVerificationToken();
        String message = "Click the link to verify your account:\n"+verificationLink;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("acemladost2004@gmail.com");

        mailSender.send(mailMessage);
    }
}
