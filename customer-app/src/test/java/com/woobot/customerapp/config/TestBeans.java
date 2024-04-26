package com.woobot.customerapp.config;

import com.woobot.customerapp.client.WebClientFavouriteProductsClient;
import com.woobot.customerapp.client.WebClientProductReviewsClient;
import com.woobot.customerapp.client.WebClientProductsRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

public class TestBeans {

    @Bean
    ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return mock();
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return mock();
    }

    @Bean
    @Primary
    public WebClientProductsRestClient mockWebClientProductsRestClient() {
        return new WebClientProductsRestClient(WebClient.builder()
                .baseUrl("http//localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientFavouriteProductsClient mockWebClientFavouriteProductsClient() {
        return new WebClientFavouriteProductsClient(WebClient.builder()
                .baseUrl("http//localhost:54321")
                .build());
    }

    @Bean
    @Primary
    public WebClientProductReviewsClient mockWebClientProductReviewsClient() {
        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl("http//localhost:54321")
                .build());
    }
}
