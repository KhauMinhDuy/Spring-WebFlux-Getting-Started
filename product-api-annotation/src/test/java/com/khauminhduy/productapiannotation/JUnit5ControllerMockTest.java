package com.khauminhduy.productapiannotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.khauminhduy.productapiannotation.controller.ProductController;
import com.khauminhduy.productapiannotation.model.Product;
import com.khauminhduy.productapiannotation.model.ProductEvent;
import com.khauminhduy.productapiannotation.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class JUnit5ControllerMockTest {
	
	private WebTestClient client;

	private List<Product> expectedList;

	@MockBean
	private ProductRepository productRepository;
	
	@BeforeEach
	void beforeEach() {
		client = WebTestClient
			.bindToController(new ProductController(productRepository))
			.configureClient()
			.baseUrl("/products")
			.build();
		expectedList = Arrays.asList(new Product("1", "Big Latte", 2.99));
	}

	@Test
	void testGetAllProducts() {
		when(productRepository.findAll()).thenReturn(Flux.fromIterable(this.expectedList));
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
		String id = "aaa";
		when(productRepository.findById(id)).thenReturn(Mono.empty());
		client
			.get()
			.uri("/{id}", id)
			.exchange()
			.expectStatus()
			.isNotFound();
	}

	@Test
	void testProductIdFound() {
		Product expectedProduct = expectedList.get(0);
		when(productRepository.findById(expectedProduct.getId())).thenReturn(Mono.just(expectedProduct));
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
