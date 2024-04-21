package com.woobot.feedbackservice.repository;

import com.woobot.feedbackservice.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductRepository {
    Mono<Void> deleteByProductId(int productId);

    Mono<FavouriteProduct> save(FavouriteProduct favoriteProduct);

    Mono<FavouriteProduct> findByProductId(int productId);

    Flux<FavouriteProduct> findAll();
}
