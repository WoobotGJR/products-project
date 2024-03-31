
package com.woobot.springproject.controller;

import com.woobot.springproject.client.BadRequestException;
import com.woobot.springproject.client.ProductsRestClient;
import com.woobot.springproject.controller.payload.UpdateProductPayload;
import com.woobot.springproject.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products/{productId:\\d+}")
public class ProductController {
    private final ProductsRestClient productsRestClient;

    private final MessageSource messageSource;

    // it will automatically send model to html, so we can use it everywhere in thymeleaf
    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return this.productsRestClient.findProduct(productId).orElseThrow(() -> new NoSuchElementException("catalogue.errors.product_not_found"));
    }

    // we can define what product id is with regEx
    @GetMapping // with Principal principal we can get info about user
    public String getProduct(Principal principal) {
        // orElseThrow added to make returning product optional, cuz it might be null
//        model.addAttribute("product", this.productService.findProduct(productId).orElseThrow());
        return "catalogue/products/product";
    }

    @GetMapping("edit") // with @AuthenticationPrincipal UserDetails userDetails we can get info about user
    public String getProductEditPage(@AuthenticationPrincipal UserDetails userDetails) {
//        model.addAttribute("product", this.productService.findProduct(productId).orElseThrow()); // created upper in ModelAttribute once for all routes
        return "catalogue/products/edit";
    }

    // for correct work model should be after bindingResult in args (or model should be last)
    @PostMapping("edit")
    public String updateProduct(@ModelAttribute(value = "product", binding = false) Product product,
                                UpdateProductPayload payload,
                                Model model) {
        try {
            this.productsRestClient.updateProduct(product.id(), payload.title(), payload.details());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException exception) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", exception.getErrors());

            return "catalogue/products/edit";

        }
    }

    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("product") Product product) {
        this.productsRestClient.deleteProduct(product.id());
        return "redirect:/catalogue/products/list";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model,
                                               HttpServletResponse response, Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(exception.getMessage(), new Object[0], locale));
        return "errors/404";
    }
}
