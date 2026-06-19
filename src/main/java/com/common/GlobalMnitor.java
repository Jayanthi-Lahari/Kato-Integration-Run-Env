package com.common;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.bson.Document;

public class GlobalMnitor {

	/*
	 * ========================================= 🟢 UNIVERSAL AUDIT HANDLER (START /
	 * SUCCESS / FAILED) =========================================
	 */
	public Document auditHandler(Exchange e) {

		String eventType = e.getIn().getHeader("auditEvent", String.class);
		if (eventType == null) {
			eventType = "START";
		}

		String auditId = e.getIn().getHeader("auditId", String.class);
		;
		String startTime = e.getIn().getHeader("startTime", String.class);
		String payload = e.getIn().getBody(String.class);

		Document doc = new Document()
//        		.append("_id", auditId)
				.append("routeId", e.getFromRouteId()).append("exchangeId", e.getExchangeId())
				.append("startTime", startTime).append("status", mapStatus(eventType))
				.append("endTime", LocalDateTime.now().toString()).append("payload", payload);

		/*
		 * ============================== CONTEXT (FILE / QUEUE ONLY)
		 * ==============================
		 */
		addFileContext(e, doc);
		addQueueContext(e, doc);

		/*
		 * ============================== ENDPOINT TRACKING
		 * ==============================
		 */
		doc.append("sourceEndpoint", e.getFromEndpoint() != null ? e.getFromEndpoint().getEndpointUri() : null);

		String targetEndpoint = e.getIn().getHeader("targetEndpoint", String.class);

		doc.append("targetEndpoint", targetEndpoint);

		/*
		 * ============================== EXCEPTION (ONLY IF FAILED)
		 * ==============================
		 */
		if ("FAILED".equals(eventType)) {
			Exception ex = e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			if (ex == null)
				ex = e.getException();

			doc.append("exceptionMessage", ex != null ? ex.getMessage() : null);
		}

		return doc;
	}

	/*
	 * ========================================= 🔴 EXCEPTION HANDLER (ONLY FOR DB:
	 * Exceptions) =========================================
	 */
	public Document exceptionHandler(Exchange e) {

		Exception ex = e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		if (ex == null)
			ex = e.getException();

		String payload = e.getIn().getBody(String.class);

		Document doc = new Document()
//        		.append("auditId",
//        		        e.getIn().getHeader("auditId", String.class))
				.append("routeId", e.getFromRouteId()).append("exchangeId", e.getExchangeId())
				.append("startTime", e.getIn().getHeader("startTime", String.class)).append("status", "FAILED")
				.append("endTime", LocalDateTime.now().toString())
				.append("exceptionType", ex != null ? ex.getClass().getName() : null)
				.append("exceptionMessage", ex != null ? ex.getMessage() : null).append("payload", payload);

		addFileContext(e, doc);
		addQueueContext(e, doc);

		doc.append("sourceEndpoint", e.getFromEndpoint() != null ? e.getFromEndpoint().getEndpointUri() : null);

		String targetEndpoint = e.getIn().getHeader("targetEndpoint", String.class);

		doc.append("targetEndpoint", targetEndpoint);

		return doc;
	}

	/*
	 * ========================================= 🔧 HELPERS (REMOVED DUPLICATION)
	 * =========================================
	 */

	private void addFileContext(Exchange e, Document doc) {
		String fileName = e.getIn().getHeader("CamelFileName", String.class);
		if (fileName != null) {
			doc.append("fileName", fileName);
		}
	}

	private void addQueueContext(Exchange e, Document doc) {
		String messageId = e.getIn().getHeader("JMSMessageID", String.class);
		if (messageId == null) {
			messageId = e.getIn().getHeader("CamelMessageId", String.class);
		}

		if (messageId != null) {
			doc.append("messageId", messageId);
		}
	}

	private String mapStatus(String eventType) {
		switch (eventType) {
		case "SUCCESS":
			return "SUCCESS";
		case "FAILED":
			return "FAILED";
		default:
			return eventType;
		}
	}
}