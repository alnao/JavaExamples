package com.xantrix.webapp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.xantrix.webapp.entities.Articoli;

public interface ArticoliService 
{
	public List<Articoli> SelByDescrizione(String descrizione);
	
	public Articoli SelByCodArt(String codArt);
	
	public Articoli SelByBarcode(String barcode);
	
	public void DelArticolo(Articoli articolo);
	
	public void InsArticolo(Articoli articolo);
	
	public Iterable<Articoli> selTutti();
	
	public List<Articoli> selByDescrizione(String descrizione);
	
	public List<Articoli> selByDescrizione(String descrizione,Pageable pageable);

}
