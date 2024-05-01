package it.alnao;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document //riferimento al Mongo
@Data //lombok getter e setter
@AllArgsConstructor //lombok
@NoArgsConstructor  //lombok
public class ClientiModel implements Serializable{
	private static final long serialVersionUID = -2166616184165494673L;
	@Id //id del mongo in automatico
	private String id;
	@Field("codfid")
	private String codfid;
	@Field("nominativo")
	private String nominativo;
	@Field("indirizzo")
	private String indirizzo;
	@Field("comune")
	private String comune;
	@Field("cap")
	private String cap;
	@Field("prov")
	private String prov;
	@Field("telefono")
	private String telefono;
	@Field("mail")
	private String mail;
	@Field("attivo")
	private boolean attivo;
	@Field("datacreazione")
	private Date datacreazione = new Date();
	@Field("cards")
	private ClientiCardModel cards;

	public ClientiModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodfid() {
		return codfid;
	}
	public void setCodfid(String codfid) {
		this.codfid = codfid;
	}
	public String getNominativo() {
		return nominativo;
	}
	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getProv() {
		return prov;
	}
	public void setProv(String prov) {
		this.prov = prov;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public boolean isAttivo() {
		return attivo;
	}
	public void setAttivo(boolean attivo) {
		this.attivo = attivo;
	}
	public Date getDatacreazione() {
		return datacreazione;
	}
	public void setDatacreazione(Date datacreazione) {
		this.datacreazione = datacreazione;
	}
	public ClientiCardModel getCards() {
		return cards;
	}
	public void setCards(ClientiCardModel cards) {
		this.cards = cards;
	}

	
}
