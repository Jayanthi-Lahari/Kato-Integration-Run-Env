package com.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.jms.ConnectionFactory;

@Configuration
public class ActiveMQConfig {
	
	@Value("${spring.activemq.broker-url}")
	private String url;

	@Value("${spring.activemq.user}")
	private String username;

	@Value("${spring.activemq.password}")
	private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory();

        factory.setBrokerURL(url);
        factory.setUserName(username);
        factory.setPassword(password);

        return factory;
    }
}