package com.xantrix.webapp.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;


import lombok.Data;

@Entity
@Table(name = "IVA")
@Data
public class Iva
{
	@Id
	@Column(name = "IDIVA")
	private int idIva;
	
	@Column(name = "DESCRIZIONE")
	private String descrizione;
	
	@Column(name = "ALIQUOTA")
	private int aliquota;
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "iva") //manca il parametro cascade perchè l'accesso sarà di sola lettura
	@JsonBackReference
	private Set<Articoli> articoli = new HashSet<>();


	public int getIdIva() {
		return idIva;
	}


	public void setIdIva(int idIva) {
		this.idIva = idIva;
	}


	public String getDescrizione() {
		return descrizione;
	}


	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public Set<Articoli> getArticoli() {
		return articoli;
	}


	public void setArticoli(Set<Articoli> articoli) {
		this.articoli = articoli;
	}
	
	
}
