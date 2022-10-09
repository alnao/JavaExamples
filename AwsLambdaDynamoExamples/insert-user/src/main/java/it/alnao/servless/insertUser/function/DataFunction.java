package it.alnao.servless.insertUser.function;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.alnao.servless.insertUser.model.Clienti;
import it.alnao.servless.insertUser.model.Message;
import it.alnao.servless.insertUser.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;

@Component //perchè componente generico
@Slf4j //log di lombok
public class DataFunction implements Function<Clienti,Message>{//FunzioneLambda<IN,OUT>
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataFunction.class); //se @Slf4j non funzia
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Override
	public Message apply(Clienti cliente) {
		Message m=new Message();
		log.info("DataFunction insert-user " + cliente);
		if (cliente.getCodfid()=="") {
			m.setMessage("Cliente senza codFid");
		}else {
			m.setMessage("Cliente " + clienteRepository.insCliente(cliente) +" inserito");
		}
		return m;
	} 
	
}
