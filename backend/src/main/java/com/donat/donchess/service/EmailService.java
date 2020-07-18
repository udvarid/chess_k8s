package com.donat.donchess.service;

import com.donat.donchess.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String MESSAGE_FROM;

    @Value("${spring.mail.registration}")
    private String registrationAdress;

    private JavaMailSender javaMailSender;


    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendAuthenticatonMail(User newUser) {
        SimpleMailMessage message;

        try {
            message = new SimpleMailMessage();
            message.setFrom(MESSAGE_FROM);
            message.setTo(newUser.getEmail());
            message.setSubject("DonChess registration - authentication");
            message.setText("Dear " + newUser.getFullname() + "! \n \n A registration has been just made with your email. If you did the registration, please confirm it and click here: \n " + registrationAdress + "/api/user/confirmation?token=" + newUser.getAuthenticationToken());
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
