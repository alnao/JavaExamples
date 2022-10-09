package it.alnao.servless.selectcodeUser;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component //perchè componente generico
@Slf4j //log di lombok
public class DataFunction implements Function<String,Clienti>{//FunzioneLambda<IN,OUT>
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFunction.class); //se @Slf4j non funzia
	@Autowired
	private ClienteDao clienteDao;
	
	@Override
	public Clienti apply(String codFid) {
		log.info("DataFunction selClienteByCode " + codFid);
		return clienteDao.selClienteByCode(codFid);
	} 
	
}
