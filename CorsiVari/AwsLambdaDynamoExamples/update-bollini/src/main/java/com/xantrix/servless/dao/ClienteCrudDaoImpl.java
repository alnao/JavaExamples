package com.xantrix.servless.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.xantrix.servless.model.Cliente;
 

@Repository
public class ClienteCrudDaoImpl implements ClienteCrudDao
{
	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	@Override
	public Cliente SelClienteByCode(String CodFid) 
	{
		Cliente cliente = dynamoDBMapper.load(Cliente.class, CodFid);
		
		return cliente;
	}
	
	@Override
	public void insCliente(Cliente cliente)
	{
		dynamoDBMapper.save(cliente);
	}

}
