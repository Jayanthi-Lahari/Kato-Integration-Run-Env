package com.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("mailNotificationBean")
public class MailNotificationBean {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFailureNotification(String toMail,
                                        String routeId,
                                        String errorMessage) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("yourgmail@gmail.com");
        message.setTo(toMail);

        message.setSubject("Order File Processing Failure");

        message.setText(
                "File Processing Failed\n\n" +
                "Route : " + routeId + "\n" +
                "Error : " + errorMessage + "\n" +
                "Time  : " + java.time.LocalDateTime.now()
        );

        mailSender.send(message);

        System.out.println("Failure notification email sent");
    }
}