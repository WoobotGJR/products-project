package com.woobot.springproject.controller;

import com.woobot.springproject.client.BadRequestException;
import com.woobot.springproject.client.ProductsRestClient;
import com.woobot.springproject.controller.payload.NewProductPayload;
import com.woobot.springproject.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {
//    @Mock
    ProductsRestClient productsRestClient;
//    @InjectMocks
    ProductsController productsController;

    @BeforeEach
    public void productsControllerGetter() {
        productsRestClient = Mockito.mock(ProductsRestClient.class);
        productsController = new ProductsController(productsRestClient);
    }

    @Test
    void getProductsList_ReturnsProductsListPage() {
        // given
        var model = new ConcurrentModel();
        var filter = "товар";

        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Товар №%d".formatted(i),
                        "Описание товара №%d".formatted(i)))
                .toList();

        doReturn(products).when(this.productsRestClient).findAllProducts(filter);

        // when
        var result = this.productsController.getProductsList(model, filter);

        // then
        assertEquals("catalogue/products/list", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(products, model.getAttribute("products"));
    }

    @Test
    void getNewProductPage_ReturnsNewProductPage () {
        // given

        // when
        var result = this.productsController.getNewProductPage();

        // then
        assertEquals("catalogue/products/new_product", result);
    }

    @Test
    @DisplayName("createProduct will create product and redirects to products page")
    // naming is <method name>_<condition>_<expected result>
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var payload = new NewProductPayload("Product", "Product description");
        var model = new ConcurrentModel();

        doReturn(new Product(1, "Product", "Product description"))
                .when(this.productsRestClient)
                .createProduct(notNull(), any());

        // when
        var result = this.productsController.createProduct(payload, model);

        // then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(this.productsRestClient).createProduct("Product", "Product description");
        verifyNoMoreInteractions(this.productsRestClient);
    }
    @Test
    @DisplayName("createProduct will return the error page, if request is invalid")
    // naming is <method name>_<condition>_<expected result>
    void createProduct_RequestIsInvalid_ReturnsProductFormWithErrors() {
        // given
        var payload = new NewProductPayload("     ", null);
        var model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("error 1", "error 2")))
                .when(this.productsRestClient)
                .createProduct("     ", null);

        // when
        var result = this.productsController.createProduct(payload, model);

        // then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("error 1", "error 2"), model.getAttribute("errors"));

        verify(this.productsRestClient).createProduct("     ", null);
        verifyNoMoreInteractions(this.productsRestClient);
    }

}