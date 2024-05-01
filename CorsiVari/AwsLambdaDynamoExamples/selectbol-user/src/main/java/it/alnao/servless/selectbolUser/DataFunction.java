package it.alnao.servless.selectbolUser;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component //perchè componente generico
@Slf4j //log di lombok
public class DataFunction implements Function<Filtro,List<Clienti>>{//FunzioneLambda<IN,OUT>
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFunction.class); //se @Slf4j non funzia
	@Autowired
	private ClienteDao clienteDao;
	
	@Override
	public List<Clienti> apply(Filtro filtro) {
		log.info("DataFunction  " + filtro);
		return clienteDao.selClienteByBollini(filtro);
	} 
}
