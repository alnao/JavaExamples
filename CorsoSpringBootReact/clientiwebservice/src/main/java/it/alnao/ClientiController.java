package it.alnao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jdk.internal.jline.internal.Log;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clienti/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
//@Slf4j //logger lombok
public class ClientiController {
	@Autowired
	ClientiService clientiService;
	
	@PostMapping ( value ="/inserisci" , produces = "application/json")
	public ResponseEntity<Mono<ClientiModel>> insertCli(@RequestBody ClientiModel newCliente){
		//Log.info("ClientiController.insertCli *****");
		Mono<ClientiModel> c=clientiService.Salva(newCliente);
		return new ResponseEntity<Mono<ClientiModel>>(c,HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/cerca/all" , method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<Flux<ClientiModel>> listAllCli(){
		Flux<ClientiModel> clienti=clientiService.SelAll()
				.switchIfEmpty(Mono.error(new ResponseStatusException( HttpStatus.NOT_FOUND,"Nessun cliente trovato" )));
				return new ResponseEntity<Flux<ClientiModel>>(clienti,HttpStatus.OK);
	} 

	@DeleteMapping(value="/elimina/{id}" , produces = "application/json")
	public ResponseEntity<Mono<Void>> deleteCli( @PathVariable("id") String id ){
		return new ResponseEntity<Mono<Void>>(clientiService.Elimina(id),HttpStatus.OK); 
	}
		
	@RequestMapping(value="/findByCodFid/{codFid}" , method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<Mono<ClientiModel>> findByCodFid(@PathVariable("codFid") String codFid ){
		Mono<ClientiModel> clienti=clientiService.findByCodfid(codFid) 
				.switchIfEmpty(Mono.error(new ResponseStatusException( HttpStatus.NOT_FOUND,"Nessun cliente trovato" )));
				return new ResponseEntity<Mono<ClientiModel>>(clienti,HttpStatus.OK);
	} 
	@RequestMapping(value="/findByNominativoLike/{nominativo}" , method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<Flux<ClientiModel>> findByNominativoLike(@PathVariable("nominativo") String nominativo ){
		Flux<ClientiModel> clienti=clientiService.findByNominativoLike(nominativo) 
				.switchIfEmpty(Mono.error(new ResponseStatusException( HttpStatus.NOT_FOUND,"Nessun cliente trovato" )));
				return new ResponseEntity<Flux<ClientiModel>>(clienti,HttpStatus.OK);
	} 
	@RequestMapping(value="/selByBollini/{bollini}" , method = RequestMethod.GET , produces = "application/json")
	public ResponseEntity<Flux<ClientiModel>> selByBollini(@PathVariable("bollini") int bollini ){
		Flux<ClientiModel> clienti=clientiService.selByBollini(bollini) 
				.switchIfEmpty(Mono.error(new ResponseStatusException( HttpStatus.NOT_FOUND,"Nessun cliente trovato" )));
				return new ResponseEntity<Flux<ClientiModel>>(clienti,HttpStatus.OK);
	} 
	
	@DeleteMapping(value="/eliminaCodFid/{codFid}" , produces = "application/json")
	public ResponseEntity<Mono<Void>> deleteCliCodFid( @PathVariable("codFid") String codFid){
		Mono<ClientiModel> clienti=clientiService.findByCodfid(codFid) 
				.switchIfEmpty(Mono.error(new ResponseStatusException( HttpStatus.NOT_FOUND,"Nessun cliente trovato" )));
		return new ResponseEntity<Mono<Void>>(clientiService.EliminaByCodFid(codFid),HttpStatus.OK); 
	}
		
	
}
