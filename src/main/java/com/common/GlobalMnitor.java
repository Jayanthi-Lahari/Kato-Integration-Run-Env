package com.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.bson.Document;

public class GlobalMnitor {

    /*
     * =========================================================
     * UNIVERSAL AUDIT HANDLER
     * =========================================================
     */
    public Document auditHandler(Exchange e) {

        String routeId = e.getFromRouteId();
        String exchangeId = e.getExchangeId();

        String eventType =
                e.getIn().getHeader("auditEvent", String.class);

        if (eventType == null) {
            eventType = "START";
        }

        String startTime =
                e.getIn().getHeader("startTime", String.class);

        String sourceEndpoint =
                e.getFromEndpoint() != null
                        ? e.getFromEndpoint().getEndpointUri()
                        : "unknown";
//      String targetEndpoint = e.getProperty(Exchange.TO_ENDPOINT, String.class);

//        String targetEndpoint =
//                e.getIn().getHeader("targetEndpoint",
//                        String.class);
        Object targetEndpoint = e.getProperty("targetEndpoints");

        if (targetEndpoint == null) {
            targetEndpoint = e.getProperty(Exchange.TO_ENDPOINT);
        }

        if (targetEndpoint == null) {
            targetEndpoint = e.getIn().getHeader("targetEndpoint");
        }
        

        String payload =
                e.getIn().getBody(String.class);

        if (payload != null && payload.length() > 5000) {
            payload = payload.substring(0, 5000);
        }

        long processingTime =
                System.currentTimeMillis() - e.getCreated();

        Document doc = new Document()

                .append("routeId", routeId)
                .append("exchangeId", exchangeId)

//                .append("eventType", eventType)
                .append("status", mapStatus(eventType))

                .append("startTime", startTime)
                .append("endTime", LocalDateTime.now().toString())
                .append("processingTimeMs", processingTime)

                .append("sourceEndpoint", sourceEndpoint)
                .append("targetEndpoint", targetEndpoint)

                .append("payload", payload);

        addFileContext(e, doc);
        addQueueContext(e, doc);
        addCorrelationContext(e, doc);

        if ("FAILED".equalsIgnoreCase(eventType)) {

            Exception ex =
                    e.getProperty(
                            Exchange.EXCEPTION_CAUGHT,
                            Exception.class);

            if (ex == null) {
                ex = e.getException();
            }

            doc.append("exceptionType",
                    ex != null
                            ? ex.getClass().getName()
                            : null);

            doc.append("exceptionMessage",
                    ex != null
                            ? ex.getMessage()
                            : null);
        }

        return doc;
    }

    /*
     * =========================================================
     * EXCEPTION AUDIT HANDLER
     * =========================================================
     */
    public Document exceptionHandler(Exchange e) {

        Exception exception =
                e.getProperty(
                        Exchange.EXCEPTION_CAUGHT,
                        Exception.class);

        if (exception == null) {
            exception = e.getException();
        }

        String routeId = e.getFromRouteId();

        String exchangeId = e.getExchangeId();

        String sourceEndpoint =
                e.getFromEndpoint() != null
                        ? e.getFromEndpoint().getEndpointUri()
                        : "unknown";

        String failedEndpoint =
                e.getProperty(
                        ExchangePropertyKey.FAILURE_ENDPOINT,
                        String.class);

        String failedRouteId =
                e.getProperty(
                        ExchangePropertyKey.FAILURE_ROUTE_ID,
                        String.class);

        String startTime =
                e.getIn().getHeader(
                        "startTime",
                        String.class);

        String payload =
                e.getIn().getBody(String.class);

        if (payload != null && payload.length() > 5000) {
            payload = payload.substring(0, 5000);
        }

        long processingTime =
                System.currentTimeMillis() - e.getCreated();

        Document doc = new Document()

                .append("routeId", routeId)
                .append("exchangeId", exchangeId)

//                .append("eventType", "FAILED")
                .append("status", "FAILED")

                .append("startTime", startTime)
                .append("endTime", LocalDateTime.now().toString())
                .append("processingTimeMs", processingTime)

                .append("sourceEndpoint", sourceEndpoint)
                .append("failedEndpoint", failedEndpoint)
                .append("failedRouteId", failedRouteId)

                .append("exceptionType",
                        exception != null
                                ? exception.getClass().getName()
                                : "Unknown")

                .append("exceptionMessage",
                        exception != null
                                ? exception.getMessage()
                                : "Unknown Error")

                .append("payload", payload);

        addFileContext(e, doc);
        addQueueContext(e, doc);
        addCorrelationContext(e, doc);

        return doc;
    }
    
