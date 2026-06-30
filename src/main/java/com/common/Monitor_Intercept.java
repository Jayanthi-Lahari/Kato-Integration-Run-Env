package com.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.bson.Document;

public class Monitor_Intercept {

    /*
     * SUCCESS AUDIT
     */
    public Document auditHandler(Exchange exchange) {

        Document audit = new Document();

        audit.append("exchangeId",
                exchange.getProperty("exchangeId", String.class));

        audit.append("routeId",
                exchange.getProperty("routeId", String.class));

        audit.append("sourceEndpoint",
                exchange.getProperty("sourceEndpoint", String.class));

        audit.append("targetEndpoints",
                exchange.getProperty("targetEndpoints", List.class));

        audit.append("startTime",
                exchange.getProperty("startTime", String.class));

        audit.append("endTime",
                LocalDateTime.now().toString());

        audit.append("status", "SUCCESS");

        audit.append("processingTimeMs",
                System.currentTimeMillis() - exchange.getCreated());

        audit.append("payload",
                exchange.getMessage().getBody(String.class));

        // Store all message headers
        audit.append("headers",
                new Document(exchange.getMessage().getHeaders()));

        exchange.getMessage().setBody(audit);

        return audit;
    }

    /*
     * EXCEPTION AUDIT
     */
    public Document exceptionHandler(Exchange exchange) {

        Exception exception =
                exchange.getProperty(
                        Exchange.EXCEPTION_CAUGHT,
                        Exception.class);

        if (exception == null) {
            exception = exchange.getException();
        }

        String failedEndpoint =
                exchange.getProperty(
                        ExchangePropertyKey.FAILURE_ENDPOINT,
                        String.class);

        String failedRouteId =
                exchange.getProperty(
                        ExchangePropertyKey.FAILURE_ROUTE_ID,
                        String.class);

        String payload =
                exchange.getMessage().getBody(String.class);

        if (payload != null && payload.length() > 5000) {
            payload = payload.substring(0, 5000);
        }

        long processingTime =
                System.currentTimeMillis() - exchange.getCreated();

        Document doc = new Document()

                .append("exchangeId",
                        exchange.getProperty("exchangeId", String.class))

                .append("routeId",
                        exchange.getProperty("routeId", String.class))

                .append("status", "FAILED")

                .append("startTime",
                        exchange.getProperty("startTime", String.class))

                .append("endTime",
                        LocalDateTime.now().toString())

                .append("processingTimeMs",
                        processingTime)

                .append("sourceEndpoint",
                        exchange.getProperty("sourceEndpoint", String.class))

                .append("targetEndpoints",
                        exchange.getProperty("targetEndpoints", List.class))

                .append("failedEndpoint",
                        failedEndpoint)

                .append("failedRouteId",
                        failedRouteId)

                .append("exceptionType",
                        exception != null
                                ? exception.getClass().getName()
                                : "Unknown")

                .append("exceptionMessage",
                        exception != null
                                ? exception.getMessage()
                                : "Unknown Error")

                .append("payload",
                        payload)

                // Store all message headers
                .append("headers",
                        new Document(exchange.getMessage().getHeaders()));

        exchange.getMessage().setBody(doc);

        return doc;
    }

    /*
     * OPTIONAL - START AUDIT
     */
    public Document startAudit(Exchange exchange) {

        Document audit = new Document()

                .append("exchangeId",
                        exchange.getExchangeId())

                .append("routeId",
                        exchange.getFromRouteId())

                .append("status",
                        "IN_PROGRESS")

                .append("startTime",
                        exchange.getProperty("startTime"))

                .append("sourceEndpoint",
                        exchange.getProperty("sourceEndpoint"))

                .append("payload",
                        exchange.getMessage().getBody(String.class))

                .append("headers",
                        new Document(exchange.getMessage().getHeaders()));

        exchange.getMessage().setBody(audit);

        return audit;
    }
}