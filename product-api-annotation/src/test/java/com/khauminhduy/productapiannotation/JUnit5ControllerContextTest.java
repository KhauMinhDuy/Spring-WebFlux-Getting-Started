package com.khauminhduy.productapiannotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.khauminhduy.productapiannotation.model.Product;
import com.khauminhduy.productapiannotation.model.ProductEvent;
import com.khauminhduy.productapiannotation.repository.ProductRepository;

import reactor.test.StepVerifier;

@SpringBootTest
public class JUnit5ControllerContextTest {
	private WebTestClient client;

	private List<Product> expectedList;

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ApplicationContext context;

	@BeforeEach
	void beforeEach() {
		client = WebTestClient
			.bindToApplicationContext(context)
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

	@Test
	void testProductEvent() {
		ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");
		FluxExchangeResult<ProductEvent> result = client
			.get()
			.uri("/event")
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.expectStatus()
			.isOk()
			.returnResult(ProductEvent.class);
		
		StepVerifier.create(result.getResponseBody())
			.expectNext(expectedEvent)
			.expectNextCount(2)
			.consumeNextWith(event -> assertEquals(Long.valueOf(3), event.getEventId()))
			.thenCancel()
			.verify();
	}
	
}