    public void captureEndpoint(Exchange exchange) {

        // Endpoint that Camel is about to invoke
        String endpoint = exchange.getProperty(Exchange.TO_ENDPOINT, String.class);
        
        if (endpoint == null) {
            return;
        }
        
        if (endpoint.startsWith("direct://Aggregate-Audit")
                || endpoint.startsWith("mongodb:")
                || endpoint.startsWith("bean:")
                || endpoint.startsWith("log:")) {
            return;
        }
        
        // Get existing endpoint list
        List<String> targetEndpoints =
                exchange.getProperty("targetEndpoints", List.class);

        // Create list if it doesn't exist
        if (targetEndpoints == null) {
            targetEndpoints = new ArrayList<>();
            exchange.setProperty("targetEndpoints", targetEndpoints);
        }

        // Avoid duplicates
        if (!targetEndpoints.contains(endpoint)) {
            targetEndpoints.add(endpoint);
        }
    }

    /*
     * =========================================================
     * FILE CONTEXT
     * =========================================================
     */
    private void addFileContext(
            Exchange e,
            Document doc) {

        String fileName =
                e.getIn().getHeader(
                        "CamelFileName",
                        String.class);

        if (fileName != null) {
            doc.append("fileName", fileName);
        }
    }

    /*
     * =========================================================
     * QUEUE CONTEXT
     * =========================================================
     */
    private void addQueueContext(
            Exchange e,
            Document doc) {

        String messageId =
                e.getIn().getHeader(
                        "JMSMessageID",
                        String.class);

        if (messageId == null) {
            messageId =
                    e.getIn().getHeader(
                            "CamelMessageId",
                            String.class);
        }

        if (messageId != null) {
            doc.append("messageId", messageId);
        }
    }

    /*
     * =========================================================
     * CORRELATION CONTEXT
     * =========================================================
     */
    private void addCorrelationContext(
            Exchange e,
            Document doc) {

        String correlationId =
                e.getIn().getHeader(
                        "JMSCorrelationID",
                        String.class);

        if (correlationId == null) {
            correlationId = e.getExchangeId();
        }

        doc.append("correlationId",
                correlationId);
    }

    /*
     * =========================================================
     * STATUS MAPPING
     * =========================================================
     */
    private String mapStatus(String eventType) {

        switch (eventType) {

            case "SUCCESS":
                return "SUCCESS";

            case "FAILED":
                return "FAILED";

            case "START":
                return "IN_PROGRESS";

            default:
                return eventType;
        }
    }
}




