package com.bean;

import java.time.LocalDateTime;

import org.apache.camel.Body;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("notificationBean")
public class NotificationBean {
	
	@Value("${alert.to.email}")
	private String toMail;
	
	public final JavaMailSender mailSender;

	NotificationBean(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void failureNotification(@Body String body) {

	    SimpleMailMessage message = new SimpleMailMessage();

	    message.setFrom("Notification@gmail.com");
	    message.setTo(toMail);

	    message.setSubject("Endpoint Processing Failed");

	    message.setText(
	            "Dear Team,\n\n" +
	            "An error occurred while processing the Endpoint.\n\n" +
	            "Exception Details:\n" +
	            body +
	            "\n\n" +
	            "Time : " + LocalDateTime.now() +
	            "\n\n" +
	            "Regards,\n" +
	            "Endpoint Processing System\n" +
	            "Apache Camel Notification Service"
	    );

	    mailSender.send(message);

	    System.out.println("Failure notification email sent.");
	}

}
