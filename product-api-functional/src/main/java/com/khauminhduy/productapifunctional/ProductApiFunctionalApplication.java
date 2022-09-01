package com.khauminhduy.productapifunctional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.khauminhduy.productapifunctional.handler.ProductHandler;
import com.khauminhduy.productapifunctional.model.Product;
import com.khauminhduy.productapifunctional.repository.ProductRepository;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class ProductApiFunctionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiFunctionalApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository productRepository) {
		return args -> {
			Flux<Product> fluxProducts = Flux.just(
				new Product(null, "Big Latte", 2.99),
				new Product(null, "Big Decaf", 2.49),
				new Product(null, "Green Tea", 1.99)
			).flatMap(productRepository::save);
			fluxProducts
				.thenMany(productRepository.findAll())
				.subscribe(System.out::println);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routers(ProductHandler productHandler) {
		return RouterFunctions.route()
			.GET("/products/event", accept(TEXT_EVENT_STREAM), productHandler::getProductEvents)
			.GET("/products/{id}", accept(APPLICATION_JSON), productHandler::getProduct)
			.GET("/products", accept(APPLICATION_JSON), productHandler::getAllProducts)
			.PUT("/products/{id}", accept(APPLICATION_JSON), productHandler::updateProduct)
			.DELETE("/products/{id}", accept(APPLICATION_JSON), productHandler::deleteProduct)
			.DELETE("/products", accept(APPLICATION_JSON), productHandler::deleteAllProduct)
			.build();
	}

}
