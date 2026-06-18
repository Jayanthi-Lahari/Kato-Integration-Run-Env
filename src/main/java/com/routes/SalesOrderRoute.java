package com.routes;


import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("java")
public class SalesOrderRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Producer Route
        from("file-watch:src/main/resources/input")
            .routeId("route-3248")

            .log("Received Order = ${body}")

            .unmarshal().json()

            .multicast()

                .to("mongodb:mongoBean"
                        + "?database=contactsDB"
                        + "&collection=orders"
                        + "&operation=insert")

                .to("activemq:topic:salesDetails"
                        + "?disableReplyTo=true"
                        + "&jmsMessageType=Text"
                        + "&connectionFactory=#connectionFactory"
                        + "&durableSubscriptionName=MongoSubscriber")

            .end();


        // Subscriber Route
        from("activemq:topic:salesDetails"
                + "?connectionFactory=#connectionFactory")

            .routeId("route-3248-2156")

            .log("Received from Topic = ${body}")

            .setBody(simple(
                "Order Notification:\n${body}"
            ))

            .to("file:src/main/resources/output"
                + "?fileName=report-${date:now:yyyyMMdd}.txt");
    }
}