package com.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean("mongoBean")
    public MongoClient mongoClient() {

        try {

            MongoClient client = MongoClients.create(
                "mongodb://admin:admin@localhost:27017/?authSource=admin"
            );

            // Test Connection
            Document result = client
                    .getDatabase("admin")
                    .runCommand(new Document("ping", 1));

            System.out.println("=================================");
            System.out.println("MongoDB Connected Successfully!");
            System.out.println("Ping Result: " + result.toJson());
            System.out.println("=================================");

            return client;

        } catch (Exception e) {

            System.out.println("=================================");
            System.out.println("MongoDB Connection Failed!");
            System.out.println("Error: " + e.getMessage());
            System.out.println("=================================");

            throw e;
        }
    }
}
