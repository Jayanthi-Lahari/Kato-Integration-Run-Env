package com.config;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bean.EndpointCaptureNotifier;

@Configuration
public class CamelConfiguration {

    @Bean
    public EndpointCaptureNotifier endpointAuditNotifier() {
        return new EndpointCaptureNotifier();
    }

    @Bean
    public CamelContextConfiguration camelContextConfiguration(
    		EndpointCaptureNotifier notifier) {

        return new CamelContextConfiguration() {

            @Override
            public void beforeApplicationStart(CamelContext context) {

                context.getManagementStrategy()
                       .addEventNotifier(notifier);
            }

            @Override
            public void afterApplicationStart(CamelContext context) {
            }
        };
    }
}