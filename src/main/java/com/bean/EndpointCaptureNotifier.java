package com.bean;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.event.ExchangeSendingEvent;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;

public class EndpointCaptureNotifier extends EventNotifierSupport{

	@Override
	public void notify(CamelEvent event) throws Exception {
		
		if(!(event instanceof ExchangeSendingEvent)) {
			return;
		}
		
		ExchangeSendingEvent sending = (ExchangeSendingEvent) event;
		Exchange exchange = sending.getExchange();
		
		String endpoint = sending.getEndpoint().getEndpointUri();
		
		List<String> endpoints = exchange.getProperty("targetEndpoints",List.class);
		
		if( endpoints == null ) {
			endpoints = new ArrayList<>();
			exchange.setProperty("targetEndpoints",endpoints);
		}
		
		if( !endpoints.contains(endpoint) ) {
			endpoints.add(endpoint);
		}
	}

	@Override
	public boolean isEnabled(CamelEvent event) {
	    return event instanceof ExchangeSendingEvent;
	}
	
}
