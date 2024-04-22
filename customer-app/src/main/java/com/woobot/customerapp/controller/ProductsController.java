package com.woobot.customerapp.controller;

import com.woobot.customerapp.client.FavouriteProductsClient;
import com.woobot.customerapp.client.ProductsRestClient;
import com.woobot.customerapp.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    private final FavouriteProductsClient favouriteProductsClient;

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange serverWebExchange) {
        Mono<CsrfToken> attribute = serverWebExchange.getAttribute(CsrfToken.class.getName());
        return attribute.doOnSuccess(csrfToken -> serverWebExchange.getAttributes()
                .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, csrfToken));
    }

    @GetMapping("list")
    public Mono<String> getProductsListPage(Model model,
                                            @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return this.productsRestClient.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

@GetMapping("favourites")
    public Mono<String> getFavouriteProductsListPage(Model model,
                                            @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return this.favouriteProductsClient.findFavouriteProducts()
                .map(FavouriteProduct::productId)
                .collectList()
                .flatMap(favouriteProducts -> this.productsRestClient.findAllProducts(filter)
                        .filter(product -> favouriteProducts.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)))
                .thenReturn("customer/products/favourites");
    }


}
