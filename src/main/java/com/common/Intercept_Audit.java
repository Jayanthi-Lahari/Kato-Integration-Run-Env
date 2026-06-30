package com.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.bson.Document;

public class Intercept_Audit {
	
	public void captureStartAudit(Exchange exchange) {

	    exchange.setProperty("exchangeId", exchange.getExchangeId());

	    exchange.setProperty("routeId", exchange.getFromRouteId());

	    exchange.setProperty("sourceEndpoint",
	            exchange.getFromEndpoint().getEndpointUri());

	    exchange.setProperty("startTime",
	            LocalDateTime.now().toString());

	    exchange.setProperty("status", "IN_PROGRESS");
	}
	
	@SuppressWarnings("unchecked")
	public void captureSendToEndpoint(Exchange exchange) {

	    String endpoint = exchange.getIn()
	            .getHeader("CamelInterceptedEndpoint", String.class);

	    if (endpoint == null) {
	        return;
	    }

	    if (endpoint.startsWith("bean:")
	            || endpoint.startsWith("log:")
	            || endpoint.startsWith("mongodb")) {
	        return;
	    }

	    List<String> targetEndpoints =
	            exchange.getProperty("targetEndpoints", List.class);

	    if (targetEndpoints == null) {
	        targetEndpoints = new ArrayList<>();
	        exchange.setProperty("targetEndpoints", targetEndpoints);
	    }

	    if (!targetEndpoints.contains(endpoint)) {
	        targetEndpoints.add(endpoint);
	    }
	}

}
