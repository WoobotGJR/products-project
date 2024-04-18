package com.woobot.customerapp.config;

import com.woobot.customerapp.client.WebClientProductsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    public WebClientProductsRestClient webClientProductsRestClient(
            @Value("services.catalogue.uri:http://localhost:8081") String catalogueBaseUri
            ) {
        return new WebClientProductsRestClient(WebClient
                .builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
