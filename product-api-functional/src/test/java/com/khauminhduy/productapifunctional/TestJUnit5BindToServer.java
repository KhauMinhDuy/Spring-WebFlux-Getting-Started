package com.khauminhduy.productapifunctional;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.khauminhduy.productapifunctional.model.Product;
import com.khauminhduy.productapifunctional.repository.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestJUnit5BindToServer {

	private WebTestClient client;

	private List<Product> expectedList;

	@Autowired
	private ProductRepository productRepository;

	@LocalServerPort
	private int port;

	@BeforeEach
	public void beforeEach() {
		this.client = WebTestClient
				.bindToServer()
				.baseUrl("http://localhost:" + port + "/products")
				.build();

		this.expectedList = productRepository.findAll().collectList().block();
	}

	@Test
	public void testGetAllProducts() {
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
	public void testProductInvalidIdNotFound() {
		client
			.get()
			.uri("/aaa")
			.exchange()
			.expectStatus()
			.isNotFound();
	}

	@Test
	public void testProductIdFound() {
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
	// public void testProductEvents() {
	// 	FluxExchangeResult<ProductEvent> result = client.get().uri("/event")
	// 		.accept(MediaType.TEXT_EVENT_STREAM)
	// 		.exchange()
	// 		.expectStatus().isOk()
	// 		.returnResult(ProductEvent.class);

	// 	ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

	// 	StepVerifier.create(result.getResponseBody())
	// 		.expectNext(expectedEvent)
	// 		.expectNextCount(2)
	// 		.consumeNextWith(event -> assertEquals(Long.valueOf(3), event.getEventId()))
	// 		.thenCancel()
	// 		.verify();
	// }

}
