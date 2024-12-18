package com.example.project_online_store_bab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPurchaseConfirmation(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Письмо отправлено на " + to);
        } catch (Exception e) {
            System.err.println("Ошибка отправки письма: " + e.getMessage());
            throw new RuntimeException("Ошибка отправки письма.");
        }
    }
}