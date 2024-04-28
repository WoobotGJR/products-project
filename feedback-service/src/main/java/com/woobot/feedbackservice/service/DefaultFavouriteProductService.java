package com.woobot.feedbackservice.service;

import com.woobot.feedbackservice.entity.FavouriteProduct;
import com.woobot.feedbackservice.repository.FavouriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DefaultFavouriteProductService implements FavouriteProductService {
    private final FavouriteProductRepository favoriteProductRepository;

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId, String userId) {
        return this.favoriteProductRepository.save( new FavouriteProduct(UUID.randomUUID(), productId, userId));
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId, String userId) {
        return this.favoriteProductRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(int productId, String userId) {
        return this.favoriteProductRepository.findByProductIdIdAndUserId(productId, userId);
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts(String userId) {
        return this.favoriteProductRepository.findAllByUserId(userId);
    }
}
