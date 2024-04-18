package com.woobot.customerapp.service;

import com.woobot.customerapp.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsService {
    Mono<ProductReview> createProductReview(int productId, int rating, String review);

    Flux<ProductReview> findProductReviewsByProduct(int productId);
}
