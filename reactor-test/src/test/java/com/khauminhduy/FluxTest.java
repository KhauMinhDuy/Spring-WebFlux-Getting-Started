package com.khauminhduy;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class FluxTest {
	
	@Test
	void firstFlux() {
		Flux.just("A", "B", "C")
				.log()
				.subscribe();
	}

	@Test
	void fluxFromIterable() {
		Flux.fromIterable(Arrays.asList("A", "B", "C"))
				.log()
				.subscribe();
	}

	@Test
	void fluxFromRange() {
		Flux.range(10, 5)
				.log()
				.subscribe();
	}

	@Test
	void fluxFromInterval() throws InterruptedException {
		Flux.interval(Duration.ofSeconds(1))
				.log()
				.take(2)
				.subscribe();
		Thread.sleep(5000);
	}

	@Test
	void fluxRequest() {
		Flux.range(1, 5)
				.log()
				// .subscribe(null, null, null, s -> s.request(3));
				.subscribe(new BaseSubscriber<Integer>() {
					@Override
					protected void hookOnSubscribe(Subscription s) {
						s.request(3);
					}
				});
	}

	@Test
	void fluxCustomSubscribe() {
		Flux.range(1, 5)
				.log()
				.subscribe(new BaseSubscriber<Integer>() {
					int elementsToProcess = 3;
					int counter = 0;

					@Override
					protected void hookOnSubscribe(Subscription subscription) {
						System.out.println("Subscribed");
						request(elementsToProcess);
					}

					@Override
					protected void hookOnNext(Integer value) {
						counter++;
						if(counter == elementsToProcess) {
							counter = 0;

							Random random = new Random();
							elementsToProcess = random.ints(1, 4).findFirst().getAsInt();
							request(elementsToProcess);
						}
					}

				});
	}

	@Test
	void fluxLimitRate() {
		Flux.range(1, 5)
				.log()
				.limitRate(3)
				.subscribe();
	}

}
