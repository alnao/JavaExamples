package it.alnao.gestUser;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UtentiRepository extends MongoRepository<Utenti,String>{
	public Utenti findByUserId(String userId);
}
