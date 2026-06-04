package com.bean;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.LocalDate;
import java.util.TreeMap;

public class OrderProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        TreeMap<String, Object> order =
                (TreeMap<String, Object>) exchange.getIn().getBody(Object.class);

        // Add Default Values

        order.put("OrderStatus", "NEW");

        // VIP Logic

        String orderType = (String) order.get("orderType");

        if(orderType != null && orderType.equalsIgnoreCase("VIP")) {

            order.put("Priority", "HIGH");

        } else {

            order.put("Priority", "NORMAL");
        }

        // Enrichment

        order.put("CreatedBy", "CamelSystem");

        order.put("CreatedDate", LocalDate.now().toString());

        order.put("SourceSystem", "ECOMMERCE");

        // Set Modified Body Back

        exchange.getMessage().setBody(order);
    }
}