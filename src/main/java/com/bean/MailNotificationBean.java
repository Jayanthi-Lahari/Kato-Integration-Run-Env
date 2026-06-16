package com.bean;

import org.apache.camel.Header;
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

        message.setFrom("Notification@gmail.com");
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
    

    public void failureNotification(

            String toMail,

            @Header("routeId") String routeId,

            @Header("exchangeId") String exchangeId,

            @Header("CamelFileName") String fileName,

            @Header("startTime") String startTime,

            @Header("errorMessage") String errorMessage,

            @Header("failedTime") String failedTime

    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("Notification@gmail.com");

        message.setTo(toMail);

        message.setSubject(
                "Order File Processing Failed"
        );

        message.setText(

                """
                Dear Team,

                File processing has failed.

                ==================================

                Route ID    : %s
                Exchange ID : %s
                File Name   : %s
                Status      : %s
                Start Time  : %s
                Error       : %s
                Failed Time : %s

                ==================================

                Please investigate the issue.

                Regards,
                Camel Notification Service
                """
                .formatted(
                        routeId,
                        exchangeId,
                        fileName,
                        "FAILED",
                        startTime,
                        errorMessage,
                        failedTime
                )
        );

        mailSender.send(message);

        System.out.println("====================================");
        System.out.println("Failure notification email sent");
        System.out.println("====================================");
    }
    
}