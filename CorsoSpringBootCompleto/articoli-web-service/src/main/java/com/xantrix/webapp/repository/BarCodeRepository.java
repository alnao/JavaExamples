package com.xantrix.webapp.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;

public interface BarCodeRepository extends JpaRepository<Barcode,String>
{
	Barcode findByBarcode(String barcode);

} 
