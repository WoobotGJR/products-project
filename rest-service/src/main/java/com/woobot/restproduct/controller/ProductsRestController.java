package com.woobot.restproduct.controller;

import com.woobot.restproduct.controller.payload.NewProductPayload;
import com.woobot.restproduct.entity.Product;
import com.woobot.restproduct.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

// RestController is analogue of Controller annotation, but methods ot its class automatically becomes @ResponseBody
// In MVC contoller we return view as String for thymeleaf or JSP. Also they are different in exception handling
@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {

    private final ProductService productService;

    @GetMapping
    public Iterable<Product> findProducts(@RequestParam(name = "filter", required = false) String filter) {
        // Here we use product, but it will be more correct to use different classes for response, db and for logic
        // because for these purposes we might need different objects with different props
        return this.productService.findAllProducts(filter);
    }

    @Operation(
            security = @SecurityRequirement(name = "keycloak"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "title", value = String.class),
                                            @StringToClassMapItem(key = "details", value = String.class)
                                    }
                            )
                    )
            ),
            responses = {
            @ApiResponse(responseCode = "201",
                    headers = @Header(name = "Content-Type", description = "Data type"),
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(key = "id", value = Integer.class),
                                            @StringToClassMapItem(key = "title", value = String.class),
                                            @StringToClassMapItem(key = "details", value = String.class)
                                    }
                            ))
                    })
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE) // by default
    public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductPayload payload,
                                           BindingResult bindingResult,
                                           UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            // BindException extends BindingResult
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Product product = this.productService.createProduct(payload.title(), payload.details());
            // created method requires uri to created object
            // we could use pathSegment instead of replacePath, because UriComponentsBuilder has uri of controller,
            // so it will look like .patSegment("{productId}"). All these methods are needed to create resource uri
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/catalogue-api/products/{productId}")
                            .build(Map.of("productId", product.getId())))
                    .body(product);
        }
    }
}
