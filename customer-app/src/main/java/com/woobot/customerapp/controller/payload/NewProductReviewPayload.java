package com.woobot.customerapp.controller.payload;

public record NewProductReviewPayload (
        Integer rating,
        String review) {
}
