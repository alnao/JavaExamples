package com.xantrix.webapp.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;

public interface BarCodeService 
{
	public Barcode selByBarcode(String barcode);

}
