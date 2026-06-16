//package com.config;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import jakarta.jms.Connection;
//import jakarta.jms.ConnectionFactory;
//
//@Configuration
//public class ActiveMQConfig {
//
//    @Value("${spring.activemq.broker-url}")
//    private String url;
//
//    @Value("${spring.activemq.user}")
//    private String username;
//
//    @Value("${spring.activemq.password}")
//    private String password;
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//
//        try {
//
//            ActiveMQConnectionFactory factory =
//                    new ActiveMQConnectionFactory();
//
//            factory.setBrokerURL(url);
//            factory.setUserName(username);
//            factory.setPassword(password);
//
//            // Test Connection
//            Connection connection = factory.createConnection();
//            connection.start();
//
//            System.out.println("=================================");
//            System.out.println("ActiveMQ Connected Successfully!");
//            System.out.println("Broker URL : " + url);
//            System.out.println("Username   : " + username);
//            System.out.println("=================================");
//
//            connection.close();
//
//            return factory;
//
//        } catch (Exception e) {
//
//            System.out.println("=================================");
//            System.out.println("ActiveMQ Connection Failed!");
//            System.out.println("Error : " + e.getMessage());
//            System.out.println("=================================");
//
//            throw new RuntimeException(e);
//        }
//    }
//}
