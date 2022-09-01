package com.khauminhduy.productapifunctional.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.khauminhduy.productapifunctional.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
	
	

}
