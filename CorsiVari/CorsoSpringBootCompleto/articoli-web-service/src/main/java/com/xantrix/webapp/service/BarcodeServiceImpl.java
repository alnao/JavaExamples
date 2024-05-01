package com.xantrix.webapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.ArticoliRepository;
import com.xantrix.webapp.repository.BarCodeRepository;

@Service
@Transactional(readOnly = true)
public class BarcodeServiceImpl implements BarCodeService
{
	@Autowired
	BarCodeRepository barcodeRepository;

	@Override
	public Barcode selByBarcode(String barcode) 
	{
		return barcodeRepository.findByBarcode(barcode);
	}

}

