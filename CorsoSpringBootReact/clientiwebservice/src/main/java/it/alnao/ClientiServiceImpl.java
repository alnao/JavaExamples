package it.alnao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientiServiceImpl implements ClientiService{

	@Autowired
	ClientiRepository clienteRepository;
		
	@Override
	public Flux<ClientiModel> SelAll() {
		return clienteRepository.findAll();
	}

	@Override
	public Mono<ClientiModel> Salva(ClientiModel cliente) {
		return clienteRepository.save(cliente);
	}

	@Override
	public Mono<Void> Elimina(String id) {
		return clienteRepository.deleteById(id);
	}

	public Mono<ClientiModel> findByCodfid(String codFid){
		return clienteRepository.findByCodfid(codFid);
	}
	
	public Flux<ClientiModel> findByNominativoLike(String nominativo){
		return clienteRepository.findByNominativoLike(nominativo);
	}
	
	public Flux<ClientiModel> selByBollini(int bollini){
		return clienteRepository.selByBollini(bollini);
	}

	public Mono<Void> EliminaByCodFid(String codFid){
		return clienteRepository.findByCodfid(codFid)
				.filter(cliente -> cliente!=null)
				.flatMap(cliente-> clienteRepository.deleteById(cliente.getId()))
				.then();
		/*
		 ClientiModel cliente=new ClientiModel();
		 
		clienteRepository.findByCodfid(codFid).subscribe(//asincrono con substribe
				p-> cliente.setId(p.getId())//-> freccia come in javascript  
		);
		try {	Thread.sleep(500);  }catch(Exception e) {	}
		return clienteRepository.deleteById(cliente.getId());
		*/
	}
}
