//package com.config;
//
//import org.apache.camel.component.http.HttpComponent;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
//import org.apache.hc.core5.ssl.SSLContexts;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.net.ssl.SSLContext;
//
//@Configuration
//public class HttpClientConfig {
//
//    @Bean
//    public HttpComponent httpComponent() throws Exception {
//
//        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(null, (chain, authType) -> true)
//                .build();
//
//        CloseableHttpClient client = HttpClients.custom()
//                .setSSLContext(sslContext)
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .build();
//
//        HttpComponent http = new HttpComponent();
//        http.setHttpClient(client);
//
//        return http;
//    }
//}