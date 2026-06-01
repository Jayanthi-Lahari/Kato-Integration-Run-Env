package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    @Bean("mongoBean")
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}