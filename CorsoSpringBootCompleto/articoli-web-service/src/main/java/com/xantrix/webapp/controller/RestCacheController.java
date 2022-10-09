package com.xantrix.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestCacheController {
	@Autowired
	CacheManager cacheManager;
	
	@GetMapping("clearCache")
	public void clearCache() {
		cacheManager.getCache("articoli").clear();
		cacheManager.getCache("CacheArticolo").clear();
	}
}
