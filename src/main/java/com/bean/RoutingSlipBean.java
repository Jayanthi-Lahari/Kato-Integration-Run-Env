package com.bean;

import java.util.Map;

import org.apache.camel.Exchange;

public class RoutingSlipBean {
	
	private static final Map<String, String> routingMap = Map.of(
			"Leave", "direct:manager,activemq:queue:hr",
		    "Purchase", "direct:manager,direct:finance,activemq:queue:procurement",
		    "Travel", "direct:manager,direct:finance,activemq:queue:travelDesk"
			);
	
	
	public void buildSlip(Exchange exchange) {
		
		String type = exchange.getIn().getHeader("documentType", String.class);
		
		exchange.getIn().setHeader("routingSlip", routingMap.getOrDefault(type, "direct:manager"));
		
	}

}
