package com.woobot.feedbackservice.controller;

import com.woobot.feedbackservice.entity.ProductReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductReviewsRestControllerIT {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("21d2aeaf-06f1-4deb-bf43-685da9e58db1"), 1, 5, "Review 1", "userid-1"),
                new ProductReview(UUID.fromString("21d2aeaf-06f1-4deb-bf43-685da9e58db2"), 1, 4, "Review 2", "userid-2"),
                new ProductReview(UUID.fromString("21d2aeaf-06f1-4deb-bf43-685da9e58db3"), 1, 3, "Review 3", "userid-3")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class)
                .all()
                .block();
    }

    @Test
    void findProductReviewsByProductId_ReturnsReviews() {
        // when


        // then
        this.webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {
                                "id": "21d2aeaf-06f1-4deb-bf43-685da9e58db1"),
                                "productId": 1,
                                "rating": 5,
                                "review": "Review 1",
                                "userId": "userid-1"
                            },
                            {
                                "id": "21d2aeaf-06f1-4deb-bf43-685da9e58db2"),
                                "productId": 1,
                                "rating": 4,
                                "review": "Review 2",
                                "userId": "userid-2"
                            },
                            {
                                "id": "21d2aeaf-06f1-4deb-bf43-685da9e58db3"),
                                "productId": 1,
                                "rating": 3,
                                "review": "Review 3",
                                "userId": "userid-3"
                            }
                        ]
                        """)
                .returnResult();
    }

    @Test
    void findProductReviewsByProductId_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // when
        // then
        this.webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // given

        // when
        this.webTestClient
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "review": "На пяторочку!"
                        }""")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview() {
        // given
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                            {
                                "productId": 1,
                                "rating": 1,
                                "review": "Review 4"
                            }
                        """)
            // then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                            {
                                "productId": 1,
                                "rating": 1,
                                "review": "Review 4",
                                "userId": "user-tester"
                            }
                        """)
                .jsonPath("$.id").exists()
                .consumeWith(document("feedback/product_reviews/create_product_review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("Product ID"),
                                fieldWithPath("rating").type("string").description("Product rating"),
                                fieldWithPath("review").type("string").description("Product review")
                        ),
                        responseFields(
                                fieldWithPath("productId").type("int").description("Product ID"),
                                fieldWithPath("rating").type("string").description("Product rating"),
                                fieldWithPath("id").type("uuid").description("Review ID"),
                                fieldWithPath("userId").type("string").description("User ID"),
                                fieldWithPath("review").type("string").description("Product review")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Link to created review")
                        )
                        ));
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest() {
        // given
        // when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                            {
                                "productId": null,
                                "rating": -100000,
                                "review": "Review ???"
                            }
                        """)
            // then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                            {
                                "errors": [
                                    "Product is null",
                                    "Rating is less than 1"
                                ]
                            }
                        """);
    }

}