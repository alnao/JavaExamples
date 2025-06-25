package it.alnao.southAfricanMobileNumbers.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


import it.alnao.southAfricanMobileNumbers.entities.SouthAfricanMobileNumbersEntity;

public interface SouthAfricanMobileNumbersRepository 
	extends MongoRepository<SouthAfricanMobileNumbersEntity, String> {

	//	public Mono<ClientiModel> findByNumber(String number);

}
