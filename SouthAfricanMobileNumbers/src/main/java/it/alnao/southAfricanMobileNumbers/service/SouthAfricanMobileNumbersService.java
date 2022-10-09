package it.alnao.southAfricanMobileNumbers.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.alnao.southAfricanMobileNumbers.entities.SouthAfricanMobileNumbersEntity;
import it.alnao.southAfricanMobileNumbers.repository.SouthAfricanMobileNumbersRepository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;

@Service
public class SouthAfricanMobileNumbersService implements SouthAfricanMobileNumbersIService {
	
	@Autowired
	SouthAfricanMobileNumbersRepository repository;
	
	@Override
	public List<SouthAfricanMobileNumbersEntity> all(){
		return repository.findAll();
	}
	@Override
	public void save(SouthAfricanMobileNumbersEntity number){
		repository.save(number);
	}
	@Override
	public void delete(String id){
		repository.deleteById(id);
	}
	@Override
	public void deleteAll(){
		repository.deleteAll();
	}
}
