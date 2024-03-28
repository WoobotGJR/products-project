package com.woobot.springproject.config;

import com.woobot.springproject.client.RestClientProductsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration // methods of this class annotated with bean will be source of components to register them in app context
public class ClientBeans {
    @Bean // All beans should start with entity, that we return (RestClient here, for example) GraalVM
    public RestClientProductsRestClient productsRestClient(
            @Value("${service.catalogue.uri:http://localhost:8081}") String catalogueBaseUri) {
        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
