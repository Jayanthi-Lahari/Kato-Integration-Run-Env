package com.common;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.bson.Document;
import org.springframework.stereotype.Component;

//@Component("MonitorLogs")
public class MonitorLogs {
	
	public Document audit(Exchange e) {
		
		String routeId = e.getFromRouteId();
		String exchangeId = e.getExchangeId();
		String startTime = e.getIn().getHeader("startTime", String.class);
		String fileName = e.getIn().getHeader("CamelFileName", String.class);
		String folder = e.getIn().getHeader("CamelFileAbsolutePath", String.class);
		String queueName = e.getIn().getHeader("CamelJMSDestinationProduced", String.class);
		
		 return new Document()
	                .append("routeId", routeId)
	                .append("exchangeId", exchangeId)
	                .append("fileName", fileName)
	                .append("status", "SUCCESS")
	                .append("startTime", startTime)
	                .append("endTime", LocalDateTime.now().toString())
	                .append("queueName", queueName);
	}
	
	public Document exceptions(Exchange e) {
			
			String routeId = e.getFromRouteId();
			String exchangeId = e.getExchangeId();
			String startTime = e.getIn().getHeader("startTime", String.class);
			String fileName = e.getIn().getHeader("CamelFileName", String.class);
			String queueName = e.getIn().getHeader("Destination", String.class);
			String fromEndpoint =
		            e.getFromEndpoint() != null
		                    ? e.getFromEndpoint().getEndpointUri()
		                    : null;
			
			Exception exception = e.getProperty(e.EXCEPTION_CAUGHT, Exception.class);
			if (exception == null) {
				exception = e.getException();
			}
			
//			System.err.println(
//				    "\n===== EXCHANGE DEBUG =====" +
//				    "\nExchangeId      : " + e.getExchangeId() +
//				    "\nRouteId         : " + e.getFromRouteId() +
//				    "\nFromEndpoint    : " +
//				        (e.getFromEndpoint() != null
//				            ? e.getFromEndpoint().getEndpointUri()
//				            : null) +
//				    "\nPattern         : " + e.getPattern() +
//				    "\nCreated         : " + e.getCreated() +
//				    "\nFailed          : " + e.isFailed() +
//				    "\nException       : " + e.getException() +
//				    "\nExceptionCaught : " +
//				        e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class) +
//				    "\nHeaders         : " + e.getIn().getHeaders() +
//				    "\nProperties      : " + e.getProperties() +
//				    "\nBody            : " + e.getIn().getBody() +
//				    "\n=========================="
//				);
			
			 return new Document()
		                .append("routeId", routeId)
		                .append("exchangeId", exchangeId)
		                .append("sourceEndpoint", fromEndpoint)
		                .append("fileName", fileName)
		                .append("status", "Failed")
		                .append("startTime", startTime)
		                .append("endTime", LocalDateTime.now().toString())
		                .append("queueName", queueName)
		                .append("exceptionType",
		                        exception != null ? exception.getClass().getName() : null)
		                .append("exceptionMessage",
		                        exception != null ? exception.getMessage() : null);
		}

}
