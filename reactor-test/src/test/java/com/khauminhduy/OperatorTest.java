package com.khauminhduy;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;

public class OperatorTest {
	
	@Test
	void map() {
		Flux.range(1, 5)
				.map(i -> i * 10)
				.subscribe(System.out::println);
	}

}
