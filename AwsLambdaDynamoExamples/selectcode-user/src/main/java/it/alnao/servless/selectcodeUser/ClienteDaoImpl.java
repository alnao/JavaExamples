package it.alnao.servless.selectcodeUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Repository
public class ClienteDaoImpl implements ClienteDao {
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	
	public Clienti selClienteByCode(String codFid) {
		return dynamoDBMapper.load(Clienti.class,codFid);
	}

}
