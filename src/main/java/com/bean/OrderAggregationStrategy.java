package com.bean;

import java.util.Map;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.util.json.JsonObject;
//import org.springframework.stereotype.Component;

//@Component("OrderAggregationStrategy")
public class OrderAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(
	        Exchange oldExchange,
	        Exchange newExchange) {

//	    logExchange("OLD", oldExchange);
//	    logExchange("NEW", newExchange);

	    if (oldExchange == null) {
	        return newExchange;
	    }

	    // Merge arrival flags
	    Boolean oldOrder =
	            oldExchange.getProperty(
	                    "orderReceived",
	                    Boolean.class);

	    Boolean oldPayment =
	            oldExchange.getProperty(
	                    "paymentReceived",
	                    Boolean.class);

	    Boolean newOrder =
	            newExchange.getProperty(
	                    "orderReceived",
	                    Boolean.class);

	    Boolean newPayment =
	            newExchange.getProperty(
	                    "paymentReceived",
	                    Boolean.class);

	    oldExchange.setProperty(
	            "orderReceived",
	            Boolean.TRUE.equals(oldOrder)
	                    || Boolean.TRUE.equals(newOrder));

	    oldExchange.setProperty(
	            "paymentReceived",
	            Boolean.TRUE.equals(oldPayment)
	                    || Boolean.TRUE.equals(newPayment));

	    // Read incoming JSON bodies
	    Map<String, Object> first = oldExchange.getIn().getBody(Map.class);

	    Map<String, Object> second = newExchange.getIn().getBody(Map.class);

	    Map<String, Object> order;
	    Map<String, Object> payment;

	    // Detect message type
	    if (first.containsKey("customerId")) {
	        order = first;
	        payment = second;
	    } else {
	        order = second;
	        payment = first;
	    }


	    

	    // Final document
	    JsonObject confirm =
	            new JsonObject();

	    confirm.put(
	            "orderId",
	            order.get("orderId"));
	    
	    order.remove("orderId");
	    payment.remove("orderId");

	    confirm.put(
	            "order",
	            order);

	    confirm.put(
	            "payment",
	            payment);

	    oldExchange.getIn()
	            .setBody(confirm);

	    System.out.println(
	            "Combined JSON = "
	                    + confirm.toJson());

	    return oldExchange;
	}

	private void logExchange(String name, Exchange exchange) {

	    System.out.println("\n========== " + name + " EXCHANGE ==========");

	    if (exchange == null) {
	        System.out.println("NULL");
	        return;
	    }

	    System.out.println("ExchangeId : " + exchange.getExchangeId());
	    System.out.println("RouteId    : " + exchange.getFromRouteId());

	    System.out.println("\nBody:");
	    System.out.println(exchange.getIn().getBody());

	    System.out.println("\nHeaders:");
	    exchange.getIn().getHeaders()
	            .forEach((k, v) -> System.out.println(k + " = " + v));

	    System.out.println("\nProperties:");
	    exchange.getProperties()
	            .forEach((k, v) -> System.out.println(k + " = " + v));

	    System.out.println("==================================");
	    String one = exchange.getProperty(
	    	    "CamelAggregatedCompletedBy",
	    	    String.class);
	    
	   

	    	System.out.println("CamelAggregatedCompletedBy "+one);

	}
}