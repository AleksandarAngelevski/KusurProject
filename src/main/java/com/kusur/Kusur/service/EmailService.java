package com.kusur.Kusur.service;

import com.kusur.Kusur.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment environment;


    public void sendVerificationEmail(User user){

        String domain = System.getProperty("DOMAIN_NAME");
        System.out.println(domain);
        String subject = "Verification Email";
        String verificationLink = "http://"+domain+"/verify?token="+ user.getVerificationToken();
        String message = "Click the link to verify your account:\n"+verificationLink;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("acemladost2004@gmail.com");

        mailSender.send(mailMessage);
    }
}
