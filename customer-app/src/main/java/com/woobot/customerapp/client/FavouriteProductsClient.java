package com.woobot.customerapp.client;

import com.woobot.customerapp.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsClient {
    Mono<FavouriteProduct> findFavouriteProductByProductId(int id);

    Mono<FavouriteProduct> addProductToFavourites(Integer productId);

    Mono<Void> removeProductFromFavourites(Integer productId);

    Flux<FavouriteProduct> findFavouriteProducts();
}
