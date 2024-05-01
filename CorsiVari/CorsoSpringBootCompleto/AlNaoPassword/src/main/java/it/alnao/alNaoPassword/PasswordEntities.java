package it.alnao.alNaoPassword;

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


@Entity
@Table(name = "cmanager_password")
public class PasswordEntities  implements Serializable{
	//@Transient
	//private Double prezzo;
	
	private static final long serialVersionUID = 7361753083273455478L;
	
	@Id
	@Column(name = "ID")
	private String codArt;
	
	@Column(name = "NOME")
	private String nome;	
	
	@Column(name = "UTENTE")
	private String utente;
	
	@Column(name = "VALORE")
	private String valore;
	
	@Column(name = "DESCRIZIONE")
	private String descrizione;
	
	@Column(name = "ORDINE")
	private Integer ordine;
	
	//@Column(name = "UPDAT")
	//private String updat;
	
	public PasswordEntities() {}

	public String getCodArt() {
		return codArt;
	}

	public void setCodArt(String codArt) {
		this.codArt = codArt;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getOrdine() {
		return ordine;
	}

	public void setOrdine(Integer ordine) {
		this.ordine = ordine;
	}



}
