package com.woobot.feedbackservice.controller;

import com.woobot.feedbackservice.entity.FavouriteProduct;
import com.woobot.feedbackservice.controller.payload.NewFavouriteProductPayload;
import com.woobot.feedbackservice.service.FavouriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/favourite-products")
@RequiredArgsConstructor
public class FavouriteProductsRestController {
    private final FavouriteProductService favouriteProductService;

    @GetMapping
    public Flux<FavouriteProduct> findFavouriteProducts(Mono<JwtAuthenticationToken> authenticationTokenMono) {
        return authenticationTokenMono.flatMapMany(token ->
                this.favouriteProductService.findFavouriteProducts(token.getToken().getSubject())) ;
    }

    @GetMapping("by-productId/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProductByProductId(Mono<JwtAuthenticationToken> authenticationTokenMono,
                                                                  @PathVariable("productId") int productId) {
        return authenticationTokenMono.flatMap(token ->
                this.favouriteProductService.findFavouriteProductByProduct(productId, token.getToken().getSubject()));
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> createFavouriteProduct(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return Mono.zip(authenticationTokenMono, payloadMono)
                .flatMap(tuple2 -> this.favouriteProductService.addProductToFavourites(tuple2.getT2().productId(),
                        tuple2.getT1().getToken().getSubject()))
                .map(favouriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favourite-products/id")
                                .build(favouriteProduct.getId()))
                        .body(favouriteProduct));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavourites(Mono<JwtAuthenticationToken> authenticationTokenMono,
                                                                  @PathVariable("productId") int productId) {
        return authenticationTokenMono.flatMap(token ->
                this.favouriteProductService.removeProductFromFavourites(productId, token.getToken().getSubject())
                .then(Mono.just(ResponseEntity.noContent().build())));
    }
}
