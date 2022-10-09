package it.alnao.reactcorso;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import it.alnao.reactcorso.controller.SalutiController;

@SpringBootTest
class ReactcorsoApplicationTests {

	
	@Test
	void A_contextLoads() {
		WebTestClient testClient = WebTestClient.bindToController(
				new SalutiController()
			).build();

		testClient.get().uri("/api/saluti/echo")
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$").isNotEmpty()
		.jsonPath("$").isEqualTo("Echo, rest service");
	}
	
	@Test
	void B_ontextLoads() {
		WebTestClient testClient = WebTestClient.bindToController(
				new SalutiController()
			).build();

		testClient.get().uri("/api/saluti/echo/alnao")
		.exchange()
		.expectStatus().isOk()
		.expectBody()
		.jsonPath("$").isNotEmpty()
		.jsonPath("$").isEqualTo("Salve alnao");
	}

}
