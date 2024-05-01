package com.xantrix.webapp.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "ARTICOLI")
@Data
public class Articoli  implements Serializable
{
	@Transient
	private Double prezzo;
	
	public Double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(Double prezzo) {
		this.prezzo = prezzo;
	}
	
	private static final long serialVersionUID = 7361753083273455478L;
	
	@Id
	@Column(name = "CODART")
	@Size(min = 5, max = 20, message = "{Size.Articoli.codArt.Validation}")
	@NotNull(message = "{NotNull.Articoli.codArt.Validation}")
	@ApiModelProperty(notes="Il codice univoco dell'articolo")
	private String codArt;
	
	@Column(name = "DESCRIZIONE")
	@Size(min = 6, max = 80, message = "{Size.Articoli.descrizione.Validation}")
	private String descrizione;	
	
	@Column(name = "UM")
	private String um;
	
	@Column(name = "CODSTAT")
	private String codStat;
	
	@Column(name = "PZCART")
	@Max(value = 99, message = "{Max.Articoli.pzCart.Validation}")
	private Integer pzCart;
	
	@Column(name = "PESONETTO")
	@Min(value = (long) 0.01, message = "{Min.Articoli.pesoNetto.Validation}")
	private double pesoNetto;
	
	@Column(name = "IDSTATOART")
	@NotNull(message= "{NotNull.Articoli.idStatoArt.Validation}") 
	private String idStatoArt;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATACREAZIONE")
	private Date dataCreaz;
	
	public Articoli() {}
	
	public Articoli(String CodArt, String Descrizione, Integer PzCart, Double PesoNetto, String IdStatoArt)
	{
		this.codArt = CodArt;
		this.descrizione = Descrizione;
		this.pzCart = PzCart;
		this.pesoNetto = PesoNetto;
		this.idStatoArt = IdStatoArt;
	}
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "articolo", orphanRemoval = true)
	@JsonManagedReference
	private Set<Barcode> barcode = new HashSet<>();
	
	@OneToOne(mappedBy = "articolo", cascade = CascadeType.ALL, orphanRemoval = true)
	private Ingredienti ingredienti;
	
	@ManyToOne
	@JoinColumn(name = "IDFAMASS", referencedColumnName = "ID")
	private FamAssort famAssort;
	
	@ManyToOne
	@JoinColumn(name = "IDIVA", referencedColumnName = "idIva")
	private Iva iva;

	public String getCodArt() {
		return codArt;
	}

	public void setCodArt(String codArt) {
		this.codArt = codArt;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getUm() {
		return um;
	}

	public void setUm(String um) {
		this.um = um;
	}

	public String getCodStat() {
		return codStat;
	}

	public void setCodStat(String codStat) {
		this.codStat = codStat;
	}

	public Integer getPzCart() {
		return pzCart;
	}

	public void setPzCart(Integer pzCart) {
		this.pzCart = pzCart;
	}

	public double getPesoNetto() {
		return pesoNetto;
	}

	public void setPesoNetto(double pesoNetto) {
		this.pesoNetto = pesoNetto;
	}

	public String getIdStatoArt() {
		return idStatoArt;
	}

	public void setIdStatoArt(String idStatoArt) {
		this.idStatoArt = idStatoArt;
	}

	public Date getDataCreaz() {
		return dataCreaz;
	}

	public void setDataCreaz(Date dataCreaz) {
		this.dataCreaz = dataCreaz;
	}

	public Set<Barcode> getBarcode() {
		return barcode;
	}

	public void setBarcode(Set<Barcode> barcode) {
		this.barcode = barcode;
	}

	public Ingredienti getIngredienti() {
		return ingredienti;
	}

	public void setIngredienti(Ingredienti ingredienti) {
		this.ingredienti = ingredienti;
	}

	public FamAssort getFamAssort() {
		return famAssort;
	}

	public void setFamAssort(FamAssort famAssort) {
		this.famAssort = famAssort;
	}

	public Iva getIva() {
		return iva;
	}

	public void setIva(Iva iva) {
		this.iva = iva;
	}

}
