package it.alnao.servless.selectnameUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class ClienteDaoImpl implements ClienteDao {
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	@Override
	public List<Clienti> selClienteByName(String name) {
		Map<String,AttributeValue> eav=new HashMap<String,AttributeValue>();
		eav.put(":par1", new AttributeValue().withS(name)); //vedere documentazione withS = con stringa
		DynamoDBScanExpression scanExpression=new DynamoDBScanExpression()
				.withFilterExpression("contains(nominativo,:par1)")
				.withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(Clienti.class, scanExpression);
	}

}
