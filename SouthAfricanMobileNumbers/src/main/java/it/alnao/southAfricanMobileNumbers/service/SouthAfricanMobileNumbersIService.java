package it.alnao.southAfricanMobileNumbers.service;

import java.util.List;

import it.alnao.southAfricanMobileNumbers.entities.SouthAfricanMobileNumbersEntity;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;

public interface SouthAfricanMobileNumbersIService {
	
	public List<SouthAfricanMobileNumbersEntity> all();

	public void save(SouthAfricanMobileNumbersEntity number);

	public void delete(String id);
	
	public void deleteAll();
}
