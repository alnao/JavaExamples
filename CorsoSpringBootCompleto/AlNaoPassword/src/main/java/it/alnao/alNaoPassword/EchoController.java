package it.alnao.alNaoPassword;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/echo")
//@CrossOrigin(origins = {"http://localhost:4200"})
public class EchoController {
	private static final Logger logger = LoggerFactory.getLogger(EchoController.class);
	
	@GetMapping(value="/echo")
	public ResponseEntity<?> echo(){
		logger.trace("Saluti echo method");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Echo OK");
		return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.CREATED);
	
	}
	
	@GetMapping(value="/saluti/{nome}")
	public ResponseEntity<?> echo(@PathVariable("nome") String nome){
		logger.trace("Saluti echo method with " + nome);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode responseNode = mapper.createObjectNode();
		responseNode.put("code", HttpStatus.OK.toString());
		responseNode.put("message", "Echo OK " + nome);
		return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.CREATED);
	
	}
}
