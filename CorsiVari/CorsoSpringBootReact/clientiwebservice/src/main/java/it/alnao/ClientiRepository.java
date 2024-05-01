package it.alnao;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientiRepository 
	extends ReactiveMongoRepository<ClientiModel,String>{//String perchè chiave primaria di MongoDB sempre

	public Mono<ClientiModel> findByCodfid(String codfid);
	
	public Flux<ClientiModel> findByNominativoLike(String nominativo);
	
	//con find è JPA, mentre sel convenzione sua per @Query
	@Query("{ 'cars.bollini':{$gt : ?0} }") //dove ?0 è il primo parametro, ?1 sarà il secondo, ?2 il terzo
	public Flux<ClientiModel> selByBollini(int bollini);
	
}
//nota: classe ReactiveCrudRepository generico per io NoSQL