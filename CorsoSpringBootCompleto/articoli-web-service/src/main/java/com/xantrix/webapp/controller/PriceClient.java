package com.xantrix.webapp.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

//localhost:50513/api/util/info
@FeignClient(name="PriceArtService", url="localhost:50513/api/util")
public interface PriceClient {
	@GetMapping("/prezzo/{codArt}")
	public Double getPrezzo(
				@PathVariable("codArt") String Barcode, 
				@RequestHeader("Authorization") String authHeader);
	
}
