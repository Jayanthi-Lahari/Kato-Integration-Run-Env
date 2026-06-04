package com.common;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("auditProcessor")
public class AuditProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        Exception exception =
                exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        String routeId =
                exchange.getFromRouteId();

        String errorMessage =
                exception.getMessage();

        String body =
                exchange.getIn().getBody(String.class);

        System.out.println("========== AUDIT LOG ==========");

        System.out.println("Timestamp : " + LocalDateTime.now());

        System.out.println("Route ID  : " + routeId);

        System.out.println("Error     : " + errorMessage);

        System.out.println("Payload   : " + body);

        System.out.println("================================");

        // Add Audit Headers

        exchange.getMessage().setHeader("AuditStatus", "FAILED");

        exchange.getMessage().setHeader("AuditTimestamp",
                LocalDateTime.now().toString());
    }
}