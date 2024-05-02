package com.woobot.customerapp.config;

import com.woobot.customerapp.client.WebClientFavouriteProductsClient;
import com.woobot.customerapp.client.WebClientProductReviewsClient;
import com.woobot.customerapp.client.WebClientProductsRestClient;
import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.ReactiveRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    @Scope("prototype")
    public WebClient.Builder productsServicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
            ObservationRegistry observationRegistry
    ) {
        var filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                authorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .observationRegistry(observationRegistry)
                .observationConvention(new DefaultClientRequestObservationConvention())
                .filter(filter);
    }

    @Bean
    public WebClientProductsRestClient webClientProductsRestClient(
            WebClient.Builder productsServicesWebClientBuilder,
            @Value("services.catalogue.uri:http://localhost:8081") String catalogueBaseUri
            ) {
        return new WebClientProductsRestClient(productsServicesWebClientBuilder
                .baseUrl(catalogueBaseUri)
                .build());
    }

    @Bean
    public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
            WebClient.Builder productsServicesWebClientBuilder,
            @Value("services.feedback.uri:http://localhost:8084") String feedbackBaseUri
            ) {
        return new WebClientFavouriteProductsClient(productsServicesWebClientBuilder
                .baseUrl(feedbackBaseUri)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            WebClient.Builder productsServicesWebClientBuilder,
            @Value("services.feedback.uri:http://localhost:8084") String feedbackBaseUri
            ) {
        return new WebClientProductReviewsClient(productsServicesWebClientBuilder
                .baseUrl(feedbackBaseUri)
                .build());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ClientProperties clientProperties,
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                authorizedClientService));
        filter.setDefaultClientRegistrationId("metrics");

        return new ReactiveRegistrationClient(WebClient.builder()
                .filter(filter)
                .build(), clientProperties.getReadTimeout());
    }
}
