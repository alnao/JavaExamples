package com.xantrix.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames= {"CacheArticolo"} )
public class ArticoliServiceImpl implements ArticoliService
{
	@Autowired
	ArticoliRepository articoliRepository;

	@Override
	@Cacheable
	public List<Articoli> SelByDescrizione(String descrizione) 
	{
		return articoliRepository.findByDescrizioneLike(descrizione.toUpperCase());
	}

	@Override
	@Cacheable(value="CacheArticolo", key="#codArt", sync=true)
	public Articoli SelByCodArt(String codArt) 
	{
		 
		return articoliRepository.findByCodArt(codArt);
	}

	@Override
	@Cacheable
	public Articoli SelByBarcode(String barcode) 
	{
		
		return articoliRepository.SelByEan(barcode);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames="articoli", allEntries=true),
			@CacheEvict(cacheNames="CacheArticolo", key="#articolo.codArt"),
		})
	public void DelArticolo(Articoli articolo) 
	{
		articoliRepository.delete(articolo);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(cacheNames="articoli", allEntries=true),
		@CacheEvict(cacheNames="CacheArticolo", key="#articolo.codArt"),
	})
	public void InsArticolo(Articoli articolo) 
	{
		articoliRepository.save(articolo);
	}
	@Override
	@Transactional
	public Iterable<Articoli> selTutti(){
		return articoliRepository.findAll();
	}
	
	public List<Articoli> selByDescrizione(String descrizione){
		return articoliRepository.selByDescrizioneLike(descrizione);
	}
	
	public List<Articoli> selByDescrizione(String descrizione,Pageable pageable){
		return articoliRepository.findByDescrizioneLike(descrizione, pageable);
	}

	
}

