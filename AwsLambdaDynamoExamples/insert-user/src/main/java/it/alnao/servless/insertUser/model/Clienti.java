package it.alnao.servless.insertUser.model;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName="CorsoLambdaClienti")
public class Clienti implements Serializable {
	private static final long serialVersionUID = 731426089588933520L;
	//@DynamoDBAutoGeneratedKey
	@DynamoDBHashKey(attributeName = "userId")
	private String codfid;

	@DynamoDBAttribute(attributeName = "nominativo")
	private String nominativo;
	
	@DynamoDBAttribute(attributeName = "attivo")
	private boolean attivo;
	
	@DynamoDBAttribute(attributeName = "dataCreazione")
	private String dataCreazione;	
	
	@DynamoDBAttribute(attributeName = "cards")
	private Cards cards;

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

	public boolean isAttivo() {
		return attivo;
	}

	public void setAttivo(boolean attivo) {
		this.attivo = attivo;
	}

	public String getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(String dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Cards getCards() {
		return cards;
	}

	public void setCards(Cards cards) {
		this.cards = cards;
	}

	public Clienti(String codfid, String nominativo, boolean attivo, String dataCreazione, Cards cards) {
		super();
		this.codfid = codfid;
		this.nominativo = nominativo;
		this.attivo = attivo;
		this.dataCreazione = dataCreazione;
		this.cards = cards;
	}

	public Clienti() {
		super();
	}
	
	
}
