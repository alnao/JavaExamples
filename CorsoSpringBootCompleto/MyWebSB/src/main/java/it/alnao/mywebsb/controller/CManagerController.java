package it.alnao.mywebsb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



import io.swagger.annotations.ApiParam;
import it.alnao.mywebsb.entities.Password;
import it.alnao.mywebsb.exception.NotFoundException;
import it.alnao.mywebsb.service.CManagerService;

@RestController
@RequestMapping("/api/contenuti")
public class CManagerController {
	private static final Logger logger = LoggerFactory.getLogger(CManagerController.class);

	@Autowired
	private CManagerService contenutiService;
	
	// ------------------- Ricerca Per Barcode ------------------------------------
	@RequestMapping(value = "/elencoPassword", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Password>> getListaPassword(/*@ApiParam("Barcode univoco dell'articolo") @PathVariable("barcode") String Barcode*/)
		throws NotFoundException{
		logger.info("****** Otteniamo elencoPassword *******");
		List<Password> lista = contenutiService.getListPassword();
		
		if (lista == null){
			String ErrMsg = String.format("Nessuna password trovata");
			logger.warn(ErrMsg);
			throw new NotFoundException(ErrMsg);
		}
		return new ResponseEntity<List<Password>>(lista, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/firstPassword", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Password> getFirstPassword(/*@ApiParam("Barcode univoco dell'articolo") @PathVariable("barcode") String Barcode*/)
		throws NotFoundException{
		logger.info("****** Otteniamo elencoPassword *******");
		Password p = contenutiService.getFirstPassword();
		
		if (p == null){
			String ErrMsg = String.format("Nessuna password trovata");
			logger.warn(ErrMsg);
			throw new NotFoundException(ErrMsg);
		}
		logger.info("****** Otteniamo elencoPassword *******" + p.getNome());
		return new ResponseEntity<Password>(p, HttpStatus.OK);
	}
}
