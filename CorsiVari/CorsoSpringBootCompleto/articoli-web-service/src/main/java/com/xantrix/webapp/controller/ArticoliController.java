package com.xantrix.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exception.BindingException;
import com.xantrix.webapp.exception.DuplicateException;
import com.xantrix.webapp.exception.NotFoundException;
import com.xantrix.webapp.service.ArticoliService;
import com.xantrix.webapp.service.BarCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(value="alphashop", tags="controller operazioni dei dati")
@RestController
@RequestMapping("/api/articoli")
@CrossOrigin(origins = {"http://localhost:4200"})
public class ArticoliController
{
	private static final Logger logger = LoggerFactory.getLogger(ArticoliController.class);
	@Autowired
	private PriceClient priceClient;
	@Autowired
	private ArticoliService articoliService;
	@Autowired
	private BarCodeService barcodeService;
	
	@GetMapping(value="/echo")
	public ResponseEntity<?> echo(){
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Echo OK");
		return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.CREATED);
	
	}
	
	
	// ------------------- Ricerca Per Barcode ------------------------------------
	@ApiOperation(value = "Ricerca per articolo"
			,notes="Ritorna l'elenco degli articoli"
			,response=Articoli.class
			,produces="application/json"
			)
	@RequestMapping(value = "/cerca/ean/{barcode}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Articoli> listArtByEan(@ApiParam("Barcode univoco dell'articolo") @PathVariable("barcode") String Barcode,HttpServletRequest httpRequest)
		throws NotFoundException	 
	{
		logger.info("****** Otteniamo l'articolo con barcode " + Barcode + " *******");
		
		Articoli articolo;
		com.xantrix.webapp.entities.Barcode ean=barcodeService.selByBarcode(Barcode);
		if (ean==null) {
			String ErrMsg = String.format("Il barcode %s non è stato trovato!", Barcode);
			logger.warn(ErrMsg);
			throw new NotFoundException(ErrMsg);			
		}else {
			articolo=ean.getArticolo();
		}
		/*Articoli articolo = articoliService.SelByBarcode(Barcode);
		
		if (articolo == null)
		{
			String ErrMsg = String.format("Il barcode %s non è stato trovato!", Barcode);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		*/
		String authHeader=httpRequest.getHeader("Authorization");
		articolo.setPrezzo( priceClient.getPrezzo(articolo.getCodArt(),authHeader) );;//header se serve
		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
	}
	
	// ------------------- Ricerca Per Codice ------------------------------------
	@RequestMapping(value = "/cerca/codice/{codart}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Articoli> listArtByCodArt(@ApiParam("Codice univoco dell'articolo") @PathVariable("codart") String CodArt , HttpServletRequest httpRequest)  
			throws NotFoundException
	{
		String authHeader=httpRequest.getHeader("Authorization");
		//httpRequest
		
		logger.info("****** Otteniamo l'articolo con codice " + CodArt + " *******");

		Articoli articolo = articoliService.SelByCodArt(CodArt);

		if (articolo == null)
		{
			String ErrMsg = String.format("L'articolo con codice %s non è stato trovato!", CodArt);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
		}
		articolo.setPrezzo( priceClient.getPrezzo(articolo.getCodArt(),authHeader) );;//header se serve
		return new ResponseEntity<Articoli>(articolo, HttpStatus.OK);
	}


	
	// ------------------- Ricerca Per Descrizione ------------------------------------
	@RequestMapping(value = "/cerca/descrizione/{filter}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Articoli>> listArtByDesc(@ApiParam("Filtro di ricerca") @PathVariable("filter") String Filter, HttpServletRequest httpRequest)
			throws NotFoundException
	{
		logger.info("****** Otteniamo gli articoli con Descrizione: " + Filter + " *******");

		List<Articoli> articoli = articoliService.SelByDescrizione(Filter + "%");
		
		if (articoli.size() == 0)
		{
			String ErrMsg = String.format("Non è stato trovato alcun articolo avente descrizione %s", Filter);
			
			logger.warn(ErrMsg);
			
			throw new NotFoundException(ErrMsg);
			
		}
		String authHeader=httpRequest.getHeader("Authorization");
		articoli.forEach(f -> f.setPrezzo(priceClient.getPrezzo(f.getCodArt(),authHeader)));

		return new ResponseEntity<List<Articoli>>(articoli, HttpStatus.OK);
	}
	@Autowired 
	private ResourceBundleMessageSource errMessage;//prende il bundle
	
	@PostMapping(value="/inserisci")
	public ResponseEntity<?> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult) throws BindingException, DuplicateException{
		logger.info( "Salvo il" + articolo.getCodArt());
		if (bindingResult.hasErrors()) {//errore di validazione
			String err=errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			logger.warn(err);
			throw new BindingException(err);
		}
		Articoli a = articoliService.SelByCodArt(articolo.getCodArt());
		if (a != null) {//se presente in anagrafica
			String err="Articolo già presente in anagrafica";
			logger.warn(err);
			throw new DuplicateException(err);
		}
		articoliService.InsArticolo(articolo);
		//return new ResponseEntity<Articoli>(articolo, HttpStatus.CREATED);
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Inserimento Articolo " + articolo.getCodArt() + " Eseguita Con Successo");
		return new ResponseEntity<>(responseNode, headers, HttpStatus.CREATED);
	}
	
	
	@RequestMapping(value="/modifica", method = RequestMethod.PUT)
	public ResponseEntity<?> updateArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult) throws BindingException, DuplicateException{
		logger.info( "Salvo il" + articolo.getCodArt());
		if (bindingResult.hasErrors()) {//errore di validazione
			String err=errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
			logger.warn(err);
			throw new BindingException(err);
		}
		Articoli articoloOld = articoliService.SelByCodArt(articolo.getCodArt());
		if (articoloOld == null) {//se NON presente in anagrafica
			String err="Articolo non presente";
			logger.warn(err);
			throw new DuplicateException(err);
		}
		//articoliService.DelArticolo(articoloOld);
		articoliService.InsArticolo(articolo);
		//return new ResponseEntity<Articoli>(articolo, HttpStatus.CREATED);
		
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Modifica Articolo " + articolo.getCodArt() + " Eseguita Con Successo");
		return new ResponseEntity<>(responseNode, headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value="/elimina/{codArt}", method = RequestMethod.DELETE, produces="application/json")
	public ResponseEntity<?> deleteArt(@PathVariable("codArt") String codArt) throws BindingException, DuplicateException{	
		logger.info( "Cancello il" + codArt);
		Articoli articolo = articoliService.SelByCodArt(codArt);
		if (articolo == null) {//se NON presente in anagrafica
			String err="Articolo non presente";
			logger.warn(err);
			throw new DuplicateException(err);
		}
		articoliService.DelArticolo(articolo);
		//boh... non ho capito perchè fa così e non in semplice ok di ritorno
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Eliminazione Articolo " + codArt + " Eseguita Con Successo");
		return new ResponseEntity<>(responseNode, headers, HttpStatus.OK);
	}
}
