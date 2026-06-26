package com.bean;

import java.time.LocalDateTime;

import org.apache.camel.Body;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("mailNotificationBean")
public class MailNotificationBean {
	
	@Value("${alert.to.email}")
	private String toMail;

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
    
    public void globalNotification(Exchange e) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toMail);

        String routeId = e.getFromRouteId();
        String exchangeId = e.getExchangeId();
        String sourceEndPoint = e.getFromEndpoint() != null ? e.getFromEndpoint().getEndpointUri() : "unknown";
        String startTime = e.getIn().getHeader("startTime",String.class);
        
        Exception exception = e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        
        if(exception == null) {
        	exception = e.getException();
        }
        
        String errorMessage = exception != null ? exception.getMessage() : "Unknown Error";
        String errorType = exception != null ? exception.getClass().getName() : "Unknown Name";
        
        String failedEndpoint = e.getProperty(ExchangePropertyKey.FAILURE_ENDPOINT, String.class);
        String failedRouteId = e.getProperty(ExchangePropertyKey.FAILURE_ROUTE_ID, String.class);
        
        message.setSubject(
        		routeId +   " Processing Failed"
        );
        

        message.setText(

                """
                Dear Team,

                Flow processing has failed.

                ==================================

                Route ID        : %s
                Exchange ID     : %s
                source EndPoint : %s
                Status          : %s
                Start Time      : %s
                Failed Time     : %s
                Error message   : %s
                Error Type      : %s
                Failed RouteId  : %s
                Failed EndPoint : %s
                

                ==================================

                Please investigate the issue.

                Regards,
                Camel Notification Service
                """
                .formatted(
                        routeId,
                        exchangeId,
                        sourceEndPoint,
                        "FAILED",
                        startTime,
                        LocalDateTime.now().toString(),
                        errorMessage,
                        errorType,
                        failedRouteId,
                        failedEndpoint
                       
                )
        );

        mailSender.send(message);

        System.out.println("====================================");
        System.out.println("Failure notification email sent");
        System.out.println("====================================");
    }
    
}