package it.alnao;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

//@SpringBootTest
@WebFluxTest(ClientiController.class)
@TestMethodOrder(OrderAnnotation.class)
class ClientiwebserviceApplicationTests {
	@Autowired
	WebTestClient webTestClient;
	@MockBean
	private ClientiService clientiService;

	@Test
	@Order(1)
	void testInsertClient() {
		ClientiModel cliente=createClienti();
		Mono<ClientiModel> monoCliente = Mono.just(cliente);
		when( //nota: il when è un metodo dei org.mockito.Mockito.when;
				clientiService.Salva(cliente) 
			).thenReturn(monoCliente);//cioè è asincono
		webTestClient.post().uri("/api/clienti/inserisci")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(monoCliente,ClientiModel.class)
			.exchange()
			.expectStatus().isCreated();
	}

	
	
	
	private ClientiModel createClienti() {
		ClientiModel cliente=new ClientiModel();
		cliente.setCodfid("6500000");
		cliente.setNominativo("Mario Rossi");
		cliente.setIndirizzo("via U. Foscolo");
		cliente.setComune("Padova");
		cliente.setCap("35100");
		cliente.setProv("PD");
		cliente.setAttivo(true);
		ClientiCardModel card=new ClientiCardModel();
		card.setBollini(42);
		card.setUltimaspesa("05/06/2020");
		cliente.setCards(card);
		return cliente;
	}
	
}
