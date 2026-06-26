package com.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class AggregationValidationProcessor implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Boolean orderReceived = exchange.getProperty("orderReceived",Boolean.class);
		Boolean paymentReceived = exchange.getProperty("paymentReceived",Boolean.class);
		
		String completedBy =
                exchange.getProperty(
                        "CamelAggregatedCompletedBy",
                        String.class);
		
		System.err.println("completedBy "+completedBy);
		System.err.println("Headers "+exchange.getIn().getHeaders());
		
		if("timeout".equals(completedBy)) {
			
			if(!Boolean.TRUE.equals(orderReceived)) {
				throw new RuntimeException("Order message missing");
			}
			
			if(!Boolean.TRUE.equals(paymentReceived)) {
				throw new RuntimeException("Payment message missing");
			}
			
		}
		
	}

}
