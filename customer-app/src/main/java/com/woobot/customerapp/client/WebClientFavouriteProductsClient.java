package com.woobot.customerapp.client;

import com.woobot.customerapp.client.exception.ClientBadRequestException;
import com.woobot.customerapp.client.payload.NewFavouriteProductPayload;
import com.woobot.customerapp.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class WebClientFavouriteProductsClient implements FavouriteProductsClient {

    final private WebClient webClient;

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(int id) {
        return this.webClient
                .get()
                .uri("/feedback-api/favourite-products/by-product-id/productId", id)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(Integer productId) {
        return this.webClient
                .post()
                .uri("/feedback-api/favourite-products")
                .bodyValue(new NewFavouriteProductPayload(productId))
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class, // error handler
                        exception -> {
                            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
                            List<String> errors = new ArrayList<>();
                            assert problemDetail != null;
                            Object rawErrors = Objects.requireNonNull(problemDetail.getProperties()).get("errors");
                            if (rawErrors instanceof List<?> rawList) {
                                for (Object obj : rawList) {
                                    if (obj instanceof String) {
                                        errors.add((String) obj);
                                    }
                                }
                            }
                            return new ClientBadRequestException("Error in adding product in favourites", exception, errors);
                        });
    }

    @Override
    public Mono<Void> removeProductFromFavourites(Integer productId) {
        return this.webClient
                .delete()
                .uri("/feedback-api/favourite-products/by-product-id/productId", productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return this.webClient
                .get()
                .uri("/feedback-api/favourite-products")
                .retrieve()
                .bodyToFlux(FavouriteProduct.class);
    }
}
