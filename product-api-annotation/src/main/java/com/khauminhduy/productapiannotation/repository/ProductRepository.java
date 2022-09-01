package com.khauminhduy.productapiannotation.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.khauminhduy.productapiannotation.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
	
	// Flux<Product> findByName(Publisher<String> name);

}
