package it.alnao.mywebsb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "Cmanager_Password")
@Data
public class Password implements Serializable{
	private static final long serialVersionUID = 7361753083273455478L;
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "nome")
	private String nome;
	
	@Column(name = "user")
	private String user;	
	
	@Column(name = "descrizione")
	private String descrizione;	
	
	@Column(name = "ordine")
	private int ordine;	
	
	@Column(name = "valore")
	private String valore;

	
	public Password() {
		super();
		// TODO Auto-generated constructor stub
	}	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public int getOrdine() {
		return ordine;
	}

	public void setOrdine(int ordine) {
		this.ordine = ordine;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}


	
}
