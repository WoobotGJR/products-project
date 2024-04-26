package com.woobot.customerapp.client;

import com.woobot.customerapp.client.exception.ClientBadRequestException;
import com.woobot.customerapp.client.payload.NewProductReviewPayload;
import com.woobot.customerapp.entity.ProductReview;
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
public class WebClientProductReviewsClient implements ProductReviewsClient {

    private final WebClient webClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return webClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review) {
        return webClient
                .post()
                .uri("/feedback-api/product-reviews")
                .bodyValue(new NewProductReviewPayload(productId, rating, review))
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
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
                            return new ClientBadRequestException("Error in product review creating", exception, errors);
                        });
    }
}
