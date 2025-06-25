package it.alnao.southAfricanMobileNumbers.controller;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import it.alnao.southAfricanMobileNumbers.entities.SouthAfricanMobileNumbersEntity;
import it.alnao.southAfricanMobileNumbers.repository.SouthAfricanMobileNumbersRepository;
import it.alnao.southAfricanMobileNumbers.service.SouthAfricanMobileNumbersService;
import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/southAfricanMobileNumbersController/")
@Slf4j //logger lombok 
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SouthAfricanMobileNumbersController {
	private static String internetionalPrefix="27";
	private static String regex=internetionalPrefix+"[0-9]{9}";//27831234567 is the correct format for this exercise
	
	@Autowired
	SouthAfricanMobileNumbersService service;
	
	@RequestMapping ( value ="/uploadFile" ,method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, 
				consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SouthAfricanMobileNumbersEntity>> uploadFile(
			@RequestBody List<SouthAfricanMobileNumbersEntity> numbers){
		//clear all data in mongo
		service.deleteAll();
		

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		//for every numbers to check if is valid
		Iterator<SouthAfricanMobileNumbersEntity> it=numbers.iterator();
		while( it.hasNext() ) {
			SouthAfricanMobileNumbersEntity number=it.next();
			//for every numbers to check if is valid
			if (Pattern.matches(regex,number.getPhoneNumber() )) {
				//Display acceptable numbers
				number.setType("OK");
			}else {
				//Corrected numbers + what was modified
				if (Pattern.matches(regex,internetionalPrefix+number.getPhoneNumber() )) {
					number.setType("OK INT PREFIX");
					number.setPhoneNumber( internetionalPrefix + number.getPhoneNumber());
				}else {
					//Incorrect numbers.
					number.setType("KO");
				}
			}
			if (! "TEST".equals(number.getIdNumber())) {
				service.save(number);
			}
		}/**/
		//numbers=service.all();
		return new ResponseEntity<List<SouthAfricanMobileNumbersEntity>>(numbers,HttpStatus.OK);
	}
	
	
	@RequestMapping ( value ="/getList" ,method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SouthAfricanMobileNumbersEntity>> getList(){
		List<SouthAfricanMobileNumbersEntity> c=service.all();
		return new ResponseEntity<List<SouthAfricanMobileNumbersEntity>>(c,HttpStatus.OK);
	}
}
