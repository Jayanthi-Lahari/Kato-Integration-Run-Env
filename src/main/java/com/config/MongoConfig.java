package com.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    // ==========================================
    // READ URL FROM application.properties
    // ==========================================

    @Value("${spring.data.mongodb.uri}")
    private String mongoUrl;

    // ==========================================
    // CREATE MONGO CLIENT BEAN
    // ==========================================

    @Bean("mongoBean")
    public MongoClient mongoClient() {

        try {
        	System.out.println("==================mongoUrl==================");

            MongoClient client = MongoClients.create(mongoUrl);

            System.out.println("====================================");
            System.out.println(" MongoDB Connected Successfully ");
            System.out.println(" URL : " + mongoUrl);
            System.out.println("====================================");

            System.out.println("Available Databases:");

            client.listDatabaseNames()
                    .forEach(System.out::println);

            System.out.println("====================================");

            return client;

        } catch (Exception e) {

            System.out.println("====================================");
            System.out.println(" MongoDB Connection Failed ");
            System.out.println(" Error : " + e.getMessage());
            System.out.println("====================================");

            throw e;
        }
    }
}