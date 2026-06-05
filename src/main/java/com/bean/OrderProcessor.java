package com.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component("orderProcessor")
public class OrderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        // =================================================
        // READ BODY AS STRING
        // =================================================

        String jsonBody =
                exchange.getIn().getBody(String.class);

        // =================================================
        // JSON STRING → MAP
        // =================================================

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> order =
                objectMapper.readValue(
                        jsonBody,
                        new TypeReference<Map<String, Object>>() {}
                );

        // =================================================
        // FORCE EXCEPTION FOR TESTING
        // =================================================

        if(order.get("customerName")
                .toString()
                .equalsIgnoreCase("Lahari")) {

            throw new Exception(
                    "Invalid Customer : Lahari Not Allowed"
            );
        }

        // =================================================
        // BUSINESS ENRICHMENT
        // =================================================

        order.put("OrderStatus", "NEW");

        String orderType =
                (String) order.get("orderType");

        if(orderType != null &&
                orderType.equalsIgnoreCase("VIP")) {

            order.put("Priority", "HIGH");

        } else {

            order.put("Priority", "NORMAL");
        }

        order.put("CreatedBy", "CamelSystem");

        order.put("CreatedDate",
                LocalDate.now().toString());

        order.put("SourceSystem",
                "ECOMMERCE");

        // =================================================
        // MAP → XML
        // =================================================

        XmlMapper xmlMapper = new XmlMapper();

        String xml =
                xmlMapper.writeValueAsString(order);

        // =================================================
        // SET XML BODY
        // =================================================

        exchange.getMessage().setBody(xml);
    }
}