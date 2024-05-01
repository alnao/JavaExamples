package it.alnao.reactcorso.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saluti")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SalutiController {
	
	@GetMapping("/echo")
	public String getSaluto() {
		return "Echo, rest service";
	}
	
	@RequestMapping(value="/echo/{nome}", method=RequestMethod.GET, produces="application/json" )
	public String getSalutoConNome(@PathVariable("nome") String nome) {
		if ("alnao2".equals(nome))
			throw new RuntimeException("Alnao proprio bello");
		return "Salve " + nome ;
	}
}
