package com.khauminhduy.productapiannotation.controller;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.khauminhduy.productapiannotation.model.Product;
import com.khauminhduy.productapiannotation.model.ProductEvent;
import com.khauminhduy.productapiannotation.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {
	
	private ProductRepository productRepository;

	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@GetMapping
	public Flux<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<Product>> getProduct(@PathVariable("id") String id) {
		return productRepository.findById(id)
			.map(product -> ResponseEntity.ok(product))
			.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Product> saveProduct(@RequestBody Product product) {
		return productRepository.save(product);
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable("id") String id, @RequestBody Product product) {
		return productRepository.findById(id)
			.flatMap(existingProduct -> {
				existingProduct.setName(product.getName());
				existingProduct.setPrice(product.getPrice());
				return productRepository.save(existingProduct);
			})
			.map(updateProduct -> ResponseEntity.ok(updateProduct))
			.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") String id) {
		return productRepository.findById(id)
			.flatMap(existingProduct -> productRepository.delete(existingProduct).then(Mono.just(ResponseEntity.ok().<Void>build())))
			.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping
	public Mono<Void> deleteAllProduct() {
		return productRepository.deleteAll();
	}

	@GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ProductEvent> getProductEvent() {
		return Flux.interval(Duration.ofSeconds(1))
			.map(value -> new ProductEvent(value, "Product Event"));
	}

}