//package com.common;
//
//import java.time.LocalDateTime;
//
//import org.apache.camel.Exchange;
//import org.apache.camel.ExchangePropertyKey;
//import org.bson.Document;
//
//public class GlobalMnitor {
//
//	/*
//	 * ========================================= 🟢 UNIVERSAL AUDIT HANDLER (START /
//	 * SUCCESS / FAILED) =========================================
//	 */
//	public Document auditHandler(Exchange e) {
//
//		String routeId = e.getFromRouteId();
//		String exchangeId = e.getExchangeId();
//		String sourceEndPoint = e.getFromEndpoint() != null ? e.getFromEndpoint().getEndpointUri() : "unknown";
//		String startTime = e.getIn().getHeader("startTime", String.class);
//
//		
//		String eventType = e.getIn().getHeader("auditEvent", String.class);
//		if (eventType == null) {
//			eventType = "START";
//		}
//
////		String auditId = e.getIn().getHeader("auditId", String.class);
//		String payload = e.getIn().getBody(String.class);
//
//		Document doc = new Document()
////        		.append("_id", auditId)
//				.append("routeId", e.getFromRouteId()).append("exchangeId", e.getExchangeId())
//				.append("startTime", startTime).append("status", mapStatus(eventType))
//				.append("endTime", LocalDateTime.now().toString()).append("payload", payload);
//
//		/*
//		 * ============================== CONTEXT (FILE / QUEUE ONLY)
//		 * ==============================
//		 */
//		addFileContext(e, doc);
//		addQueueContext(e, doc);
//
//		/*
//		 * ============================== ENDPOINT TRACKING
//		 * ==============================
//		 */
//		doc.append("sourceEndpoint", sourceEndPoint);
//		System.err.println("Target --------> "+e.getProperty("CamelToEndpoint"));
//		System.err.println("===== EXCHANGE PROPERTIES =====");
//
//		e.getProperties().forEach((k, v) ->
//		    System.err.println(k + " = " + v)
//		);
//
//		System.err.println("===============================");
//		String targetEndpoint = e.getIn().getHeader("targetEndpoint", String.class);
//
//		doc.append("targetEndpoint", e.getProperty("CamelToEndpoint"));
//
//		/*
//		 * ============================== EXCEPTION (ONLY IF FAILED)
//		 * ==============================
//		 */
//		if ("FAILED".equals(eventType)) {
//			Exception ex = e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
//			if (ex == null)
//				ex = e.getException();
//
//			doc.append("exceptionMessage", ex != null ? ex.getMessage() : null);
//		}
//
//		return doc;
//	}
//
//	/*
//	 * ========================================= 🔴 EXCEPTION HANDLER (ONLY FOR DB:
//	 * Exceptions) =========================================
//	 */
//	public Document exceptionHandler(Exchange e) {
//
//		Exception exception = e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
//
//		if (exception == null) {
//			exception = e.getException();
//		}
//
//		String errorMessage = exception != null ? exception.getMessage() : "Unknown Error";
//		String errorType = exception != null ? exception.getClass().getName() : "Unknown Name";
//
//		String failedEndpoint = e.getProperty(ExchangePropertyKey.FAILURE_ENDPOINT, String.class);
//		String failedrouteId = e.getProperty(ExchangePropertyKey.FAILURE_ROUTE_ID, String.class);
//
//System.err.println("Target --------> "+e.getProperty("CamelToEndpoint"));
//
//System.err.println("===== EXCHANGE PROPERTIES =====");
//
//e.getProperties().forEach((k, v) ->
//    System.err.println(k + " = " + v)
//);
//
//System.err.println("===============================");
//		String payload = e.getIn().getBody(String.class);
//
//		Document doc = new Document()
////        		.append("auditId",
////        		        e.getIn().getHeader("auditId", String.class))
//				.append("routeId", e.getFromRouteId()).append("exchangeId", e.getExchangeId())
//				.append("startTime", e.getIn().getHeader("startTime", String.class)).append("status", "FAILED")
//				.append("endTime", LocalDateTime.now().toString())
//				.append("exceptionType", errorType )
//				.append("exceptionMessage", errorMessage)
//				.append("failedEndpoint", failedEndpoint )
//				.append("failedrouteId", failedrouteId)
//				.append("payload", payload);
//
//		addFileContext(e, doc);
//		addQueueContext(e, doc);
//
//		doc.append("sourceEndpoint", e.getFromEndpoint() != null ? e.getFromEndpoint().getEndpointUri() : null);
//
//		String targetEndpoint = e.getIn().getHeader("targetEndpoint", String.class);
//
//		doc.append("targetEndpoint", targetEndpoint);
//
//		return doc;
//	}
//
//	/*
//	 * ========================================= 🔧 HELPERS (REMOVED DUPLICATION)
//	 * =========================================
//	 */
//
//	private void addFileContext(Exchange e, Document doc) {
//		String fileName = e.getIn().getHeader("CamelFileName", String.class);
//		if (fileName != null) {
//			doc.append("fileName", fileName);
//		}
//	}
//
//	private void addQueueContext(Exchange e, Document doc) {
//		String messageId = e.getIn().getHeader("JMSMessageID", String.class);
//		if (messageId == null) {
//			messageId = e.getIn().getHeader("CamelMessageId", String.class);
//		}
//
//		if (messageId != null) {
//			doc.append("messageId", messageId);
//		}
//	}
//
//	private String mapStatus(String eventType) {
//		switch (eventType) {
//		case "SUCCESS":
//			return "SUCCESS";
//		case "FAILED":
//			return "FAILED";
//		case "START":
//			return "IN_PROGRESS";
//		default:
//			return eventType;
//		}
//	}
//}