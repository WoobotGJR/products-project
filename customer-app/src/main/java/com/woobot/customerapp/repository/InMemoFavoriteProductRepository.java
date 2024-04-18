package com.woobot.customerapp.repository;

import com.woobot.customerapp.entity.FavouriteProduct;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class InMemoFavoriteProductRepository implements FavoriteProductRepository {
    private final List<FavouriteProduct> favoriteProducts = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Mono<Void> deleteByProductId(int productId) {
        favoriteProducts.removeIf(favoriteProduct -> favoriteProduct.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<FavouriteProduct> save(FavouriteProduct favoriteProduct) {
        this.favoriteProducts.add(favoriteProduct);
        return Mono.just(favoriteProduct);
    }

    @Override
    public Mono<FavouriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(this.favoriteProducts)
                .filter(favouriteProduct -> favouriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavouriteProduct> findAll() {
        return Flux.fromIterable(favoriteProducts);
    }
}
