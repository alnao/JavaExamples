package it.alnao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientiService {
	
	public Flux<ClientiModel> SelAll();
	
	public Mono<ClientiModel> Salva(ClientiModel cliente);
	
	public Mono<Void> Elimina(String id);
	
	public Mono<ClientiModel> findByCodfid(String codfid);
	
	public Flux<ClientiModel> findByNominativoLike(String nominativo);
	
	public Flux<ClientiModel> selByBollini(int bollini);
	
	public Mono<Void> EliminaByCodFid(String codFid);
	
}
