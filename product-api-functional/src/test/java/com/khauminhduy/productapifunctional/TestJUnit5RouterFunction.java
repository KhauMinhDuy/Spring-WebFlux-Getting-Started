package com.khauminhduy.productapifunctional;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.khauminhduy.productapifunctional.model.Product;
import com.khauminhduy.productapifunctional.repository.ProductRepository;

@SpringBootTest
public class TestJUnit5RouterFunction {
	
	private WebTestClient client;

	private List<Product> expectedList;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private RouterFunction<ServerResponse> routes;

	@BeforeEach
	void beforeEach() {
		client = WebTestClient
			.bindToRouterFunction(routes)
			.configureClient()
			.baseUrl("/products")
			.build();
		
		expectedList = productRepository.findAll().collectList().block();
	}

	@Test
	void testGetAllProducts() {
		client
			.get()
			.uri("/")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Product.class)
			.isEqualTo(expectedList);
	}

	@Test
	void testProductInvalidIdNotFound() {
		client
			.get()
			.uri("/aaa")
			.exchange()
			.expectStatus()
			.isNotFound();
	}

	@Test
	void testProductIdFound() {
		Product expectedProduct = expectedList.get(0);
		client
			.get()
			.uri("/{id}", expectedProduct.getId())
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(Product.class)
			.isEqualTo(expectedProduct);
	}

	// @Test
	// void testProductEvents() {
	// 	ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

	// 	FluxExchangeResult<ProductEvent> result = client
	// 			.get()
	// 			.uri("/event")
	// 			.accept(MediaType.TEXT_EVENT_STREAM)
	// 			.exchange()
	// 			.expectStatus()
	// 			.isOk()
	// 			.returnResult(ProductEvent.class);

	// 	StepVerifier.create(result.getResponseBody())
	// 		.expectNext(expectedEvent)
	// 		.expectNextCount(1)
	// 		.consumeNextWith(event -> assertEquals(Long.valueOf(1), event.getEventId()))
	// 		.thenCancel()
	// 		.verify();
  // }

}
