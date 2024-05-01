package it.alnao.servless.insertUser.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import it.alnao.servless.insertUser.model.Clienti;

@Repository
public class ClienteRepositoryImpl  implements ClienteRepository{
	
	@Autowired
	private DynamoDBMapper dynamoDBMapper; 
	
	@Override
	public Clienti insCliente(Clienti cliente) {
		dynamoDBMapper.save(cliente);
		return cliente;
	}
	
}
