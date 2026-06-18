//package com.bean;
//
//import java.io.File;
//import java.nio.file.Path;
//
//import org.apache.camel.Exchange;
//import org.apache.camel.language.simple.SimpleLanguage;
//public class TrgFileValidatorBean {
//	
//	public void validator(Exchange e) {
//		String filename = e.getIn().getHeader("CamelFileNameOnly").toString();
//		String folder = e.getIn().getHeader("CamelFileParent").toString();
////		String name = FilenameUtils.removeExtension(fileName);
//		String baseName =e.getContext()
//		        .resolveLanguage("simple")
//		        .createExpression("${file:name.noext}")
//		        .evaluate(e, String.class);
//		Path trgFile = folder+baseName+".trg";
//		System.err.println(e.getFromEndpoint() != null ? e.getFromEndpoint().getEndpointUri() : null);
//		if(Files.exists(trgFile)) {
//			
//		}
//	}
//
//}
