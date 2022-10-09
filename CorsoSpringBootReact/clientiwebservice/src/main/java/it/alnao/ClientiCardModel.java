package it.alnao;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class ClientiCardModel implements Serializable {
	private static final long serialVersionUID = 7907254493701870809L;
	
	@Field("bollini")
	private int bollini;
	
	@Field("ultimaspesa")
	private String ultimaspesa;

	public int getBollini() {
		return bollini;
	}

	public void setBollini(int bollini) {
		this.bollini = bollini;
	}

	public String getUltimaspesa() {
		return ultimaspesa;
	}

	public void setUltimaspesa(String ultimaspesa) {
		this.ultimaspesa = ultimaspesa;
	}

	public ClientiCardModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
