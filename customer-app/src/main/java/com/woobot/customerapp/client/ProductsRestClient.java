package com.woobot.customerapp.client;

import com.woobot.customerapp.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsRestClient {
    Flux<Product> findAllProducts(String filter);

    Mono<Product> findProduct(int id);
}
