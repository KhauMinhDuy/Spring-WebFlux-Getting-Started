package com.khauminhduy.productapiannotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.khauminhduy.productapiannotation.controller.ProductController;
import com.khauminhduy.productapiannotation.model.Product;
import com.khauminhduy.productapiannotation.model.ProductEvent;
import com.khauminhduy.productapiannotation.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(ProductController.class)
public class JUnit5WebFluxAnnotationTest {
	
	@Autowired
	private WebTestClient client;
	
	private List<Product> expectedList;

	@MockBean
	private CommandLineRunner commandLineRunner;

	@MockBean
	private ProductRepository productRepository;

	@BeforeEach
	void beforeEach() {
		expectedList = Arrays.asList(new Product("1", "Big Latte", 2.99));
	}

	@Test
	void testGetAllProducts() {
		when(productRepository.findAll()).thenReturn(Flux.fromIterable(this.expectedList));

		client
			.get()
			.uri("/products")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(Product.class)
			.isEqualTo(expectedList);
	}

	@Test
	void testProductInvalidIdNotFound() {
		String id = "aaa";
		when(productRepository.findById(id)).thenReturn(Mono.empty());

		client
			.get()
			.uri("/products/{id}", id)
			.exchange()
			.expectStatus()
			.isNotFound();
	}

	@Test
	void testProductIdFound() {
		Product expectedProduct = this.expectedList.get(0);
		when(productRepository.findById(expectedProduct.getId())).thenReturn(Mono.just(expectedProduct));

		client
			.get()
			.uri("/products/{id}", expectedProduct.getId())
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(Product.class)
			.isEqualTo(expectedProduct);
	}

	@Test
	void testProductEvents() {
		ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

		FluxExchangeResult<ProductEvent> result =
			client.get().uri("/products/event")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ProductEvent.class);

		StepVerifier.create(result.getResponseBody())
			.expectNext(expectedEvent)
			.expectNextCount(2)
			.consumeNextWith(event -> assertEquals(Long.valueOf(3), event.getEventId()))
			.thenCancel()
			.verify();
	}



}
