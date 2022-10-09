package it.alnao.prezzi;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;




@RequestMapping("/api/util")
@RestController
public class InfoController{
	private static final Logger logger = LoggerFactory.getLogger(InfoController.class);
	
	@Autowired
	private AppConfig configuration;
	
	@GetMapping("/echo")
	public String echo() {
		return "ECHO OK";
	}
	
	@GetMapping("/info")
	public Map<String,String> getInfo(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("prezzo",configuration.getPrezzo());
		return map;
	}
	
	@GetMapping("/prezzo/{codArt}")
	public Double getPrezzo(@PathVariable("codArt") String codArt){
		//long v= 0;//Math.round( Math.random() * 100);
		logger.info("prezzo fisso ="+ configuration.getPrezzo());
		Long v=null;
		try {
			v=Long.valueOf(  configuration.getPrezzo() );
		}catch (Exception e) {
			// TODO: handle exception
			v = Math.round( Math.random() * 100);
		}
		logger.info("prezzo="+ codArt + "=" +v);
		return Double.valueOf( v  );
	}
}
