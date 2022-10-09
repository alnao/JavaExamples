package com.xantrix.servless.security;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public class Utenti  implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6380094058524826764L;
	private String userId;
	private String password;
	private String attivo;
	
	private List<String> ruoli;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAttivo() {
		return attivo;
	}

	public void setAttivo(String attivo) {
		this.attivo = attivo;
	}

	public List<String> getRuoli() {
		return ruoli;
	}

	public void setRuoli(List<String> ruoli) {
		this.ruoli = ruoli;
	}

	public Utenti(String userId, String password, String attivo, List<String> ruoli) {
		super();
		this.userId = userId;
		this.password = password;
		this.attivo = attivo;
		this.ruoli = ruoli;
	}
	public Utenti() {super();}
	
}
