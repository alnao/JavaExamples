package it.alnao.servless.insertUser.hendler;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

import it.alnao.servless.insertUser.model.Clienti;
import it.alnao.servless.insertUser.model.Message;

@SuppressWarnings("deprecation")
public class DataFuncionHandler extends SpringBootRequestHandler<Clienti,Message>{
	
}
