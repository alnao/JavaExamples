package it.alnao.servless.selectbolUser;

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
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFunction.class); //se @Slf4j non funzia
	
	public List<Clienti> selClienteByBollini(Filtro filtro){
		log.info( filtro.getTipoRicerca() + filtro.getBollini() );
		Map<String,AttributeValue> eav=new HashMap<String,AttributeValue>();
		String filtroTest="cards.bollini = :par1";
		if ("gt".equalsIgnoreCase(filtro.getTipoRicerca())){
			filtroTest="cards.bollini > :par1";
		}
		if ("lt".equalsIgnoreCase(filtro.getTipoRicerca())){
			filtroTest="cards.bollini < :par1";
		}
		eav.put(":par1", new AttributeValue().withN(filtro.getBollini() ));
		log.info( filtroTest + "------" + eav.get(":par1") );
		DynamoDBScanExpression scanExpression=new DynamoDBScanExpression()
				.withFilterExpression(filtroTest)
				.withExpressionAttributeValues(eav);
		return dynamoDBMapper.scan(Clienti.class, scanExpression);
	}
}
