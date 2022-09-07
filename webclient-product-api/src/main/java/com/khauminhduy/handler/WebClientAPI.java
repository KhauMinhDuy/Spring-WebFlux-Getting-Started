package com.khauminhduy.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.khauminhduy.model.Product;
import com.khauminhduy.model.ProductEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class WebClientAPI {
	
	private WebClient webClient;

	public WebClientAPI() {
		webClient = WebClient.builder().baseUrl("http://localhost:8080/products").build();
	}

	private Mono<ResponseEntity<Product>> postNewProduct() {
		return webClient
			.post()
			.body(Mono.just(new Product(null, "Jasmine Tea", 1.99)), Product.class)
			.exchangeToMono(response -> response.toEntity(Product.class))
			.doOnSuccess(o -> System.out.println("***********POST " + o));
	}

	private Flux<Product> getAllProducts() {
		return webClient
			.get()
			.retrieve()
			.bodyToFlux(Product.class)
			.doOnNext(o -> System.out.println("************GET " + o));
	}

	private Mono<Product> updateProduct(String id, String name, double price) {
		return webClient
			.put()
			.uri("/{id}", id)
			.body(Mono.just(new Product(null, name, price)), Product.class)
			.retrieve()
			.bodyToMono(Product.class)
			.doOnSuccess(o -> System.out.println("***********UPDATE " + o));
	}

	private Mono<Void> deleteProduct(String id) {
		return webClient
			.delete()
			.uri("/{id}", id)
			.retrieve()
			.bodyToMono(Void.class)
			.doOnSuccess(o -> System.out.println("************DELETE " + o));
	}

	private Flux<ProductEvent> getAllEvents() {
		return webClient
			.get()
			.uri("/event")
			.retrieve()
			.bodyToFlux(ProductEvent.class);
	}

	public static void main(String[] args) {
		WebClientAPI api = new WebClientAPI();
		api
			.postNewProduct()
			.thenMany(api.getAllProducts())
			.take(1)
			.flatMap(e -> api.updateProduct(e.getId(), "White Tea", 0.99))
			.flatMap(e -> api.deleteProduct(e.getId()))
			.thenMany(api.getAllProducts())
			.thenMany(api.getAllEvents())
			.subscribeOn(Schedulers.newSingle("myThread"))
			.subscribe(System.out::println);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
		}

	}

}
